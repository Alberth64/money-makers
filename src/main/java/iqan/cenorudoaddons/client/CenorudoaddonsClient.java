package iqan.cenorudoaddons.client;
import com.mojang.brigadier.CommandDispatcher;
import iqan.cenorudoaddons.client.commands.CenorudoAddonsCommand;
import iqan.cenorudoaddons.client.feature.Feature;
import iqan.cenorudoaddons.client.feature.FeatureManager;
import iqan.cenorudoaddons.client.feature.impl.fishing.AutoFishingFeature;
import iqan.cenorudoaddons.client.feature.impl.fishing.OxygenRecoverFeature;
import iqan.cenorudoaddons.client.utils.RotationUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.resources.Identifier;

public class CenorudoaddonsClient implements ClientModInitializer {
  public static final KeyMapping.Category KEYBIND_CATEGORY = KeyMapping.Category.register(
      Identifier.fromNamespaceAndPath("cenorudo-addons", "keybinds"));






  
  public void onInitializeClient() {
    MoneyMakersKeyBindings.register();
    RotationUtils.init();
    
    ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> CenorudoAddonsCommand.register(dispatcher));



    
    initializeFeatures();
  }
  
  private void initializeFeatures() {
    FeatureManager features = FeatureManager.get();
    
    features.register(new Feature[] { (Feature)new AutoFishingFeature(), (Feature)new OxygenRecoverFeature() });



    
    features.start();
  }
}


