package com.bonker.swordinthestone.common.ability;

import com.bonker.swordinthestone.SwordInTheStone;
import com.bonker.swordinthestone.client.particle.SSParticles;
import com.bonker.swordinthestone.common.SSAttributes;
import com.bonker.swordinthestone.common.SSConfig;
import com.bonker.swordinthestone.common.SSSounds;
import com.bonker.swordinthestone.common.entity.BatSwarmGoal;
import com.bonker.swordinthestone.common.entity.EnderRift;
import com.bonker.swordinthestone.common.entity.SpellFireball;
import com.bonker.swordinthestone.common.networking.payloads.Play2ClientDeltaPayload;
import com.bonker.swordinthestone.server.attachment.SSAttachments;
import com.bonker.swordinthestone.util.AbilityUtil;
import com.bonker.swordinthestone.util.SideUtil;
import com.bonker.swordinthestone.util.Util;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class SwordAbilities {
    public static final ResourceKey<Registry<SwordAbility>> REGISTRY_KEY = ResourceKey.createRegistryKey(Util.makeResource("sword_abilities"));
    public static final Registry<SwordAbility> SWORD_ABILITY_REGISTRY = new RegistryBuilder<>(REGISTRY_KEY).create();
    public static final DeferredRegister<SwordAbility> SWORD_ABILITIES = DeferredRegister.create(SWORD_ABILITY_REGISTRY, SwordInTheStone.MODID);

    // Thunder Smite
    public static final DeferredHolder<SwordAbility, SwordAbility> THUNDER_SMITE = register("thunder_smite",
            () -> new SwordAbilityBuilder(0x57faf6)
                    .onHit((level, holder, victim) -> {
                        if (holder instanceof Player player && player.getAttackStrengthScale(0) < 0.75F) {
                            return;
                        }

                        ItemStack stack = holder.getItemInHand(InteractionHand.MAIN_HAND);
                        int charge = AbilityUtil.getCharge(stack);
                        level.sendParticles(ParticleTypes.ELECTRIC_SPARK, victim.getX(), victim.getY() + 1.0, victim.getZ(), 20, 0.7, 1, 0.7, 0.4);
                        level.playSound(null, holder.getX(), holder.getY(), holder.getZ(), SSSounds.ZAP.get(), SoundSource.PLAYERS, 2.0F, 2.0F - charge * 0.5F);
                        if (++charge > SSConfig.THUNDER_SMITE_CHARGES.get()) {
                            LightningBolt bolt = EntityType.LIGHTNING_BOLT.spawn(level, entity -> {
                                entity.setVisualOnly(true);
                                if (holder instanceof ServerPlayer serverPlayer) entity.setCause(serverPlayer);
                            }, victim.blockPosition(), MobSpawnType.MOB_SUMMONED, false, false);
                            if (bolt != null) {
                                List<Entity> list = level.getEntities(bolt, new AABB(bolt.getX() - 3.0D, bolt.getY() - 3.0D, bolt.getZ() - 3.0D, bolt.getX() + 3.0D, bolt.getY() + 6.0D + 3.0D, bolt.getZ() + 3.0D), Entity::isAlive);
                                for (Entity entity : list) {
                                    if (entity == holder) continue;
                                    if (!EventHooks.onEntityStruckByLightning(entity, bolt)) entity.thunderHit(level, bolt);
                                }
                            }
                            charge = 0;
                        }
                        AbilityUtil.setCharge(stack, charge);
                    })
                    .customBar(stack -> false,
                            stack -> (float) AbilityUtil.getCharge(stack) / SSConfig.THUNDER_SMITE_CHARGES.get())
                    .build());

    // Vampiric
    public static final DeferredHolder<SwordAbility, SwordAbility> VAMPIRIC = register("vampiric",
            () -> new SwordAbilityBuilder(0xe20028)
                    .onKill((level, holder, victim) -> {
                        float healing = Mth.clamp(victim.getMaxHealth() * SSConfig.VAMPIRIC_HEALTH_PERCENT.get().floatValue(), 1, SSConfig.VAMPIRIC_HEALTH_CAP.get());
                        int particles = Mth.clamp(Math.round(healing * 3), 4, 20);
                        holder.heal(healing);
                        level.sendParticles(SSParticles.HEAL.get(), victim.getX(), victim.getY() + victim.getBbHeight() * 0.5, victim.getZ(), particles, victim.getBbWidth() * 0.2, victim.getBbHeight() * 0.2, victim.getBbWidth() * 0.2, 0);
                    })
                    .build());

    // Toxic Dash
    public static final DeferredHolder<SwordAbility, SwordAbility> TOXIC_DASH = register("toxic_dash",
            () -> new SwordAbilityBuilder(0x52c539)
                    .onUse((level, player, usedHand) -> {
                        ItemStack stack = player.getItemInHand(usedHand);
                        if (AbilityUtil.isOnCooldown(stack, level, SSConfig.TOXIC_DASH_COOLDOWN.get())) return InteractionResultHolder.fail(stack);

                        level.playSound(player, player.getX(), player.getY(), player.getZ(), SSSounds.DASH.get(), SoundSource.PLAYERS, 2.0F, 0.8F + level.random.nextFloat() * 0.4F);
                        level.playSound(player, player.getX(), player.getY(), player.getZ(), SSSounds.TOXIC.get(), SoundSource.PLAYERS, 2.0F, 0.8F + level.random.nextFloat() * 0.4F);

                        player.getData(SSAttachments.DASH).setDashTicks(10);

                        Vec3 delta;
                        if (player.isUnderWater()) {
                            delta = Util.calculateViewVector(player.getXRot(), player.getYRot()).scale(2);
                        } else {
                            delta = Util.calculateViewVector(Math.min(0, player.getXRot()), player.getYRot()).multiply(3, 1.2, 3);
                        }

                        if (!level.isClientSide) {
                            player.push(delta.x, delta.y, delta.z);
                            Vec3 sendDelta = player.getDeltaMovement();
                            PacketDistributor.sendToPlayer((ServerPlayer) player, new Play2ClientDeltaPayload(sendDelta.x(), sendDelta.y(), sendDelta.z(), -1));
                        }

                        AbilityUtil.setOnCooldown(stack, level);
                        return InteractionResultHolder.success(stack);
                    })
                    .addCooldown(SSConfig.TOXIC_DASH_COOLDOWN)
                    .build());

    // Ender Rift
    public static final DeferredHolder<SwordAbility, SwordAbility> ENDER_RIFT = register("ender_rift",
            () -> new SwordAbilityBuilder(0xe434ff)
                    .onUse((level, player, usedHand) -> {
                        ItemStack stack = player.getItemInHand(usedHand);
                        if (AbilityUtil.isOnCooldown(stack, level, SSConfig.ENDER_RIFT_COOLDOWN.get())) return InteractionResultHolder.fail(stack);

                        if (!level.isClientSide) {
                            EnderRift enderRift = new EnderRift(level, player);
                            level.addFreshEntity(enderRift);
                        }

                        level.playSound(player, player.getX(), player.getY(), player.getZ(), SSSounds.RIFT.get(), SoundSource.PLAYERS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);

                        return InteractionResultHolder.pass(stack);
                    })
                    .onReleaseUsing((stack, level, entity, ticks) -> {
                        if (AbilityUtil.isOnCooldown(stack, level, SSConfig.ENDER_RIFT_COOLDOWN.get())) return;

                        if (!level.isClientSide) {
                            if (SSConfig.ENDER_RIFT_DURATION.get() - ticks > 4) {
                                Util.getOwnedProjectiles(entity, EnderRift.class, (ServerLevel) level).forEach(EnderRift::teleport);
                            } else {
                                Util.getOwnedProjectiles(entity, EnderRift.class, (ServerLevel) level).forEach(e -> e.getEntityData().set(EnderRift.DATA_CONTROLLING, false));
                            }
                        }

                        AbilityUtil.setOnCooldown(stack, level);
                    })
                    .useDuration(stack -> SSConfig.ENDER_RIFT_DURATION.get())
                    .addCooldown(SSConfig.ENDER_RIFT_COOLDOWN)
                    .useAnimation(stack -> UseAnim.BLOCK)
                    .build());

    // Fireball
    public static final DeferredHolder<SwordAbility, SwordAbility> FIREBALL = register("fireball",
            () -> new SwordAbilityBuilder(0xff4b25)
                    .onUse((level, player, usedHand) -> {
                        ItemStack stack = player.getItemInHand(usedHand);
                        if (AbilityUtil.isOnCooldown(stack, level, SSConfig.FIREBALL_COOLDOWN.get())) return InteractionResultHolder.fail(stack);

                        if (!level.isClientSide) {
                            SpellFireball fireball = new SpellFireball(level, player);
                            fireball.setPower(0.1F);
                            fireball.setPos(player.getEyePosition().add(player.getLookAngle().scale(1.5)));
                            level.addFreshEntity(fireball);
                        }

                        return InteractionResultHolder.pass(stack);
                    })
                    .onReleaseUsing((stack, level, entity, ticks) -> {
                        if (!level.isClientSide) {
                            Util.getOwnedProjectiles(entity, SpellFireball.class, (ServerLevel) level).forEach(e -> e.getEntityData().set(SpellFireball.DATA_SHOT, true));
                        }
                        AbilityUtil.setOnCooldown(stack, level);
                    })
                    .useDuration(stack -> 72000)
                    .addCooldown(SSConfig.FIREBALL_COOLDOWN)
                    .useAnimation(stack -> UseAnim.BOW)
                    .build());

    // Double Jump
    public static final AttributeModifier DOUBLE_JUMP_MODIFIER = new AttributeModifier(Util.makeResource("double_jump"), 1, AttributeModifier.Operation.ADD_VALUE);
    public static final DeferredHolder<SwordAbility, SwordAbility> DOUBLE_JUMP = register("double_jump",
            () -> new SwordAbilityBuilder(0xb2dce7)
                    .attributes(builder -> builder.add(SSAttributes.JUMPS, DOUBLE_JUMP_MODIFIER, EquipmentSlotGroup.MAINHAND))
                    .build());

    // Alchemist
    public static final TagKey<Potion> ALCHEMIST_SELF_EFFECTS = Util.makeTag(Registries.POTION, "alchemist_self");
    public static final TagKey<Potion> ALCHEMIST_VICTIM_EFFECTS = Util.makeTag(Registries.POTION, "alchemist_victim");
    public static final DeferredHolder<SwordAbility, SwordAbility> ALCHEMIST = register("alchemist",
            () -> new SwordAbilityBuilder(0xffbf47)
            .onHit((level, attacker, victim) -> {
                if (!level.isClientSide && !victim.isDeadOrDying() &&
                        !(attacker instanceof Player player && player.getAttackStrengthScale(0F) < 1.0)) {
                    handleAlchemistAbility(level, attacker, victim);
                }
            })
            .onKill((level, attacker, victim) -> {
                if (!level.isClientSide) {
                    handleAlchemistAbility(level, attacker, null);
                }
            })
            .build());

    public static void handleAlchemistAbility(Level level, LivingEntity attacker, @Nullable LivingEntity victim) {
        float chance = (victim == null ? SSConfig.ALCHEMIST_SELF_CHANCE : SSConfig.ALCHEMIST_VICTIM_CHANCE).get().floatValue();
        if (level.random.nextFloat() <= chance) {
            for (int tries = 0; tries < 3; tries++) {
                Optional<Holder<Potion>> optional = level.registryAccess().registryOrThrow(Registries.POTION)
                        .getOrCreateTag(victim == null ? ALCHEMIST_SELF_EFFECTS : ALCHEMIST_VICTIM_EFFECTS)
                        .getRandomElement(level.random);

                if (optional.isPresent()) {
                    Holder<Potion> potionHolder = optional.get();
                    boolean splashed = false;
                    List<MobEffectInstance> effects = Util.copyWithDuration(potionHolder.value().getEffects(), duration -> duration / 4);

                    for (MobEffectInstance effect : effects) {
                        LivingEntity target = victim == null ? attacker : victim;
                        if (!CommonHooks.canMobEffectBeApplied(target, effect)) {
                            continue;
                        }
                        target.addEffect(effect);

                        if (!splashed) {
                            splashed = true;

                            level.levelEvent(
                                    LevelEvent.PARTICLES_SPELL_POTION_SPLASH,
                                    (victim == null ? attacker : victim).blockPosition(),
                                    PotionContents.getColor(potionHolder)
                            );
                        }

                        if (attacker instanceof ServerPlayer serverPlayer) {
                            serverPlayer.sendSystemMessage(
                                    Component.translatable(
                                            "ability.swordinthestone.alchemist." + (victim == null ? "self" : "victim"),
                                            getPotionMessage(effect)
                                    ).withStyle(ALCHEMIST.get().getColorStyle()),
                                    effects.size() == 1
                            );
                        }
                    }

                    if (splashed) return;
                }
            }
        }
    }

    private static MutableComponent getPotionMessage(MobEffectInstance effect) {
        Component potionName = Component.translatable(effect.getDescriptionId());
        int duration = effect.getDuration() / 20;

        MutableComponent potionMessage;
        if (effect.getAmplifier() > 0) {
            potionMessage = Component.translatable("ability.swordinthestone.alchemist.potionAmplifier",
                    potionName,
                    Component.translatable("potion.potency." + effect.getAmplifier()),
                    duration);
        } else {
            potionMessage = Component.translatable("ability.swordinthestone.alchemist.potion",
                    potionName,
                    duration);
        }

        return potionMessage.withStyle(Style.EMPTY.withColor(effect.getEffect().value().getColor()));
    }

    // Bat Swarm
    public static final DeferredHolder<SwordAbility, SwordAbility> BAT_SWARM = register("bat_swarm",
            () -> new SwordAbilityBuilder(0xab29ff)
            .onUse((level, player, usedHand) -> {
                ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
                if (AbilityUtil.isOnCooldown(stack, level, SSConfig.BAT_SWARM_COOLDOWN.get())) return InteractionResultHolder.fail(stack);

                if (!level.isClientSide) {
                    Vec3 pos = player.getEyePosition().add(player.getLookAngle().scale(0.9));
                    BatSwarmGoal.BatSwarm swarm = new BatSwarmGoal.BatSwarm(player);
                    for (int i = 0; i < 15; i++) {
                        boolean isLeader = i == 0;
                        Bat entity = EntityType.BAT.spawn((ServerLevel) level, bat -> {
                            bat.setPos(pos.add(Util.relativeVec(player.getRotationVector(), 0, (level.random.nextFloat() - 0.5) * 2 - 1, (level.random.nextFloat() - 0.5) * 2)));
                            bat.setCustomName(Component.translatable("ability.swordinthestone.bat_swarm.name", player.getDisplayName(), Component.translatable(bat.getType().getDescriptionId())).withStyle(SwordAbilities.BAT_SWARM.get().getColorStyle()));
                            bat.goalSelector.addGoal(0, new BatSwarmGoal(bat, swarm, isLeader));
                        }, BlockPos.ZERO, MobSpawnType.COMMAND,false, false);
                        if (isLeader && entity != null) player.startRiding(entity);
                    }
                }

                player.swing(InteractionHand.MAIN_HAND);
                AbilityUtil.setOnCooldown(stack, level);
                return InteractionResultHolder.success(stack);
            })
            .addCooldown(SSConfig.BAT_SWARM_COOLDOWN)
            .build());

    public static final DeferredHolder<SwordAbility, SwordAbility> VORTEX_CHARGE = register("vortex_charge",
            () -> new SwordAbilityBuilder(0x00ffb9)
                    .onUse((level, player, usedHand) -> {
                        ItemStack stack = player.getItemInHand(usedHand);
                        int charge = AbilityUtil.getCharge(stack);
                        if (player.isCrouching()) {
                            if (level.isClientSide) {
                                SideUtil.releaseRightClick();
                            }

                            if (charge > 0) {
                                AbilityUtil.setCharge(stack, 0);

                                player.swing(usedHand);

                                if (level.isClientSide) {
                                    for (int i = 0; i < 20 + (charge / SSConfig.VORTEX_CHARGE_CAPACITY.get()) * 40; i++) {
                                        Vec3 pos = player.getEyePosition().add((level.random.nextFloat() - 0.5) * 0.5, (level.random.nextFloat() - 0.5) * 0.5, (level.random.nextFloat() - 0.5) * 0.5);
                                        Vec3 delta = player.getEyePosition().subtract(pos).normalize();
                                        level.addParticle(SSParticles.VORTEX.get(), pos.x(), pos.y(), pos.z(), delta.x(), delta.y(), delta.z());
                                    }
                                } else {
                                    level.playSound(null, player.blockPosition(), SSSounds.VORTEX.get(), SoundSource.PLAYERS, 1.0F, 1.0F);

                                    double percentCharge = charge / (float) SSConfig.VORTEX_CHARGE_CAPACITY.get();
                                    double range = 5 + percentCharge * 15;
                                    for (Entity entity : level.getEntities(player, AABB.ofSize(player.getEyePosition(), range, range, range))) {
                                        double percentDistance = (10 - entity.distanceTo(player)) / 10F;
                                        double scale = percentCharge * percentDistance;

                                        entity.hurt(level.damageSources().playerAttack(player), SSConfig.VORTEX_CHARGE_DAMAGE.get().floatValue() * (float) scale);

                                        Vec3 delta = entity.position()
                                                .subtract(player.position())
                                                .normalize()
                                                .add(0, scale * 0.2, 0)
                                                .scale(scale * 5);
                                        entity.push(delta.x(), delta.y(), delta.z());
                                        entity.resetFallDistance();
                                    }
                                }

                                return InteractionResultHolder.success(stack);
                            }
                        } else if (charge > 0) {
                            level.playSound(null, player.blockPosition(), SSSounds.SUCTION.get(), SoundSource.PLAYERS, 1.0F, 1.4F);
                        }
                        return InteractionResultHolder.pass(stack);
                    })
                    .onUseTick((level, user, stack, remainingUseDuration) -> {
                        int charge = AbilityUtil.getCharge(stack);
                        if (charge > 0) {
                            AbilityUtil.setCharge(stack, charge - 1);
                            for (Entity entity : level.getEntities(user, AABB.ofSize(user.position(), 20, 20, 20), e -> e.getBoundingBox().getSize() < 2.5)) {
                                if (!level.isClientSide) {
                                    double percentDistance = (15 - entity.distanceTo(user)) / 15F;
                                    entity.setDeltaMovement(user.position()
                                            .subtract(entity.getX(), entity.getY() - 0.5, entity.getZ())
                                            .normalize()
                                            .multiply(percentDistance * 0.4, percentDistance * 0.4, percentDistance * 0.4));
                                    entity.hurtMarked = true;
                                    entity.resetFallDistance();
                                }
                            }

                            if (!level.isClientSide && remainingUseDuration % 18 == 0) {
                                level.playSound(null, user.blockPosition(), SSSounds.SUCTION.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                            }

                            if (level.isClientSide) {
                                for (int i = 0; i < 4; i++) {
                                    Vec3 pos = user.position().add((level.random.nextFloat() - 0.5) * 15, (level.random.nextFloat() - 0.5) * 0.2, (level.random.nextFloat() - 0.5) * 15);
                                    Vec3 delta = user.position().subtract(pos).normalize().scale(0.2);
                                    level.addParticle(SSParticles.VORTEX.get(), pos.x(), pos.y() + 0.5, pos.z(), delta.x(), delta.y(), delta.z());
                                }
                            }
                        }
                    })
                    .onHit((level, attacker, victim) -> {
                        float percent = 1;
                        if (attacker instanceof Player player) {
                            float scale = player.getAttackStrengthScale(0);
                            if (scale < 0.75F) {
                                return;
                            }
                            percent = scale * scale;
                        }

                        ItemStack stack = attacker.getItemInHand(InteractionHand.MAIN_HAND);
                        int charge = AbilityUtil.getCharge(stack);
                        if (charge < SSConfig.VORTEX_CHARGE_CAPACITY.get()) {
                            level.sendParticles(SSParticles.VORTEX.get(), victim.getX(), victim.getY() + 0.5, victim.getZ(), Mth.ceil(percent * 12), 0.4, 0.1, 0.4, 0.2);

                            level.playSound(null, victim.blockPosition(), SSSounds.WHOOSH.get(), SoundSource.PLAYERS, 1.0F, 0.6F + level.random.nextFloat() * 0.8F);

                            AbilityUtil.setCharge(stack, Math.min(SSConfig.VORTEX_CHARGE_CAPACITY.get(), charge + Mth.floor(percent * SSConfig.VORTEX_CHARGE_PER_HIT.get())));
                        }
                    })
                    .customBar(stack -> false, stack -> (float) AbilityUtil.getCharge(stack) / SSConfig.VORTEX_CHARGE_CAPACITY.get())
                    .useDuration(AbilityUtil::getCharge)
                    .useAnimation(stack -> UseAnim.BOW)
                    .build());

    private static DeferredHolder<SwordAbility, SwordAbility> register(String name, Supplier<SwordAbility> supplier) {
        SwordInTheStone.ABILITY_MODEL_MAP.put(SwordInTheStone.MODID + ":" + name, ModelResourceLocation.standalone(Util.makeResource("item/ability/" + name)));
        return SWORD_ABILITIES.register(name, supplier);
    }
}