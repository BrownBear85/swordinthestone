package com.bonker.swordinthestone.common;

import com.bonker.swordinthestone.SwordInTheStone;
import com.bonker.swordinthestone.client.gui.SSItemDecorator;
import com.bonker.swordinthestone.client.particle.SSParticles;
import com.bonker.swordinthestone.common.ability.SwordAbilities;
import com.bonker.swordinthestone.common.ability.SwordAbility;
import com.bonker.swordinthestone.common.entity.BatSwarmGoal;
import com.bonker.swordinthestone.common.entity.EnderRift;
import com.bonker.swordinthestone.common.entity.HeightAreaEffectCloud;
import com.bonker.swordinthestone.common.item.SSItems;
import com.bonker.swordinthestone.common.item.UniqueSwordItem;
import com.bonker.swordinthestone.common.networking.payloads.*;
import com.bonker.swordinthestone.server.attachment.DashAttachment;
import com.bonker.swordinthestone.server.attachment.ExtraJumpsAttachment;
import com.bonker.swordinthestone.server.attachment.SSAttachments;
import com.bonker.swordinthestone.server.command.SSCommands;
import com.bonker.swordinthestone.util.AbilityUtil;
import com.bonker.swordinthestone.util.Color;
import com.bonker.swordinthestone.util.DoubleJumpEvent;
import com.bonker.swordinthestone.util.Util;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@SuppressWarnings("unused")
public class CommonEvents {
    @EventBusSubscriber(modid = SwordInTheStone.MODID)
    public static class ForgeBus {
        @SubscribeEvent
        public static void onCommandsRegistered(final RegisterCommandsEvent event) {
            SSCommands.register(event.getDispatcher());
        }

        @SubscribeEvent
        public static void onEntityInvulnerabilityCheck(final EntityInvulnerabilityCheckEvent event) {
            if (event.getEntity().invulnerableTime > 0) return;

            if (!(event.getEntity().getType() == EntityType.PLAYER)) {
                if (event.getSource().is(DamageTypes.MOB_ATTACK) &&
                        event.getEntity() instanceof LivingEntity target &&
                        event.getSource().getEntity() instanceof LivingEntity attacker) {
                    ItemStack stack = event.getSource().getWeaponItem();
                    if (stack != null && stack.getItem() instanceof UniqueSwordItem uniqueSwordItem) {
                        uniqueSwordItem.hurtEnemy(stack, target, attacker);
                    }
                }
            }

            if (event.getEntity() instanceof ServerPlayer player &&
                    player.getData(SSAttachments.DASH).getDashTicks() > 0 &&
                    event.getSource().is(DamageTypes.MOB_ATTACK)) {
                event.setInvulnerable(true);
            }
        }

        @SubscribeEvent
        public static void onPlayerTick(final PlayerTickEvent.Pre event) {
            Player player = event.getEntity();

            DashAttachment dashCap = player.getData(SSAttachments.DASH);
            int dashTicks = dashCap.getDashTicks();
            if (dashTicks > 0) {
                dashTicks--;

                if (player.getDeltaMovement().lengthSqr() < 0.01) dashTicks = 0;

                if (!event.getEntity().level().isClientSide && dashTicks > 0) {
                    HeightAreaEffectCloud.createToxicDashCloud(player.level(), player, player.getX(), player.getY() - 0.5, player.getZ());
                }

                if (dashTicks > 0 && event.getEntity().level().isClientSide) {
                    player.level().getEntities(player, player.getBoundingBox().inflate(0.5)).forEach(entity -> {
                        if (entity instanceof LivingEntity && !dashCap.isDashed(entity)) {
                            dashCap.addToDashed(entity);
                            PacketDistributor.sendToServer(new Play2ServerDashAttackPayload(entity.getId()));
                        }
                    });
                    dashCap.clearDashed();
                }
                dashCap.setDashTicks(dashTicks);
            }

            ExtraJumpsAttachment extraJumps = player.getData(SSAttachments.EXTRA_JUMPS);
            if (player.getVehicle() == null || !SSConfig.DOUBLE_JUMP_VEHICLE.get()) {
                if (player.onGround()) {
                    extraJumps.resetExtraJumps();
                }
            } else if ((player.getVehicle().onGround() || player.getVehicle().getBlockStateOn().is(Blocks.WATER)) && player.level().getGameTime() % 5 == 0) {
                extraJumps.resetExtraJumps();
            }
        }

        @SubscribeEvent
        public static void onLivingDeath(final LivingDeathEvent event) {
            if (event.getEntity() instanceof ServerPlayer player) {
                Util.getOwnedProjectiles(player, EnderRift.class, player.serverLevel()).forEach(Entity::discard);
            }
        }

