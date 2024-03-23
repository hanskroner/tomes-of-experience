package com.hangbunny.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "tomes_of_experience")
@Config.Gui.Background("minecraft:textures/block/stone.png")
public class TomesOfExperienceConfig implements ConfigData {

    @ConfigEntry.Category("tome_of_experience")
    @ConfigEntry.Gui.Tooltip
    public int experience_points_capacity = 550;
    @ConfigEntry.Category("tome_of_experience")
    @ConfigEntry.Gui.Tooltip
    public float experience_points_efficiency = 0.98F;
}
