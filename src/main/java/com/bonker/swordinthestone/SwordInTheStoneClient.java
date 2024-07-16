package com.bonker.swordinthestone;

import com.bonker.swordinthestone.util.Util;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

@Mod(value = SwordInTheStone.MODID, dist = Dist.CLIENT)
public class SwordInTheStoneClient {
    public static final Map<String, ModelResourceLocation> ABILITY_MODEL_MAP = new HashMap<>();
    public static final Map<ResourceLocation, ModelResourceLocation> SWORD_MODEL_MAP = new HashMap<>();

    public SwordInTheStoneClient(IEventBus bus, ModContainer container) {

    }

    public static void addAbility(String name) {
        ABILITY_MODEL_MAP.put(SwordInTheStone.MODID + ":" + name, ModelResourceLocation.standalone(Util.makeResource("item/ability/" + name)));
    }

    public static void addSword(String name) {
        SWORD_MODEL_MAP.put(Util.makeResource(name), ModelResourceLocation.standalone(Util.makeResource("item/sword/" + name)));
    }
}
