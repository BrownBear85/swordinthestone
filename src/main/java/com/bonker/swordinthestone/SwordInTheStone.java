package com.bonker.swordinthestone;

import com.bonker.swordinthestone.client.particle.SSParticles;
import com.bonker.swordinthestone.common.SSAttributes;
import com.bonker.swordinthestone.common.SSSounds;
import com.bonker.swordinthestone.common.ability.SwordAbilities;
import com.bonker.swordinthestone.common.block.SSBlocks;
import com.bonker.swordinthestone.common.block.entity.SSBlockEntities;
import com.bonker.swordinthestone.common.entity.SSEntityTypes;
import com.bonker.swordinthestone.common.item.SSItems;
import com.bonker.swordinthestone.common.networking.SSNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.HashMap;
import java.util.Map;

@Mod(SwordInTheStone.MODID)
public class SwordInTheStone {

    public static final String MODID = "swordinthestone";

    public static final Map<String, ResourceLocation> ABILITY_MODEL_MAP = new HashMap<>();
    public static final Map<ResourceLocation, ResourceLocation> SWORD_MODEL_MAP = new HashMap<>();

    public SwordInTheStone() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        SwordAbilities.SWORD_ABILITIES.register(bus);
        SSBlocks.BLOCKS.register(bus);
        SSBlockEntities.BLOCK_ENTITIES.register(bus);
        SSItems.ITEMS.register(bus);
        SSSounds.SOUND_EVENTS.register(bus);
        SSParticles.PARTICLE_TYPES.register(bus);
        SSEntityTypes.ENTITY_TYPES.register(bus);
        SSAttributes.ATTRIBUTES.register(bus);
        SSNetworking.register();
    }
}
