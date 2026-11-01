package iqan.cenorudoaddons.client.feature.impl.fishing;

import iqan.cenorudoaddons.client.events.impl.ChatReceivedEvent;
import iqan.cenorudoaddons.client.events.impl.ClientTickEvent;
import iqan.cenorudoaddons.client.events.impl.EntitySpawnEvent;
import iqan.cenorudoaddons.client.feature.Feature;
import iqan.cenorudoaddons.client.utils.RotationUtils;
import iqan.cenorudoaddons.config.categories.FishingConfigurationCategory;
import java.util.Locale;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;

public class AutoFishingFeature
  extends Feature {
  private static final Pattern TIMER_PATTERN = Pattern.compile("§e§l\\d+(\\.\\d+)?");
  
  private static final String BITE_INDICATOR = "!!!";
  
  private static final double ARMOR_STAND_DETECTION_RADIUS = 2.0D;
  
  private static final double GOLDEN_FISH_SEARCH_RADIUS = 5.0D;
  
  private static final float GOLDEN_FISH_ROTATION_TOLERANCE = 5.0F;
  
  private static final long GOLDEN_FISH_TIMEOUT_MS = 70000L;
  
  private static final String GOLDEN_FISH_SPAWN = "You spot a Golden Fish surface from beneath the lava!";
  
  private static final String GOLDEN_FISH_INTERACT = "The Golden Fish escapes your hook but looks weakened.";
  
  private static final String GOLDEN_FISH_WEAK = "The Golden Fish is weak!";
  
  private static final String GOLDEN_FISH_DESPAWN = "The Golden Fish swims back beneath the lava...";
  
  private int cooldown = 0;
  
  private ArmorStand trackedArmorStand = null;
  
  private boolean bitePending = false;
  
  private int hookWaterTicks = 0;
  
  private boolean goldenFishActive = false;
  
  private boolean goldenFishWeak = false;
  
  private boolean goldenFishActionPending = false;
  
  private long goldenFishLastUpdate = 0L;
  
  private ArmorStand goldenFishTarget = null;
  
  private final Minecraft client = Minecraft.getInstance();
  
  public AutoFishingFeature() {
    super("autoFishingFeature", "Auto Fishing", () -> FishingConfigurationCategory.autoFishingEnabled);
  }
  
  protected void onActivate() {
    subscribe(ClientTickEvent.class, this::onClientTick);
    subscribe(EntitySpawnEvent.class, this::onEntitySpawn);
    subscribe(ChatReceivedEvent.class, this::onChatReceived);
  }
  
  protected void onDeactivate() {
    resetState();
    resetGoldenFishState();
    this.cooldown = 0;
    this.hookWaterTicks = 0;
  }
  
  private void onChatReceived(ChatReceivedEvent event) {
    if (!isEnabled() || !FishingConfigurationCategory.autoGoldenFishEnabled)
      return; 
    String message = event.getStrippedMessage();
    if (message.contains("You spot a Golden Fish surface from beneath the lava!")) {
      this.goldenFishActive = true;
      this.goldenFishWeak = false;
      this.goldenFishActionPending = false;
      this.goldenFishTarget = null;
      this.goldenFishLastUpdate = System.currentTimeMillis();
      return;
    } 
    if (!this.goldenFishActive)
      return; 
    if (message.contains("The Golden Fish escapes your hook but looks weakened.")) {
      this.goldenFishActionPending = true;
      this.goldenFishLastUpdate = System.currentTimeMillis();
      return;
    } 
    if (message.contains("The Golden Fish is weak!")) {
      this.goldenFishWeak = true;
      this.goldenFishLastUpdate = System.currentTimeMillis();
      return;
    } 
    if (message.contains("The Golden Fish swims back beneath the lava...") || message.contains("TROPHY FISH! Golden Fish"))
      resetGoldenFishState(); 
  }
  
  private void onEntitySpawn(EntitySpawnEvent event) {
    ArmorStand armorStand;
    if (!isEnabled())
      return; 
    Entity entity = event.entity();
    if (entity instanceof ArmorStand) {
      armorStand = (ArmorStand)entity;
    } else {
      return;
    } 
    if (this.client.player == null)
      return; 
    FishingHook bobber = this.client.player.fishing;
    if (bobber == null)
      return; 
    if (armorStand.distanceToSqr((Entity)bobber) <= 4.0D)
      this.trackedArmorStand = armorStand; 
    if (!this.goldenFishActive)
      return; 
    if (armorStand.distanceToSqr((Entity)bobber) <= 25.0D && isPotentialGoldenFish(armorStand))
      this.goldenFishTarget = armorStand; 
  }
  
  private void onClientTick(ClientTickEvent event) {
    if (!isEnabled() || this.client.player == null || this.client.level == null)
      return; 
    if (FishingConfigurationCategory.disableFishingWhileOnScreen && this.client.gui.screen() != null)
      return; 
    if (this.cooldown > 0) {
      this.cooldown--;
      return;
    } 
    FishingHook bobber = this.client.player.fishing;
    if (handleGoldenFish(bobber))
      return; 
    if (bobber == null) {
      resetState();
      if (isHoldingFishingRod(this.client)) {
        useItem(this.client);
        this.hookWaterTicks = 0;
        this.cooldown = FishingConfigurationCategory.fishingCastDelay;
      } 
      return;
    } 
    this.hookWaterTicks++;
    if (this.bitePending) {
      useItem(this.client);
      this.cooldown = FishingConfigurationCategory.fishingReelDelay;
      resetState();
      return;
    } 
    int minWaitTicks = FishingConfigurationCategory.minFishWaitDelay * 20;
    if (this.trackedArmorStand != null) {
      if (this.trackedArmorStand.isRemoved()) {
        this.trackedArmorStand = null;
        return;
      } 
      if (this.hookWaterTicks >= minWaitTicks && isBiteIndicator(this.trackedArmorStand))
        this.bitePending = true; 
    } 
    if (this.trackedArmorStand == null)
      findArmorStandNearBobber(this.client, bobber); 
  }
  
  private boolean handleGoldenFish(FishingHook bobber) {
    if (!FishingConfigurationCategory.autoGoldenFishEnabled) {
      resetGoldenFishState();
      return false;
    } 
    if (!this.goldenFishActive)
      return false; 
    if (System.currentTimeMillis() - this.goldenFishLastUpdate > 70000L) {
      resetGoldenFishState();
      return false;
    } 
    updateGoldenFishTarget(bobber);
    if (this.goldenFishWeak) {
      if (bobber != null) {
        useItem(this.client);
        this.cooldown = FishingConfigurationCategory.fishingReelDelay;
        resetGoldenFishState();
        resetState();
        return true;
      } 
      if (!isHoldingFishingRod(this.client))
        return false; 
      if (!isFacingGoldenFishTarget())
        return true; 
      useItem(this.client);
      this.cooldown = FishingConfigurationCategory.autoGoldenFishCastDelay;
      return true;
    } 
    if (this.goldenFishActionPending && bobber != null) {
      useItem(this.client);
      this.cooldown = FishingConfigurationCategory.autoGoldenFishCastDelay;
      this.goldenFishActionPending = false;
      return true;
    } 
    if (bobber == null && isHoldingFishingRod(this.client)) {
      if (!isFacingGoldenFishTarget())
        return true; 
      useItem(this.client);
      this.cooldown = FishingConfigurationCategory.autoGoldenFishCastDelay;
      return true;
    } 
    return true;
  }
  
  private void updateGoldenFishTarget(FishingHook bobber) {
    if (this.client.level == null || this.client.player == null)
      return; 
    if (this.goldenFishTarget != null && !this.goldenFishTarget.isRemoved() && this.goldenFishTarget.isAlive())
      return; 
    this.goldenFishTarget = null;
    if (bobber == null)
      return; 
    AABB searchBox = bobber.getBoundingBox().inflate(5.0D);
    for (Entity entity : this.client.level.getEntities((Entity)bobber, searchBox)) {
      if (entity instanceof ArmorStand) {
        ArmorStand armorStand = (ArmorStand)entity;
        if (!isPotentialGoldenFish(armorStand))
          continue; 
        this.goldenFishTarget = armorStand;
        break;
      } 
    } 
  }
  
  private boolean isFacingGoldenFishTarget() {
    if (this.client.player == null || this.goldenFishTarget == null)
      return true; 
    if (RotationUtils.isRotating())
      return false; 
    Vec2 rotation = RotationUtils.calculateRotationToEntity((Entity)this.goldenFishTarget).rotation();
    if (rotation == null)
      return true; 
    float yawDiff = Math.abs(Mth.wrapDegrees(rotation.y - this.client.player.getYRot()));
    float pitchDiff = Math.abs(rotation.x - this.client.player.getXRot());
    if (yawDiff <= 5.0F && pitchDiff <= 5.0F)
      return true; 
    RotationUtils.smoothRotate(rotation.y, rotation.x, 120, 220, false, true);
    return false;
  }
  
  private boolean isPotentialGoldenFish(@NotNull ArmorStand armorStand) {
    if (armorStand.hasCustomName()) {
      String customName = armorStand.getCustomName().getString();
      if (customName.toLowerCase(Locale.ROOT).contains("golden fish"))
        return true; 
      if (customName.equals("!!!") || TIMER_PATTERN.matcher(customName).matches())
        return false; 
    } 
    Item headItem = armorStand.getItemBySlot(EquipmentSlot.HEAD).getItem();
    return (headItem == Items.PLAYER_HEAD);
  }
  
  private void findArmorStandNearBobber(@NotNull Minecraft client, @NotNull FishingHook bobber) {
    AABB searchBox = bobber.getBoundingBox().inflate(2.0D);
    if (client.level == null)
      return; 
    for (Entity entity : client.level.getEntities((Entity)bobber, searchBox)) {
      if (entity instanceof ArmorStand) {
        ArmorStand armorStand = (ArmorStand)entity;
        if (isValidFishingArmorStand(armorStand)) {
          this.trackedArmorStand = armorStand;
          break;
        } 
      } 
    } 
  }
  
  private boolean isValidFishingArmorStand(@NotNull ArmorStand armorStand) {
    if (!armorStand.hasCustomName())
      return false; 
    String name = armorStand.getCustomName().getString();
    return (name.equals("!!!") || TIMER_PATTERN.matcher(name).matches());
  }
  
  private boolean isBiteIndicator(@NotNull ArmorStand armorStand) {
    if (!armorStand.hasCustomName())
      return false; 
    return armorStand.getCustomName().getString().equals("!!!");
  }
  
  private void resetState() {
    this.trackedArmorStand = null;
    this.bitePending = false;
    this.hookWaterTicks = 0;
  }
  
  private void resetGoldenFishState() {
    this.goldenFishActive = false;
    this.goldenFishWeak = false;
    this.goldenFishActionPending = false;
    this.goldenFishLastUpdate = 0L;
    this.goldenFishTarget = null;
  }
  
  private boolean isHoldingFishingRod(@NotNull Minecraft client) {
    Item mainHand = client.player.getMainHandItem().getItem();
    Item offHand = client.player.getOffhandItem().getItem();
    return (mainHand instanceof net.minecraft.world.item.FishingRodItem || offHand instanceof net.minecraft.world.item.FishingRodItem);
  }
  
  private void useItem(@NotNull Minecraft client) {
    if (client.gameMode != null && client.player != null) {
      InteractionHand hand = (client.player.getMainHandItem().getItem() instanceof net.minecraft.world.item.FishingRodItem) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
      client.gameMode.useItem((Player)client.player, hand);
    } 
  }
}


