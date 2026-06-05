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

                if (tomeExperience > 0) {
                    int percentFull = (int) Math.floor(((float) tomeExperience / tome.getCapacity()) * 10);
                    String key = switch (percentFull) {
                        case 0, 1 -> "0-20";
                        case 2, 3 -> "20-40";
                        case 4, 5 -> "40-60";
                        case 6, 7 -> "60-80";
                        case 8, 9 -> "80-100";
                        default   -> "100";
                    };
                    lines.add(Component.translatable("item.tomes_of_experience.tome_of_experience.tooltip." + key));
                } else {
                    lines.add(Component.translatable("item.tomes_of_experience.tome_of_experience.tooltip.0"));
                }

                if (ClientInputHelper.hasShiftDown()) {
                    lines.add(Component.translatable("item.tomes_of_experience.tome_of_experience.tooltip.points", tomeExperience, tome.getCapacity()));
                }
            }
        });
    }
}