package com.bonker.swordinthestone.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.bonker.swordinthestone.SwordInTheStone.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SSDatagen {
    @SubscribeEvent
    public static void gatherData(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        SSLanguageProvider languageProvider = new SSLanguageProvider(generator, MODID, "en_us");
        SSAnimatedTextureProvider animatedTextureProvider = new SSAnimatedTextureProvider(generator, MODID);
        SSBlockStateProvider blockStateProvider = new SSBlockStateProvider(generator, MODID, existingFileHelper);

        generator.addProvider(true, new SSItemModelProvider(generator, MODID, existingFileHelper, languageProvider, animatedTextureProvider, blockStateProvider));
        generator.addProvider(true, new SSSoundProvider(generator, MODID, existingFileHelper, languageProvider));
        generator.addProvider(true, languageProvider);
        generator.addProvider(true, animatedTextureProvider);
        generator.addProvider(true, blockStateProvider);
    }
}
