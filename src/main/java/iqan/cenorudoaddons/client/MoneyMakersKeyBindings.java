package iqan.cenorudoaddons.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.teamresourceful.resourcefulconfig.api.client.ResourcefulConfigScreen;
import iqan.cenorudoaddons.Cenorudoaddons;
import iqan.cenorudoaddons.config.Configuration;
import iqan.cenorudoaddons.config.categories.FishingConfigurationCategory;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class MoneyMakersKeyBindings
{
  private static final Minecraft mc = Minecraft.getInstance();
  
  private static KeyMapping openConfigKey;
  
  private static KeyMapping disableAutoFishingKey;
  
  public static void register() {
    openConfigKey = KeyMappingHelper.registerKeyMapping(new KeyMapping("key.cenorudo-addons.open-config", InputConstants.Type.KEYSYM, 75, CenorudoaddonsClient.KEYBIND_CATEGORY));







    
    disableAutoFishingKey = KeyMappingHelper.registerKeyMapping(new KeyMapping("key.cenorudo-addons.toggle_autofish", InputConstants.Type.KEYSYM, 72, CenorudoaddonsClient.KEYBIND_CATEGORY));







    
    ClientTickEvents.END_CLIENT_TICK.register(client -> {
          while (openConfigKey.consumeClick()) {
            openConfigScreen(client);
          }
          while (disableAutoFishingKey.consumeClick()) {
            FishingConfigurationCategory.autoFishingEnabled = !FishingConfigurationCategory.autoFishingEnabled;
            assert mc.player != null;
            mc.player.sendSystemMessage((Component)Component.literal("Auto Fishing " + (FishingConfigurationCategory.autoFishingEnabled ? "Enabled" : "Disabled")));
          } 
        });
  }









  
  public static void openConfigScreen(@NotNull Minecraft client) {
    client.gui.setScreen(
        ResourcefulConfigScreen.make(
          Cenorudoaddons.get().getConfigurator(), Configuration.class)
        
        .withParent(null).build());
  }
}


