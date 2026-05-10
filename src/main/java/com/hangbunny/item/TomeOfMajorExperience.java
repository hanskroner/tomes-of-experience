package com.hangbunny.item;

import com.hangbunny.TomesOfExperience;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public class TomeOfMajorExperience extends BaseTomeOfExperience {

    public TomeOfMajorExperience(Properties properties) {
        super(properties.stacksTo(6).rarity(Rarity.EPIC));
   }

    @Override
    public int getCapacity() {
        return TomesOfExperience.CONFIG.major_experience_points_capacity;
    }

    @Override
    protected float getEfficiency() {
        return TomesOfExperience.CONFIG.major_experience_points_efficiency;
    }

    @Override
    protected int getMinimumLevel() {
        return TomesOfExperience.CONFIG.major_minimum_level;
    }

    @Override
    protected int transferToTome(ItemStack tomeItemStack, Player player) {
        // Transfer the points in the current level plus the points of the four levels
        // before it. Transfer one level at a time in a loop.
        int totalPointsConsumed = 0;
        for (int i = 0; i < 5; i++) {
            int pointsToNextLevel = player.getXpNeededForNextLevel();
            int pointsCurrentLevel = (int) Math.ceil(player.experienceProgress * (float)pointsToNextLevel);

            // Transfer all the points in the current level plus
            // a single point to go down to the previous level.
            int pointsToTransfer = pointsCurrentLevel + 1;

            // 'player.totalExperience' doesn't seem to report reliable values.
            // Check that we're not transferring more points than the player has by
            // checking both the level and the points in the current level.
            if (player.experienceLevel == 0 && pointsToTransfer > pointsCurrentLevel) {
                pointsToTransfer = pointsCurrentLevel;
            }

            // Try to transfer the points to the tome and subtract the amount that
            // could be transferred from the player.
            int pointsConsumed = this.addPointsToTome(tomeItemStack, pointsToTransfer);
            player.giveExperiencePoints(-pointsConsumed);

            totalPointsConsumed += pointsConsumed;
        }

        return totalPointsConsumed;
    }

    @Override
    protected int transferToPlayer(ItemStack tomeItemStack, Player player) {
        // Transfer as many points as needed for the player to go up a level.
        // Do this five times to go up a total of five levels.
        int totalPointsTransferred = 0;
        for (int i = 0; i < 5; i++) {
            int pointsToNextLevel = player.getXpNeededForNextLevel();
            int pointsCurrentLevel = (int) Math.ceil(player.experienceProgress * (float)pointsToNextLevel);
            int pointsNextLevel = pointsToNextLevel - pointsCurrentLevel;

            // Transfer as many points as needed to get to the next level.
            int pointsToTransfer = pointsNextLevel + 1;

            // Try to get the points from the tome - or as many as it can provide - and 
            // add that amount to the player.
            int pointsTransferred = this.removePointsFromTome(tomeItemStack, pointsToTransfer);
            player.giveExperiencePoints(pointsTransferred);

            totalPointsTransferred += pointsTransferred;
        }

        return totalPointsTransferred;
    }

    @Override
    protected int roundingMethod(float value) {
        return (int) Math.ceil(value);
    }
}