package com.bonker.swordinthestone.util;

import com.bonker.swordinthestone.common.ability.SwordAbilities;
import com.bonker.swordinthestone.common.ability.SwordAbility;
import com.bonker.swordinthestone.common.item.SSDataComponents;
import com.bonker.swordinthestone.common.item.UniqueSwordItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class AbilityUtil {
    public static SwordAbility getSwordAbility(LivingEntity holder) {
        return getSwordAbility(holder.getItemInHand(InteractionHand.MAIN_HAND));
    }

    public static SwordAbility getSwordAbility(ItemStack stack) {
        if (stack.getItem() instanceof UniqueSwordItem) {
            SwordAbility ability = SwordAbilities.SWORD_ABILITY_REGISTRY.get(ResourceLocation.tryParse(stack.getOrDefault(SSDataComponents.ABILITY_COMPONENT, "")));
            return ability == null ? SwordAbility.NONE : ability;
        }
        return SwordAbility.NONE;
    }

    public static void setSwordAbility(ItemStack stack, SwordAbility ability) {
        ResourceLocation key = SwordAbilities.SWORD_ABILITY_REGISTRY.getKey(ability);
        if (key == null) return;
        stack.set(SSDataComponents.ABILITY_COMPONENT, key.toString());
    }

    public static boolean isPassiveActive(LivingEntity holder, SwordAbility ability) {
        return getSwordAbility(holder) == ability;
    }

    public static boolean isOnCooldown(ItemStack stack, @Nullable Level level, int cooldownLength) {
        if (!stack.has(SSDataComponents.LAST_USED_TICK_COMPONENT)) return false;

        long lastUsedTick = stack.getOrDefault(SSDataComponents.LAST_USED_TICK_COMPONENT, 0L);

        long time;
        if (level != null) {
            time = level.getGameTime() - lastUsedTick;
        } else {
            time = SideUtil.getTimeSinceTick(lastUsedTick);
        }

        return time < cooldownLength;
    }

    public static void setOnCooldown(ItemStack stack, Level level) {
        stack.set(SSDataComponents.LAST_USED_TICK_COMPONENT, level.getGameTime());
    }

    public static float cooldownProgress(ItemStack stack, Supplier<Integer> cooldownSupplier) {
        if (!stack.has(SSDataComponents.LAST_USED_TICK_COMPONENT)) return 0;

        long time = SideUtil.getTimeSinceTick(stack.getOrDefault(SSDataComponents.LAST_USED_TICK_COMPONENT, 0L));
        int cooldown = cooldownSupplier.get();
        if (time >= cooldown) return 0;
        return (float) time / cooldown;
    }

    public static int getCharge(ItemStack stack) {
        return stack.getOrDefault(SSDataComponents.CHARGE_COMPONENT, 0);
    }

    public static void setCharge(ItemStack stack, int charge) {
        stack.set(SSDataComponents.CHARGE_COMPONENT, charge);
    }
}
