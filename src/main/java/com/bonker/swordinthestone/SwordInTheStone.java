package com.bonker.swordinthestone;

import com.bonker.swordinthestone.client.particle.SSParticles;
import com.bonker.swordinthestone.common.SSAttributes;
import com.bonker.swordinthestone.common.SSConfig;
import com.bonker.swordinthestone.common.SSSounds;
import com.bonker.swordinthestone.common.ability.SwordAbilities;
import com.bonker.swordinthestone.common.block.SSBlocks;
import com.bonker.swordinthestone.common.block.entity.SSBlockEntities;
import com.bonker.swordinthestone.common.entity.SSEntityTypes;
import com.bonker.swordinthestone.common.item.SSDataComponents;
import com.bonker.swordinthestone.common.item.SSItems;
import com.bonker.swordinthestone.server.attachment.SSAttachments;
import com.bonker.swordinthestone.server.worldgen.SSWorldGen;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLPaths;

import java.util.HashMap;
import java.util.Map;

@Mod(SwordInTheStone.MODID)
public class SwordInTheStone {

    public static final String MODID = "swordinthestone";

    public static final Map<String, ModelResourceLocation> ABILITY_MODEL_MAP = new HashMap<>();
    public static final Map<ResourceLocation, ModelResourceLocation> SWORD_MODEL_MAP = new HashMap<>();

    public SwordInTheStone(IEventBus bus, ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, SSConfig.COMMON_CONFIG);
        SSConfig.load(FMLPaths.CONFIGDIR.get().resolve(SwordInTheStone.MODID + "-common.toml"));

        SwordAbilities.SWORD_ABILITIES.register(bus);
        SSBlocks.BLOCKS.register(bus);
        SSBlockEntities.BLOCK_ENTITIES.register(bus);
        SSItems.ITEMS.register(bus);
        SSItems.TABS.register(bus);
        SSDataComponents.DATA_COMPONENT_TYPES.register(bus);
        SSSounds.SOUND_EVENTS.register(bus);
        SSParticles.PARTICLE_TYPES.register(bus);
        SSEntityTypes.ENTITY_TYPES.register(bus);
        SSAttachments.ATTACHMENT_TYPES.register(bus);
        SSAttributes.ATTRIBUTES.register(bus);
        SSWorldGen.STRUCTURE_PLACEMENT_TYPES.register(bus);
    }
}
