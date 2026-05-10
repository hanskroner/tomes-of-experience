package com.hangbunny.client;

import com.hangbunny.TomesOfExperience;
import com.hangbunny.item.BaseTomeOfExperience;
import com.hangbunny.item.component.TomeComponent;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.network.chat.Component;

public class TomesOfExperienceClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
        ItemTooltipCallback.EVENT.register((itemStack, context, type, lines) -> {
            if (itemStack.getItem() instanceof BaseTomeOfExperience tome) {
                TomeComponent tomeComponent = itemStack.getOrDefault(TomesOfExperience.TOME_DATA, TomeComponent.DEFAULT);
                int tomeExperience = tomeComponent.experience();

                if (ClientInputHelper.hasShiftDown()) {
                    lines.add(Component.translatable("item.tomes_of_experience.tome_of_experience.tooltip.points", tomeExperience, tome.getCapacity()));
                }
            }
        });
	}
}