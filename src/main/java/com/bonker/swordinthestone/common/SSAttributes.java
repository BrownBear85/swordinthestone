package com.bonker.swordinthestone.common;

import com.bonker.swordinthestone.SwordInTheStone;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SSAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, SwordInTheStone.MODID);

    public static final DeferredHolder<Attribute, Attribute> JUMPS = ATTRIBUTES.register("extra_jumps",
            () -> new RangedAttribute("attribute.swordinthestone.extra_jumps", 0, 0, 64));
}
