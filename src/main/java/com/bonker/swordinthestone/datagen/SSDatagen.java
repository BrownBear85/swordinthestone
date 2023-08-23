package com.bonker.swordinthestone.datagen;

import com.google.gson.Gson;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

import static com.bonker.swordinthestone.SwordInTheStone.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SSDatagen {
    static Gson GSON = new Gson();

    @SubscribeEvent
    public static void gatherData(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        SSLanguageProvider languageProvider = new SSLanguageProvider(generator, MODID, "en_us");
        SSAnimatedTextureProvider animatedTextureProvider = new SSAnimatedTextureProvider(generator, MODID);
        SSBlockStateProvider blockStateProvider = new SSBlockStateProvider(generator, MODID, existingFileHelper);

        generator.addProvider(new SSItemModelProvider(generator, MODID, existingFileHelper, languageProvider, animatedTextureProvider, blockStateProvider));
        generator.addProvider(new SSSoundProvider(generator, MODID, existingFileHelper, languageProvider));
        generator.addProvider(languageProvider);
        generator.addProvider(animatedTextureProvider);
        generator.addProvider(blockStateProvider);
    }
}
