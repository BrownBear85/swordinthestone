package com.bonker.swordinthestone.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.logging.LogUtils;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class AnimatedTextureProvider implements DataProvider {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final DataGenerator generator;
    private final String modid;
    private final List<AnimatedTextureBuilder> data = new ArrayList<>();

    public AnimatedTextureProvider(DataGenerator generator, String modid) {
        this.generator = generator;
        this.modid = modid;
    }

    protected abstract void addFiles();

    @Override
    public void run(HashCache pCache) {
        addFiles();
        try {
            generateAll(pCache);
        } catch (IOException exception) {
            LOGGER.error("Something went wrong creating animated texture mcmeta files", exception);
        }
    }

    protected void generateAll(HashCache cache) throws IOException {
        for (AnimatedTextureBuilder builder : data) {
            Path target = generator.getOutputFolder().resolve(modid).resolve("textures").resolve(builder.path + ".png.mcmeta");
            DataProvider.save(SSDatagen.GSON, cache, builder.toJson(), target);
        }
    }

    @Override
    public String getName() {
        return "Animated Textures:" + modid;
    }

    public AnimatedTextureBuilder create(String path) {
        AnimatedTextureBuilder builder = new AnimatedTextureBuilder(path);
        data.add(builder);
        return builder;
    }

    public static class AnimatedTextureBuilder {
        private final String path;
        private int frametime;
        private JsonArray frames;

        public AnimatedTextureBuilder(String path) { // ex. block/texture or item/texture
            this.path = path;
        }

        public JsonObject toJson() {
            JsonObject root = new JsonObject();
            JsonObject animation = new JsonObject();
            root.add("animation", animation);
            animation.add("frametime", new JsonPrimitive(frametime));
            if (frames != null) {
                animation.add("frames", frames);
            }
            return root;
        }

        public AnimatedTextureBuilder frametime(int frametime) {
            this.frametime = frametime;
            return this;
        }

        public AnimatedTextureBuilder frames(@Nullable int[] framesArr) {
            if (framesArr == null) return this;
            frames = new JsonArray();
            for (int i : framesArr) {
                frames.add(new JsonPrimitive(i));
            }
            return this;
        }
    }
}
