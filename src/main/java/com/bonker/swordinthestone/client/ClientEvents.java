package com.bonker.swordinthestone.client;

import com.bonker.swordinthestone.SwordInTheStone;
import com.bonker.swordinthestone.client.gui.SSGuiOverlay;
import com.bonker.swordinthestone.client.particle.*;
import com.bonker.swordinthestone.client.renderer.SSBEWLR;
import com.bonker.swordinthestone.client.renderer.SwordStoneBlockEntityRenderer;
import com.bonker.swordinthestone.client.renderer.entity.EnderRiftRenderer;
import com.bonker.swordinthestone.client.renderer.entity.SpellFireballRenderer;
import com.bonker.swordinthestone.common.ability.SwordAbilities;
import com.bonker.swordinthestone.common.ability.SwordAbility;
import com.bonker.swordinthestone.common.block.entity.SSBlockEntities;
import com.bonker.swordinthestone.common.entity.SSEntityTypes;
import com.bonker.swordinthestone.common.item.SSItems;
import com.bonker.swordinthestone.common.item.UniqueSwordItem;
import com.bonker.swordinthestone.common.networking.payloads.Play2ServerExtraJumpPayload;
import com.bonker.swordinthestone.util.AbilityUtil;
import com.bonker.swordinthestone.util.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.lwjgl.glfw.GLFW;

@SuppressWarnings("unused")
public class ClientEvents {
    private static Minecraft minecraft;

    @EventBusSubscriber(modid = SwordInTheStone.MODID, value = Dist.CLIENT)
    public static class ForgeBus {
        @SubscribeEvent
        public static void onTooltipColors(final RenderTooltipEvent.Color event) {
            if (event.getItemStack().getItem() instanceof UniqueSwordItem uniqueSwordItem) {
                SwordAbility ability = AbilityUtil.getSwordAbility(event.getItemStack());
                if (ability == SwordAbility.NONE) return;

                Color color = UniqueSwordItem.COLOR_TABLE.get(uniqueSwordItem, ability);
                if (color == null) return;

                color.applyTooltipColors(event);
            }
        }

        @SubscribeEvent
        public static void onKeyInput(final InputEvent.Key event) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null &&
                    event.getAction() == GLFW.GLFW_PRESS &&
                    event.getKey() == minecraft.options.keyJump.getKey().getValue() &&
                    !player.onGround() && !player.isFallFlying() &&
                    AbilityUtil.isPassiveActive(player, SwordAbilities.DOUBLE_JUMP.get())) {
                PacketDistributor.sendToServer(new Play2ServerExtraJumpPayload(player.input.left, player.input.right, player.input.up, player.input.down));
            }
        }
    }

    @EventBusSubscriber(modid = SwordInTheStone.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModBus {
        @SubscribeEvent
        public static void onClientSetup(final FMLClientSetupEvent event) {
            minecraft = Minecraft.getInstance();
            SSBEWLR.INSTANCE = new SSBEWLR(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels());
        }

        @SubscribeEvent
        public static void onRegisterEntityRenderers(final EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(SSEntityTypes.HEIGHT_AREA_EFFECT_CLOUD.get(), NoopRenderer::new);
            event.registerEntityRenderer(SSEntityTypes.ENDER_RIFT.get(), EnderRiftRenderer::new);
            event.registerEntityRenderer(SSEntityTypes.SPELL_FIREBALL.get(), SpellFireballRenderer::new);

            event.registerBlockEntityRenderer(SSBlockEntities.SWORD_STONE_MASTER.get(), SwordStoneBlockEntityRenderer::new);
        }

        @SubscribeEvent
        public static void onRegisterAdditionalModels(final ModelEvent.RegisterAdditional event) {
            for (ResourceLocation ability : SwordAbilities.SWORD_ABILITY_REGISTRY.keySet()) {
                event.register(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(ability.getNamespace(), "item/ability/" + ability.getPath())));
            }
            for (DeferredHolder<Item, ? extends Item> regObj : SSItems.ITEMS.getEntries()) {
                if (!(regObj.get() instanceof UniqueSwordItem)) continue;
                ResourceLocation loc = BuiltInRegistries.ITEM.getKey(regObj.get());
                event.register(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), "item/sword/" + loc.getPath())));
            }
        }

        @SubscribeEvent
        public static void onRegisterParticleProviders(final RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(SSParticles.HEAL.get(), HealParticle.Provider::new);
            event.registerSpriteSet(SSParticles.FIRE.get(), FireParticle.Provider::new);
            event.registerSpriteSet(SSParticles.AIR.get(), AirParticle.Provider::new);
            event.registerSpriteSet(SSParticles.VORTEX.get(), VortexParticle.Provider::new);
        }

        @SubscribeEvent
        public static void onRegisterGuiOverlays(final RegisterGuiLayersEvent event) {
            event.registerAboveAll(SSGuiOverlay.NAME, SSGuiOverlay.OVERLAY);
        }
    }
}
