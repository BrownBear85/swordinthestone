package com.bonker.swordinthestone.server.worldgen;

import com.bonker.swordinthestone.SwordInTheStone;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SSWorldGen {
    public static final DeferredRegister<StructurePlacementType<?>> STRUCTURE_PLACEMENT_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_PLACEMENT, SwordInTheStone.MODID);

    public static final DeferredHolder<StructurePlacementType<?>, StructurePlacementType<SwordStonePlacement>> SWORD_STONE_PLACEMENT =
            STRUCTURE_PLACEMENT_TYPES.register("sword_stone_placement", () -> () -> SwordStonePlacement.CODEC);
}
