package com.hangbunny.item;

import java.util.List;

import com.hangbunny.experience.ExperienceUtils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
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

    public static final String EXPERIENCE = "experience";

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
        return 1.0f;
    }

    protected int getMinimumLevel() {
        return 0;
    }

    // Overridden by the subclasses to control how many
    // experience points are transferred.
    protected int pointsToTransferToTome(PlayerEntity user) {
        return 0;
    }

    protected int pointsToTransferToPlayer(PlayerEntity user) {
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
        NbtCompound tags = itemStack.getOrCreateNbt();
        int tomeExperience = tags.getInt(EXPERIENCE);
        if (tomeExperience > 0) {
            // Lacking a 'switch' statement with range capabilities, do this
            // instead to add flavor text to the tooltip depending on how full
            // of experience points the tome is.
            int percentFull = (int) Math.floor(((float) tomeExperience / this.getCapacity()) * 10);
            switch (percentFull) {
                case 0:
                case 1:
                    tooltip.add(Text.translatable("item.tomes_of_experience.tome_of_experience.tooltip.0-20"));
                    break;
                case 2:
                case 3:
                    tooltip.add(Text.translatable("item.tomes_of_experience.tome_of_experience.tooltip.20-40"));
                    break;
                case 4:
                case 5:
                    tooltip.add(Text.translatable("item.tomes_of_experience.tome_of_experience.tooltip.40-60"));
                    break;
                case 6:
                case 7:
                    tooltip.add(Text.translatable("item.tomes_of_experience.tome_of_experience.tooltip.60-80"));
                    break;
                case 8:
                case 9:
                    tooltip.add(Text.translatable("item.tomes_of_experience.tome_of_experience.tooltip.80-100"));
                    break; 
                default:
                    tooltip.add(Text.translatable("item.tomes_of_experience.tome_of_experience.tooltip.100"));
                    break; 
            }
        } else {
            tooltip.add(Text.translatable("item.tomes_of_experience.tome_of_experience.tooltip.0"));
        }

        if (Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("item.tomes_of_experience.tome_of_experience.tooltip.points", tomeExperience, this.getCapacity()));
        }

        super.appendTooltip(itemStack,world, tooltip, tooltipContext);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        NbtCompound tags = itemStack.getOrCreateNbt();


        // Get tome configuration.
        int capacity = this.getCapacity();
        float efficiency = this.getEfficiency();

        int pointsTome = tags.getInt(EXPERIENCE);

        if (!world.isClient) {
            // Split a tome stack if there's more than one tome
            // in it. This mimicks the behavior of vanilla stacks.
            if (itemStack.getCount() > 1) {
                int splitStackSlot = user.getInventory().getEmptySlot();

                ItemStack newItemStack = itemStack.split(itemStack.getCount() - 1);
                user.getInventory().insertStack(splitStackSlot, newItemStack);
            }

            if (user.isSneaking()) {
                // Apply an efficiency loss when storing experience points.
                // Using 'floor' to be biased towards losing experience points in the transfer.
                int pointsToTransfer = this.pointsToTransferToTome(user);
                int pointsToStore = this.roundingMethod(pointsToTransfer * efficiency);

                // Don't try to store experience points if the user doesn't have any.
                // Don't allow storing more experience points if the tome is full.
                // Don't truncate the tome to not penalize players that are tweaking
                // the configuration values.
                if ((ExperienceUtils.getExperiencePoints(user) <= 0)
                    || (pointsToTransfer == 0)
                    || (pointsTome >= capacity)) {
                    return TypedActionResult.pass(itemStack);
                }

                // Enforce the minimum experience level the player must have
                // to be allowed to transfer experience points to the tome.
                if (user.experienceLevel < this.getMinimumLevel()) {
                    return TypedActionResult.pass(itemStack);
                }

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
                int pointsToTransfer = this.pointsToTransferToPlayer(user);

                if ((pointsTome <= 0) 
                    || (pointsToTransfer == 0)) {
                    return TypedActionResult.pass(itemStack);
                }

                // Drain the remaining XP points in the tome if it contains
                // less than the requested amount.
                if (pointsTome < pointsToTransfer) {
                    pointsToTransfer = pointsTome;
                }

                pointsTome -= pointsToTransfer;
                tags.putInt(EXPERIENCE, pointsTome);

                user.addExperience(pointsToTransfer);

                // Play a sound when getting experience points.
                float volumeMultiplier = 0.1f;
                float pitchMultiplier = (world.random.nextFloat() - world.random.nextFloat()) * 0.35f + 0.9f;
                world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, volumeMultiplier, pitchMultiplier);
            }
        } else {
            // On the client side
            // Don't swing the player's arm when:
            //   The player has no experience points
            //   The tome is either empty or full
            //   No experience points would be transferred in either direction
            //   The player doesn't have the minimum experience level required by the tome
            if ((user.isSneaking() && ExperienceUtils.getExperiencePoints(user) <= 0)
                || (user.isSneaking() && pointsTome >= this.getCapacity())
                || (user.isSneaking() && this.pointsToTransferToTome(user) == 0)
                || (user.isSneaking() && (user.experienceLevel < this.getMinimumLevel()))
                || (!user.isSneaking() && pointsTome <= 0)
                || (!user.isSneaking() && this.pointsToTransferToPlayer(user) == 0)) {
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