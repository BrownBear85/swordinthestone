package com.bonker.swordinthestone.server.worldgen;

import com.bonker.swordinthestone.SwordInTheStone;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class SSWorldGen {
    public static final DeferredRegister<StructurePlacementType<?>> STRUCTURE_PLACEMENT_TYPES =
            DeferredRegister.create(Registry.STRUCTURE_PLACEMENT_TYPE_REGISTRY, SwordInTheStone.MODID);

    public static final RegistryObject<StructurePlacementType<SwordStonePlacement>> SWORD_STONE_PLACEMENT =
            STRUCTURE_PLACEMENT_TYPES.register("sword_stone_placement", () -> () -> SwordStonePlacement.CODEC);
}
