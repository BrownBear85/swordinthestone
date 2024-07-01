package com.bonker.swordinthestone.datagen;

import com.bonker.swordinthestone.common.block.SSBlocks;
import com.bonker.swordinthestone.common.block.SwordStoneBlock;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.HashMap;
import java.util.Map;

public class SSBlockStateProvider extends BlockStateProvider {
    final Map<String, ModelFile> swordStoneVariants = new HashMap<>();

    public SSBlockStateProvider(PackOutput output, String modid, ExistingFileHelper exFileHelper) {
        super(output, modid, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        getVariantBuilder(SSBlocks.SWORD_STONE.get())
                .forAllStatesExcept(state ->
                            ConfiguredModel.builder()
                                    .modelFile(swordStoneVariants.get(state.getValue(SwordStoneBlock.VARIANT).getSerializedName()))
                                    .rotationY((int) state.getValue(SwordStoneBlock.FACING).getOpposite().toYRot())
                                    .build()
                    , SwordStoneBlock.IS_DUMMY);
    }
}
