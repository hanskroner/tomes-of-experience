package com.hangbunny.item;

import java.util.List;

import com.hangbunny.TomesOfExperience;

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
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public abstract class BaseTomeOfExperience extends Item {

    private static final String EXPERIENCE = "experience";

    public BaseTomeOfExperience() { 
        super(new Settings());
    }

    public BaseTomeOfExperience(Settings settings) {
        super(settings);
    }

    // Use accessors for configuration values.
    // This allows subclasses to override them while allowing for live
    // changes in the configuration file.
    protected int getCapacity() {
        return 0;
    }

    protected float getEfficiency() {
        return 1.0F;
    }

    // Overridden by the subclasses to control how many
    // experience points are transferred.
    protected int pointsToTransferToTome() {
        return 0;
    }

    protected int pointsToTransferToPlayer() {
        return 0;
    }

    protected int roundingMethod(float value) {
        return 0;
    }
   
    @Override
    public ItemStack getDefaultStack() {
        ItemStack itemStack = super.getDefaultStack();
        itemStack.getOrCreateNbt().putInt(EXPERIENCE, 0);

        return itemStack;
    }

    @Override
    public void onCraft(ItemStack itemStack, World world, PlayerEntity player) {
        if (world.isClient) { return; }

        itemStack.getOrCreateNbt().putInt(EXPERIENCE, 0);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        // If the tome has experience points stored, show how many.
        // Otherwise show that it's empty.
        NbtCompound tags = itemStack.getOrCreateNbt();
        int tomeExperience = tags.getInt(EXPERIENCE);
        if (tomeExperience > 0) {
            tooltip.add(Text.translatable("item.tomes_of_experience.tome_of_experience.tooltip.points", tomeExperience));
            return;
        }

        tooltip.add(Text.translatable("item.tomes_of_experience.tome_of_experience.tooltip.empty"));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        NbtCompound tags = itemStack.getOrCreateNbt();

        // FIXME: Split a tome off the stack
        if (itemStack.getCount() > 1) {
            return TypedActionResult.pass(itemStack);
        }

        // Get tome configuration.
        int capacity = this.getCapacity();
        float efficiency = this.getEfficiency();

        int pointsTome = tags.getInt(EXPERIENCE);

        if (!world.isClient) {

            if (user.isSneaking()) {
                // Don't allow storing more experience points if the tome is full.
                // Don't truncate the tome to not penalize players that are tweaking
                // the configuration values.
                if (pointsTome >= capacity) {
                    return TypedActionResult.pass(itemStack);
                }

                // Apply an efficiency loss when storing experience points.
                // Using 'floor' to be biased towards losing experience points in the transfer.
                int pointsToTransfer = this.pointsToTransferToTome();
                int pointsToStore = this.roundingMethod(pointsToTransfer * efficiency);

                user.addExperience(-pointsToTransfer);

                // Respect the storage capacity of the tome and refund the player the
                // experience points that couldn't be stored - after the efficiency loss.
                if (pointsTome + pointsToStore > capacity) {
                    int pointsToRefund = capacity - (pointsTome + pointsToStore);
                    pointsTome = capacity;
                    user.addExperience(pointsToRefund);
                } else {
                    pointsTome += pointsToStore;
                }

                tags.putInt(EXPERIENCE, pointsTome);

            } else {
                if (pointsTome <= 0) {
                    return TypedActionResult.pass(itemStack);
                }

                int pointsToTransfer = this.pointsToTransferToPlayer();

                // Drain the remaining XP points in the tome if it contains
                // less than the requested amount.
                if (pointsTome < pointsToTransfer) {
                    pointsToTransfer = pointsTome;
                }

                pointsTome -= pointsToTransfer;
                tags.putInt(EXPERIENCE, pointsTome);

                user.addExperience(pointsToTransfer);

                // Play a sound when getting experience points.
                float volumeMultiplier = 0.1F;
                float pitchMultiplier = (world.random.nextFloat() - world.random.nextFloat()) * 0.35F + 0.9F;
                world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, volumeMultiplier, pitchMultiplier);
            }
        } else {
            // On the client side
            // Don't swing the player's arm when the tome is empty or full.
            if (!user.isSneaking() && pointsTome <= 0
                || user.isSneaking() && pointsTome >= capacity) {
                return TypedActionResult.pass(itemStack);
            }
        }

        return TypedActionResult.success(itemStack, world.isClient());
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean hasGlint(ItemStack itemStack) {
        // Make the tome glint if it has experience points stored.
        NbtCompound tags = itemStack.getOrCreateNbt();
        int tomeExperience = tags.getInt(EXPERIENCE);

        return (tomeExperience > 0);
    }
}