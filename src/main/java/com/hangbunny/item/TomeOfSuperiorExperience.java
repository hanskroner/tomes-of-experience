package com.hangbunny.item;

import com.hangbunny.TomesOfExperience;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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
    protected int transferToTome(ItemStack tomeItemStack, PlayerEntity user) {
        // Transfer the points in the current level plus the points of the two levels
        // before it. Transfer one level at a time in a loop.
        int totalPointsConsumed = 0;
        for (int i = 0; i < 3; i++) {
            int pointsToNextLevel = user.getNextLevelExperience();
            int pointsCurrentLevel = (int) Math.ceil(user.experienceProgress * (float)pointsToNextLevel);

            // Transfer all the points in the current level plus
            // a single point to go down to the previous level.
            int pointsToTransfer = pointsCurrentLevel + 1;

            // 'user.totalExperience' doesn't seem to report reliable values.
            // Check that we're not transferring more points than the player has by
            // checking both the level and the points in the current level.
            if (user.experienceLevel == 0 && pointsToTransfer > pointsCurrentLevel) {
                pointsToTransfer = pointsCurrentLevel;
            }

            // Try to transfer the points to the tome and subtract the amount that
            // could be transferred from the player.
            int pointsConsumed = this.addPointsToTome(tomeItemStack, pointsToTransfer);
            user.addExperience(-pointsConsumed);

            totalPointsConsumed += pointsConsumed;
        }

        return totalPointsConsumed;
    }

    @Override
    protected int transferToPlayer(ItemStack tomeItemStack, PlayerEntity user) {
        // Transfer as many points as needed for the player to go up a level.
        // Do this three times to go up a total of three levels.
        int totalPointsTransferred = 0;
        for (int i = 0; i < 3; i++) {
            int pointsToNextLevel = user.getNextLevelExperience();

            // Transfer as many points as needed to get to the next level.
            int pointsToTransfer = pointsToNextLevel;

            // Try to get the points from the tome - or as many as it can provide - and 
            // add that amount to the player.
            int pointsTransferred = this.removePointsFromTome(tomeItemStack, pointsToTransfer);
            user.addExperience(pointsTransferred);

            totalPointsTransferred += pointsTransferred;
        }

        return totalPointsTransferred;
    }

    @Override
    protected int roundingMethod(float value) {
        return (int) Math.ceil(value);
    }
}