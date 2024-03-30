package com.hangbunny.item;

import com.hangbunny.TomesOfExperience;
import com.hangbunny.experience.ExperienceUtils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Rarity;

public class TomeOfExperience extends BaseTomeOfExperience {

    public TomeOfExperience(Settings settings) {
        super(settings.maxCount(6).rarity(Rarity.UNCOMMON));
   }

    @Override
    protected int getCapacity() {
        return TomesOfExperience.CONFIG.experience_points_capacity;
    }

    @Override
    protected float getEfficiency() {
        return TomesOfExperience.CONFIG.experience_points_efficiency;
    }

    @Override
    protected int getMinimumLevel() {
        return TomesOfExperience.CONFIG.minimum_level;
    }

    @Override
    protected int pointsToTransferToTome(PlayerEntity user) {
        int currentLevel = user.experienceLevel;
        int pointsCurrentLevel = ExperienceUtils.getExperiencePoints(user) - ExperienceUtils.getExperienceForLevel(currentLevel);

        // Transfer all the points in the current level plus
        // a single point to go down to the previous level.
        int pointsToTransfer = pointsCurrentLevel + 1;

        return pointsToTransfer;
    }

    @Override
    protected int pointsToTransferToPlayer(PlayerEntity user) {
        int currentLevel = user.experienceLevel;
        int pointsTotal = ExperienceUtils.getExperiencePoints(user);
        
        // Transfer as many points as needed to get to the next level.
        int pointsToTransfer = (ExperienceUtils.getExperienceForLevel(currentLevel + 1) - pointsTotal) + 1;

        return pointsToTransfer;
    }

    @Override
    protected int roundingMethod(float value) {
        return (int) Math.floor(value);
    }
}