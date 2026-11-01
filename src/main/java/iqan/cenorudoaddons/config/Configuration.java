package iqan.cenorudoaddons.config;

import com.teamresourceful.resourcefulconfig.api.annotations.Config;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigInfo;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigInfo.Link;
import iqan.cenorudoaddons.config.categories.FishingConfigurationCategory;

@Config(value = "moneymakers", categories = {FishingConfigurationCategory.class})
@ConfigInfo(title = "CanorasMakers cracked v67.0.1", description = "iqan comeu o alberth 67 vezes.", links = {@Link(value = "https://github.com/pehenriiReguladorDeSource", icon = "code-2", text = "Github")})
public class Configuration {}


