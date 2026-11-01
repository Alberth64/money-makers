package iqan.cenorudoaddons.client.feature.impl.fishing;

import iqan.cenorudoaddons.client.events.impl.ClientTickEvent;
import iqan.cenorudoaddons.client.feature.Feature;
import iqan.cenorudoaddons.config.categories.FishingConfigurationCategory;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

public class OxygenRecoverFeature
  extends Feature
{
  private static final int OXYGEN_CHECK_NTH = 10;
  private volatile boolean recoveringOxygen = false;
  
  public OxygenRecoverFeature() {
    super("oxygenRecover", "Oxygen Recovery", () -> FishingConfigurationCategory.oxygenRecoveryEnabled);
  }





  
  protected void onActivate() {
    subscribe(ClientTickEvent.class, this::onClientTick);
  }

  
  private void onClientTick(@NotNull ClientTickEvent event) {
    if (!event.isNthTick(10)) {
      return;
    }
    Minecraft client = Minecraft.getInstance();
    
    if (client.player == null || client.level == null) {
      return;
    }
    int oxygenLevel = client.player.getAirSupply();
    
    if (oxygenLevel > FishingConfigurationCategory.oxygenThreshold && this.recoveringOxygen)
    {
      this.recoveringOxygen = false;
    }
    
    if (client.player.isUnderWater() && oxygenLevel <= FishingConfigurationCategory.oxygenThreshold) {

      
      this.recoveringOxygen = true;
      
      client.options.keyJump.setDown(true);
    }
    else if (!this.recoveringOxygen) {
      
      client.options.keyJump.setDown(false);
    } 
  }
}


