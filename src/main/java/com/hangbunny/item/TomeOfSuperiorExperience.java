package com.hangbunny.item;

import com.hangbunny.TomesOfExperience;
import com.hangbunny.experience.ExperienceUtils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Rarity;

public class TomeOfSuperiorExperience extends BaseTomeOfExperience {

    public TomeOfSuperiorExperience(Settings settings) {
        super(settings.maxCount(6).rarity(Rarity.EPIC));
   }

    @Override
    protected int getCapacity() {
        return TomesOfExperience.CONFIG.superior_experience_points_capacity;
    }

    @Override
    protected float getEfficiency() {
        return TomesOfExperience.CONFIG.superior_experience_points_efficiency;
    }

    @Override
    protected int getMinimumLevel() {
        return TomesOfExperience.CONFIG.superior_minimum_level;
    }

    @Override
    protected int pointsToTransferToTome(PlayerEntity user) {
        int currentLevel = user.experienceLevel;
        int pointsCurrentLevel = ExperienceUtils.getExperiencePoints(user) - ExperienceUtils.getExperienceForLevel(currentLevel);
        int additionalPoints = ExperienceUtils.getExperienceForLevel(currentLevel) - ExperienceUtils.getExperienceForLevel(currentLevel - 2);

        // Transfer all the points in the current level, two additional
        // levels, plus a single point to go down a total of three levels.
        int pointsToTransfer = pointsCurrentLevel + additionalPoints + 1;

        return pointsToTransfer;
    }

    @Override
    protected int pointsToTransferToPlayer(PlayerEntity user) {
        int currentLevel = user.experienceLevel;
        int pointsTotal = ExperienceUtils.getExperiencePoints(user);
        int additionalPoints = ExperienceUtils.getExperienceForLevel(currentLevel + 3) - ExperienceUtils.getExperienceForLevel(currentLevel + 1);
        
        // Transfer as many points as needed to get to the next level, and
        // two more levels after that to go up a total of three levels.
        int pointsToTransfer = ExperienceUtils.getExperienceForLevel(currentLevel + 1) - pointsTotal + additionalPoints;

        return pointsToTransfer;
    }

    @Override
    protected int roundingMethod(float value) {
        return (int) Math.ceil(value);
    }
}