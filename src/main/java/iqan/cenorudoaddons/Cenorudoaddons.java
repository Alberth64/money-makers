package iqan.cenorudoaddons;

import com.teamresourceful.resourcefulconfig.api.loader.Configurator;
import iqan.cenorudoaddons.config.Configuration;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;


public class Cenorudoaddons
  implements ModInitializer
{
  public static final String MOD_ID = "cenorudoaddons";
  private static Cenorudoaddons instance;
  private Configurator configurator;
  
  public static Cenorudoaddons get() {
    return instance;
  }
  
  public Configurator getConfigurator() {
    return this.configurator;
  }

  
  public void onInitialize() {
    instance = this;
    
    this.configurator = new Configurator("cenorudoaddons");
    this.configurator.register(Configuration.class);
  }
  
  public static Identifier id(String path) {
    return Identifier.fromNamespaceAndPath("cenorudoaddons", path);
  }
}


