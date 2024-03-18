package com.hangbunny.item;

import java.util.List;

import com.hangbunny.experience.ExperienceUtils;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class TomeOfExperience extends Item {
    
    public TomeOfExperience(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        tooltip.add(Text.translatable("item.tomes_of_experience.tome_of_experience.tooltip.empty"));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        // FIXME: Split a tome off the stack
        if (stack.getCount() > 1) {
            return TypedActionResult.pass(stack);
        }

        // Make sure the tome has custom tags
        NbtCompound tags = stack.getNbt();
        if (!stack.hasNbt()) {
            tags = new NbtCompound();
            tags.putInt("experience", 0);
        }

        int pointsPlayer = ExperienceUtils.getExperiencePoints(user);
        int pointsTome = tags.getInt("experience");

        if (!world.isClient) {

            if (user.isSneaking()) {
                // Transfer a whole XP level into the tome.

                // FIXME: Check player xp level isn't 0
                user.addExperienceLevels(-1);

                int pointsTranferred = pointsPlayer - ExperienceUtils.getExperiencePoints(user);
                pointsTome += pointsTranferred;
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
            }
        }

        return TypedActionResult.success(stack, world.isClient());
    }
}