        @SubscribeEvent
        public static void onLivingFall(final LivingFallEvent event) {
            if (AbilityUtil.isPassiveActive(event.getEntity(), SwordAbilities.DOUBLE_JUMP.get())) {
                float distance = event.getDistance();
                if (distance >= 3) {
                    if (distance <= 7) event.getEntity().playSound(SoundEvents.GENERIC_SMALL_FALL, 0.5F, 1.0F);

                    event.getEntity().playSound(SSSounds.LAND.get(), 0.3F, 0.6F + event.getEntity().level().random.nextFloat() * 0.8F);

                    if (event.getEntity().level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(SSParticles.AIR.get(), event.getEntity().getX(), event.getEntity().getY() + 0.1, event.getEntity().getZ(), 20, 0.5, 0.1, 0.5, 0.05);
                    }
                }

                event.setDistance(distance - 4);
                event.setDamageMultiplier(0.6F);
            }
        }

        @SubscribeEvent
        public static void onDoubleJump(final DoubleJumpEvent event) {
            Vec3 pos = event.getEntity().position();
            ServerLevel level = (ServerLevel) event.getEntity().level();
            level.sendParticles(SSParticles.AIR.get(), pos.x, pos.y, pos.z, 20, 0.5, 0.1, 0.5, 0.05);
            level.playSound(null, pos.x, pos.y, pos.z, SSSounds.JUMP.get(), SoundSource.PLAYERS, 0.7F, 0.8F + event.getEntity().level().random.nextFloat() * 0.4F);
        }

        @SubscribeEvent
        public static void onEntityJoinLevel(final EntityJoinLevelEvent event) {
            if (event.loadedFromDisk() && event.getEntity().getTags().contains(BatSwarmGoal.BAT_SWARM)) {
                event.getEntity().setPos(Vec3.ZERO);
                event.getEntity().kill();
            }
        }

        @SubscribeEvent
        public static void onLivingDrops(final LivingDropsEvent event) {
            if (event.getEntity().getTags().contains(BatSwarmGoal.BAT_SWARM)) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onLivingExperienceDrop(final LivingExperienceDropEvent event) {
            if (event.getEntity().getTags().contains(BatSwarmGoal.BAT_SWARM)) {
                event.setCanceled(true);
            }
        }
    }

    @EventBusSubscriber(modid = SwordInTheStone.MODID, bus = EventBusSubscriber.Bus.MOD)
    public static class ModBus {
        @SubscribeEvent
        public static void onCommonSetup(final FMLCommonSetupEvent event) {
            for (DeferredHolder<Item, ? extends Item> itemObj : SSItems.ITEMS.getEntries()) {
                if (itemObj.get() instanceof UniqueSwordItem uniqueSwordItem) {
                    for (DeferredHolder<SwordAbility, ? extends SwordAbility> abilityObj : SwordAbilities.SWORD_ABILITIES.getEntries()) {
                        UniqueSwordItem.COLOR_TABLE.put(uniqueSwordItem, abilityObj.get(), Color.uniqueSwordColor(abilityObj.get().getColor().getValue(), uniqueSwordItem.getColor()));
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onNewRegistries(final NewRegistryEvent event) {
            event.register(SwordAbilities.SWORD_ABILITY_REGISTRY);
        }

        @SubscribeEvent
        public static void onRegisterPayloadHandlers(final RegisterPayloadHandlersEvent event) {
            PayloadRegistrar registrar = event.registrar("2.0");

            BidirectionalEnderRiftPayload.register(registrar);
            Play2ClientDeltaPayload.register(registrar);
            Play2ClientSwordStoneDataPayload.register(registrar);
            Play2ClientSwordStoneItemPayload.register(registrar);
            Play2ServerDashAttackPayload.register(registrar);
            Play2ServerExtraJumpPayload.register(registrar);
        }

        @SubscribeEvent
        public static void onAttributeModification(final EntityAttributeModificationEvent event) {
            event.add(EntityType.PLAYER, SSAttributes.JUMPS, 0);
        }

        @SubscribeEvent
        public static void onRegisterItemDecorations(final RegisterItemDecorationsEvent event) {
            for (DeferredHolder<Item, ? extends Item> obj : SSItems.ITEMS.getEntries()) {
                if (obj.get() instanceof UniqueSwordItem) {
                    event.register(obj.get(), SSItemDecorator.ITEM_DECORATOR);
                }
            }
        }
    }
}
