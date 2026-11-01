package iqan.cenorudoaddons.client.feature.impl.failsafe;

import iqan.cenorudoaddons.config.categories.FishingConfigurationCategory;
import java.util.concurrent.ThreadLocalRandom;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;







public final class AutoFishingFailSafe
{
  private static final double TELEPORT_DISTANCE_THRESHOLD = 20.0D;
  private static final long LOOK_DELAY_MIN_MS = 5000L;
  private static final long LOOK_DELAY_MAX_MS = 10000L;
  private static final long LOOK_STEP_MIN_MS = 350L;
  private static final long LOOK_STEP_MAX_MS = 700L;
  private static ResourceKey<Level> lastWorldKey;
  private static double lastX;
  private static double lastY;
  private static double lastZ;
  private static boolean trackingPosition;
  private static boolean lookAroundPending;
  private static long nextLookActionAtMs;
  private static int lookStep;
  private static float baseYaw;
  private static float basePitch;
  
  public static void register() {
    ClientTickEvents.END_CLIENT_TICK.register(AutoFishingFailSafe::onClientTick);
    ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> triggerFailSafe(client));
  }

  
  private static void onClientTick(Minecraft client) {
    if (client.player == null || client.level == null) {
      trackingPosition = false;
      
      return;
    } 
    if (lookAroundPending && System.currentTimeMillis() >= nextLookActionAtMs) {
      performLookAroundStep(client);
    }
    
    if (!FishingConfigurationCategory.autoFishingEnabled) {
      trackingPosition = false;
      
      return;
    } 
    ResourceKey<Level> currentWorldKey = client.level.dimension();
    
    double currentX = client.player.getX();
    double currentY = client.player.getY();
    double currentZ = client.player.getZ();
    
    if (!trackingPosition) {
      trackCurrentPosition(currentWorldKey, currentX, currentY, currentZ);
      
      return;
    } 
    if (!currentWorldKey.equals(lastWorldKey)) {
      triggerFailSafe(client);
      trackCurrentPosition(currentWorldKey, currentX, currentY, currentZ);
      
      return;
    } 
    double dx = currentX - lastX;
    double dy = currentY - lastY;
    double dz = currentZ - lastZ;
    
    double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
    
    if (distance >= 20.0D) {
      triggerFailSafe(client);
    }
    
    trackCurrentPosition(currentWorldKey, currentX, currentY, currentZ);
  }

  
  private static void triggerFailSafe(Minecraft client) {
    if (!FishingConfigurationCategory.autoFishingEnabled) {
      return;
    }
    
    FishingConfigurationCategory.autoFishingEnabled = false;
    
    if (client.player != null) {
      baseYaw = client.player.getYRot();
      basePitch = client.player.getXRot();
    } 
    
    lookAroundPending = true;
    lookStep = 0;


    
    nextLookActionAtMs = System.currentTimeMillis() + ThreadLocalRandom.current().nextLong(5000L, 10001L);
  }

  
  private static void performLookAroundStep(Minecraft client) {
    if (client.player == null) {
      lookAroundPending = false;
      
      return;
    } 
    if (lookStep == 0) {
      
      float yawOffset = randomYawOffset();
      
      client.player.setYRot(baseYaw + yawOffset);
      client.player.setXRot(basePitch);
      
      lookStep = 1;
      nextLookActionAtMs = System.currentTimeMillis() + randomStepDelay();
      
      return;
    } 
    if (lookStep == 1) {
      
      float yawOffset = randomYawOffset();
      
      client.player.setYRot(baseYaw - yawOffset);
      client.player.setXRot(basePitch);
      
      lookStep = 2;
      nextLookActionAtMs = System.currentTimeMillis() + randomStepDelay();
      
      return;
    } 
    client.player.setYRot(baseYaw);
    client.player.setXRot(basePitch);
    
    lookAroundPending = false;
  }
  
  private static float randomYawOffset() {
    return (float)ThreadLocalRandom.current().nextDouble(30.0D, 65.0D);
  }
  
  private static long randomStepDelay() {
    return ThreadLocalRandom.current().nextLong(350L, 701L);
  }


  
  private static void trackCurrentPosition(ResourceKey<Level> worldKey, double x, double y, double z) {
    lastWorldKey = worldKey;
    lastX = x;
    lastY = y;
    lastZ = z;
    trackingPosition = true;
  }
}


