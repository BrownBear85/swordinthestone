package com.bonker.swordinthestone.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import static com.bonker.swordinthestone.SwordInTheStone.MODID;

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
public class SSDatagen {
    @SubscribeEvent
    public static void gatherData(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        PackOutput packOutput = generator.getPackOutput();

        SSLanguageProvider languageProvider = new SSLanguageProvider(packOutput, MODID, "en_us");
        SSAnimatedTextureProvider animatedTextureProvider = new SSAnimatedTextureProvider(packOutput, MODID);
        SSBlockStateProvider blockStateProvider = new SSBlockStateProvider(packOutput, MODID, existingFileHelper);

        generator.addProvider(true, new SSItemModelProvider(packOutput, MODID, existingFileHelper, languageProvider, animatedTextureProvider, blockStateProvider));
        generator.addProvider(true, new SSSoundProvider(packOutput, MODID, existingFileHelper, languageProvider));
        generator.addProvider(true, languageProvider);
        generator.addProvider(true, animatedTextureProvider);
        generator.addProvider(true, blockStateProvider);
    }
}
