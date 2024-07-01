package com.bonker.swordinthestone.common.block.entity;

import com.bonker.swordinthestone.SwordInTheStone;
import com.bonker.swordinthestone.common.block.SSBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SSBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SwordInTheStone.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SwordStoneMasterBlockEntity>> SWORD_STONE_MASTER = register("sword_stone_master",
            () -> BlockEntityType.Builder.of(SwordStoneMasterBlockEntity::new, SSBlocks.SWORD_STONE.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SwordStoneDummyBlockEntity>> SWORD_STONE_DUMMY = register("sword_stone_dummy",
            () -> BlockEntityType.Builder.of(SwordStoneDummyBlockEntity::new, SSBlocks.SWORD_STONE.get()));

    @SuppressWarnings("DataFlowIssue") // suppress passing null for the unused datatype parameter
    private static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> register(String pKey, Supplier<BlockEntityType.Builder<T>> builderSupplier) {
        return BLOCK_ENTITIES.register(pKey, () -> builderSupplier.get().build(null));
    }
}
