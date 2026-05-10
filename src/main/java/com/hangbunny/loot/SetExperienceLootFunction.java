package com.hangbunny.loot;

import java.util.List;
import java.util.Set;

import com.hangbunny.TomesOfExperience;
import com.hangbunny.item.component.TomeComponent;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;

public class SetExperienceLootFunction extends LootItemConditionalFunction {
     final SkewedLootNumberProvider experienceRange;

     protected SetExperienceLootFunction(List<LootItemCondition> conditions, SkewedLootNumberProvider experienceRange) {
          super(conditions);
          this.experienceRange = experienceRange;
     }

     public static final MapCodec<SetExperienceLootFunction> CODEC = RecordCodecBuilder.mapCodec(instance ->
          commonFields(instance).and(
               SkewedLootNumberProvider.CODEC.fieldOf("experience").forGetter(f -> f.experienceRange))
          .apply(instance, SetExperienceLootFunction::new)
     );

     @Override
     public MapCodec<? extends LootItemConditionalFunction> codec() {
          return CODEC;
     }

     // @Override
     // public LootItemFunctionType<? extends LootItemConditionalFunction> getType() {
     //      return TomesLootFunctionTypes.SET_EXPERIENCE;
     // }

     public Set<ContextKey<?>> getReferencedContextParams() {
          return this.experienceRange.getReferencedContextParams();
     }

     @Override
     public ItemStack run(ItemStack itemStack, LootContext context) {
          int experience = this.experienceRange.nextSkewedInt(context);
          itemStack.set(TomesOfExperience.TOME_DATA, new TomeComponent(experience));

          return itemStack;
     }

     public static LootItemConditionalFunction.Builder<?> builder(SkewedLootNumberProvider experienceRange) {
          return LootItemConditionalFunction.simpleBuilder(
               (conditions) -> new SetExperienceLootFunction(conditions, experienceRange)
          );
     }
}
