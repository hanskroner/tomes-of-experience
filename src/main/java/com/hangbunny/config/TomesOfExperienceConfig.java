package com.hangbunny.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "tomes_of_experience")
@Config.Gui.Background("minecraft:textures/block/stone.png")
public class TomesOfExperienceConfig implements ConfigData {

    @Comment("The maximum amount of experience points a tome can hold")
    public int experience_points_capacity = 1395;
    @Comment("The percentage of experience points the tome will convert to store")
    public float experience_points_efficiency = 0.98F;
}
