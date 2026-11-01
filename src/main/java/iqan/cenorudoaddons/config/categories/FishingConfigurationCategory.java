package iqan.cenorudoaddons.config.categories;

import com.teamresourceful.resourcefulconfig.api.annotations.Category;
import com.teamresourceful.resourcefulconfig.api.annotations.Comment;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption.Range;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption.Separator;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption.Slider;

@Category("Fishing")
public class FishingConfigurationCategory {
  @Separator("Auto Fishing")
  @ConfigEntry(id = "autoFishingEnabled", translation = "Auto Fishing")
  @Comment("Automatically reel and recast your fishing rod")
  public static boolean autoFishingEnabled = false;
  @ConfigEntry(id = "autoFishingCastDelay", translation = "Cast Delay (ticks)")
  @Comment("Delay in ticks before casting the rod after reeling")
  @Range(min = 5.0D, max = 100.0D)
  @Slider
  public static int fishingCastDelay = 20;
  
  @ConfigEntry(id = "autoFishingReelDelay", translation = "Reel Delay (ticks)")
  @Comment("Delay in ticks before reeling after a bite")
  @Range(min = 1.0D, max = 20.0D)
  @Slider
  public static int fishingReelDelay = 10;
  
  @ConfigEntry(id = "minFishWaitDelay", translation = "Min Fish Wait Delay (seconds)")
  @Comment("Minimum time in seconds the hook must stay in water before reeling")
  @Range(min = 0.0D, max = 60.0D)
  @Slider
  public static int minFishWaitDelay = 0;
  
  @ConfigEntry(id = "autoFishingDisableInScreen", translation = "Disable While In Screen")
  @Comment("Pause auto fishing when any screen is open")
  public static boolean disableFishingWhileOnScreen = true;
  
  @ConfigEntry(id = "autoGoldenFishEnabled", translation = "Auto Golden Fish")
  @Comment("Automatically handles Golden Fish hook/recast cycle using chat cues")
  public static boolean autoGoldenFishEnabled = false;
  
  @ConfigEntry(id = "autoGoldenFishCastDelay", translation = "Golden Fish Action Delay (ticks)")
  @Comment("Delay in ticks between Golden Fish hook interactions")
  @Range(min = 1.0D, max = 20.0D)
  @Slider
  public static int autoGoldenFishCastDelay = 4;
  
  @Separator("Oxygen Recovery")
  @ConfigEntry(id = "oxygenRecoveryEnabled", translation = "Oxygen Recovery")
  @Comment("Automatically jump when oxygen is low to recover it")
  public static boolean oxygenRecoveryEnabled = false;
  
  @ConfigEntry(id = "oxygenThreshold", translation = "Oxygen Threshold (%)")
  @Comment("Start jumping when oxygen falls below this percentage")
  @Range(min = 5.0D, max = 50.0D)
  @Slider
  public static int oxygenThreshold = 10;
}


