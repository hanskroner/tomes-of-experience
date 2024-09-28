package com.hangbunny.loot;

import java.util.Set;

import com.hangbunny.TomesOfExperience;
import com.hangbunny.item.component.TomeComponent;

import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;

import java.util.List;

public class SetExperienceLootFunction extends ConditionalLootFunction {
     final SkewedLootNumberProvider experienceRange;

     protected SetExperienceLootFunction(List<LootCondition> conditions, SkewedLootNumberProvider experienceRange) {
          super(conditions);
          this.experienceRange = experienceRange;
     }

     public LootFunctionType<? extends ConditionalLootFunction> getType() {
          return LootFunctionTypes.SET_COMPONENTS;
     }

     public Set<LootContextParameter<?>> getRequiredParameters() {
          return this.experienceRange.getRequiredParameters();
     }

     public ItemStack process(ItemStack itemStack, LootContext context) {
          int experience = this.experienceRange.nextSkewedInt(context);
          itemStack.set(TomesOfExperience.TOME_DATA, new TomeComponent(experience));

          return itemStack;
     }

     public static ConditionalLootFunction.Builder<?> builder(SkewedLootNumberProvider experienceRange) {
          return builder((conditions) -> {
               return new SetExperienceLootFunction(conditions, experienceRange);
          });
     }
}
