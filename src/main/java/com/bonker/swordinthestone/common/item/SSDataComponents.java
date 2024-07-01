package com.bonker.swordinthestone.common.item;

import com.bonker.swordinthestone.SwordInTheStone;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SSDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, SwordInTheStone.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> LAST_USED_TICK_COMPONENT = DATA_COMPONENT_TYPES.register("last_used_tick",
            () -> DataComponentType.<Long>builder().persistent(Codec.LONG).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> CHARGE_COMPONENT = DATA_COMPONENT_TYPES.register("charge",
            () -> DataComponentType.<Integer>builder().persistent(Codec.INT).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> ABILITY_COMPONENT = DATA_COMPONENT_TYPES.register("ability",
            () -> DataComponentType.<String>builder().persistent(Codec.STRING).build());
}
