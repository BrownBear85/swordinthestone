package com.bonker.swordinthestone.common;

import com.bonker.swordinthestone.SwordInTheStone;
import com.bonker.swordinthestone.Util;
import com.bonker.swordinthestone.common.ability.SwordAbilities;
import com.bonker.swordinthestone.common.ability.SwordAbility;
import com.bonker.swordinthestone.common.capability.DashCapability;
import com.bonker.swordinthestone.common.capability.IDashCapability;
import com.bonker.swordinthestone.common.command.MakeSwordCommand;
import com.bonker.swordinthestone.common.entity.HeightAreaEffectCloud;
import com.bonker.swordinthestone.common.item.SSItems;
import com.bonker.swordinthestone.common.item.UniqueSwordItem;
import com.bonker.swordinthestone.common.networking.SSNetworking;
import com.bonker.swordinthestone.common.networking.ServerboundDashAttackPacket;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CommonEvents {
    @Mod.EventBusSubscriber(modid = SwordInTheStone.MODID)
    public static class ForgeBus {
        @SubscribeEvent
        public static void onCommandsRegistered(final RegisterCommandsEvent event) {
            MakeSwordCommand.register(event.getDispatcher());
        }

        @SubscribeEvent
        public static void onLivingAttack(final LivingAttackEvent event) {
            if (event.getSource().getDirectEntity() instanceof LivingEntity attacker && !(attacker instanceof Player)) {
                ItemStack stack =  attacker.getItemInHand(InteractionHand.MAIN_HAND);
                if (stack.getItem() instanceof UniqueSwordItem uniqueSwordItem)
                    uniqueSwordItem.hurtEnemy(stack, event.getEntity(), attacker);
            }

            if (event.getEntity() instanceof ServerPlayer player) {
                player.getCapability(DashCapability.DASH_DATA).ifPresent(cap -> {
                    if (DashCapability.getTicks(player) > 0 && event.getSource().is(DamageTypes.MOB_ATTACK)) event.setCanceled(true);
                });
            }
        }

        @SubscribeEvent
        public static void onAttachCapabilities(final AttachCapabilitiesEvent<Entity> event) {
            if (!(event.getObject() instanceof Player)) return;

            IDashCapability dash = DashCapability.create();
            LazyOptional<IDashCapability> dashOptional = LazyOptional.of(() -> dash);

            ICapabilityProvider provider = new ICapabilityProvider() {
                @Override
                public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                    return DashCapability.DASH_DATA.orEmpty(cap, dashOptional);
                }
            };

            event.addCapability(DashCapability.NAME, provider);
        }

        @SubscribeEvent
        public static void onPlayerTick(final TickEvent.PlayerTickEvent event) {
            Player player = event.player;
            player.getCapability(DashCapability.DASH_DATA).ifPresent(cap -> {
                if (event.phase == TickEvent.Phase.END) return;

                int dashTicks = DashCapability.getTicks(player);
                if (dashTicks <= 0) return;
                dashTicks--;

                if (player.getDeltaMovement().lengthSqr() < 0.01) dashTicks = 0;

                if (event.side == LogicalSide.SERVER && dashTicks > 0) HeightAreaEffectCloud.createToxicDashCloud(player.level(), player, player.getX(), player.getY() - 0.5, player.getZ());

                if (dashTicks > 0 && event.side == LogicalSide.CLIENT) {
                    player.level().getEntities(player, player.getBoundingBox().inflate(0.5)).forEach(entity -> {
                        if (entity instanceof LivingEntity && !cap.isDashed(entity)) {
                            cap.addToDashed(entity);
                            SSNetworking.sendToServer(new ServerboundDashAttackPacket(entity.getId()));
                        }
                    });
                    cap.clearDashed();
                }
                cap.setDashTicks(dashTicks);
            });
        }
    }

    @Mod.EventBusSubscriber(modid = SwordInTheStone.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBus {
        @SubscribeEvent
        public static void onCommonSetup(final FMLCommonSetupEvent event) {
            for (RegistryObject<Item> itemObj : SSItems.ITEMS.getEntries()) {
                if (itemObj.get() instanceof UniqueSwordItem uniqueSwordItem) {
                    for (RegistryObject<SwordAbility> abilityObj : SwordAbilities.SWORD_ABILITIES.getEntries()) {
                        UniqueSwordItem.STYLE_TABLE.put(uniqueSwordItem, abilityObj.get(), Style.EMPTY.withColor(Util.mergeColors(abilityObj.get().getColor(), uniqueSwordItem.getColor())));
                    }
                }
            }
        }
    }
}
