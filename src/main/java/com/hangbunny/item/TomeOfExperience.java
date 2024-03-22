package com.hangbunny.item;

import java.util.List;

import com.hangbunny.TomesOfExperience;
import com.hangbunny.experience.ExperienceUtils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class TomeOfExperience extends Item {
    
    public TomeOfExperience(Settings settings) {
        super(settings.maxCount(8).rarity(Rarity.COMMON));
   }

   @Override
   public ItemStack getDefaultStack() {
        ItemStack itemStack = super.getDefaultStack();
        NbtCompound tags = itemStack.getOrCreateNbt();
        tags.putInt("experience", 0);

        return itemStack;
   }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        // Check whether the tome has a custom tag for tracking
        // how many experience points are stored in it.
        NbtCompound tags = itemStack.getNbt();
        if (itemStack.hasNbt()) {
            int tomeExperience = tags.getInt("experience");
            if (tomeExperience > 0) {
                tooltip.add(Text.translatable("item.tomes_of_experience.tome_of_experience.tooltip.points", tomeExperience));
                return;
            }
        }

        tooltip.add(Text.translatable("item.tomes_of_experience.tome_of_experience.tooltip.empty"));
    }

    @Override
    public void onCraft(ItemStack itemStack, World world, PlayerEntity player) {
        if (world.isClient) { return; }

        itemStack.getOrCreateNbt().putInt("experience", 0);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        // FIXME: Split a tome off the stack
        if (stack.getCount() > 1) {
            return TypedActionResult.pass(stack);
        }

        // Get tome configuration
        int capacity = TomesOfExperience.CONFIG.experience_points_capacity;
        float efficiency = TomesOfExperience.CONFIG.experience_points_efficiency;

        // Make sure the tome has a custom tag for tracking
        // how many experience points are stored in it.
        NbtCompound tags = stack.getNbt();
        if (!stack.hasNbt()) {
            tags = new NbtCompound();
            tags.putInt("experience", 0);
        }

        int pointsPlayer = ExperienceUtils.getExperiencePoints(user);
        int pointsTome = tags.getInt("experience");

        if (!world.isClient) {

            if (user.isSneaking()) {
                // Don't allow storing more experience points if the tome is full
                // Don't truncate the tome to not penalize players that are tweaking
                // the configuration values.
                if (pointsTome >= capacity) {
                    return TypedActionResult.pass(stack);
                }

                // Transfer a whole XP level into the tome.

                // FIXME: Check player xp level isn't 0
                user.addExperienceLevels(-1);

                int pointsTranferred = pointsPlayer - ExperienceUtils.getExperiencePoints(user);

                // Apply an efficiency loss when storing experience points
                // Using 'floor' to be biased towards losing experience points in the transfer.
                int pointsToStore = (int) Math.floor(pointsTranferred * efficiency);

                // Respect the storage capacity of the tome and refund the player the
                // experience points that couldn't be stored - after the efficiency loss.
                if (pointsTome + pointsToStore > capacity) {
                    int pointsToRefund = capacity - (pointsTome + pointsToStore);
                    pointsTome = capacity;
                    user.addExperience(pointsToRefund);
                } else {
                    pointsTome += pointsToStore;
                }

                tags.putInt("experience", pointsTome);
                stack.setNbt(tags);

            } else {
                // Transfer up to 10 XP points from the tome.
                int pointsToTransfer = 10;

                if (pointsTome <= 0) {
                    return TypedActionResult.pass(stack);
                }

                // Drain the remaining XP points in the tome if it contains
                // less than the requested amount.
                if (pointsTome < pointsToTransfer) {
                    pointsToTransfer = pointsTome;
                }

                pointsTome -= pointsToTransfer;
                tags.putInt("experience", pointsTome);
                stack.setNbt(tags);

                user.addExperience(pointsToTransfer);

                // Play a sound when getting experience points
                float volumeMultiplier = 0.1F;
                float pitchMultiplier = (world.random.nextFloat() - world.random.nextFloat()) * 0.35F + 0.9F;
                world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, volumeMultiplier, pitchMultiplier);
            }
        } else {
            // On the client side
            // Don't swing the player's arm when the tome is empty or full.
            if (!user.isSneaking() && pointsTome <= 0
                || user.isSneaking() && pointsTome >= capacity) {
                return TypedActionResult.pass(stack);
            }
        }

        return TypedActionResult.success(stack, world.isClient());
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean hasGlint(ItemStack itemStack) {
        NbtCompound tags = itemStack.getNbt();
        if (itemStack.hasNbt()) {
            int tomeExperience = tags.getInt("experience");
            if (tomeExperience > 0) {
                return true;
            }
        }

        return false;
    }
}