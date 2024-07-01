package com.bonker.swordinthestone.common.block;

import com.bonker.swordinthestone.SwordInTheStone;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SSBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SwordInTheStone.MODID);

    public static final DeferredBlock<SwordStoneBlock> SWORD_STONE = BLOCKS.register("sword_stone",
            () -> new SwordStoneBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(-1.0F, 3600000.0F)
                    .noLootTable()));
}
