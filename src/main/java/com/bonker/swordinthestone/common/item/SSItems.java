package com.bonker.swordinthestone.common.item;

import com.bonker.swordinthestone.SwordInTheStone;
import com.bonker.swordinthestone.SwordInTheStoneClient;
import com.bonker.swordinthestone.common.SSConfig;
import com.bonker.swordinthestone.common.ability.SwordAbilities;
import com.bonker.swordinthestone.common.ability.SwordAbility;
import com.bonker.swordinthestone.util.AbilityUtil;
import com.bonker.swordinthestone.util.Util;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SSItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SwordInTheStone.MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SwordInTheStone.MODID);

    public static final DeferredItem<UniqueSwordItem> FOREST_SWORD = swordVariant("forest_sword", 0x33641c);
    public static final DeferredItem<UniqueSwordItem> DESERT_SWORD = swordVariant("desert_sword", 0xdad2a3);
    public static final DeferredItem<UniqueSwordItem> ARCTIC_SWORD = swordVariant("arctic_sword", 0x85adf8);
    public static final DeferredItem<UniqueSwordItem> PLAINS_SWORD = swordVariant("plains_sword", 0x587336);
    public static final DeferredItem<UniqueSwordItem> NETHER_SWORD = swordVariant("nether_sword", 0x723232);
    public static final DeferredItem<UniqueSwordItem> END_SWORD = swordVariant("end_sword", 0xecfbaf);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = TABS.register("unique_swords", () -> CreativeModeTab.builder()
            .title(Component.translatable("item_group.swordinthestone.swords"))
            .icon(() -> new ItemStack(FOREST_SWORD.get()))
            .displayItems(((params, items) -> {
                float damage = SSConfig.BASE_DAMAGE.get() - 1 + Util.constrictToMultiple(0.5F * SSConfig.MAX_DAMAGE_MODIFIER.get().floatValue(), 0.5F);
                float speed = SSConfig.BASE_SPEED.get().floatValue() - 4 + Util.constrictToMultiple(0.5F * SSConfig.MAX_SPEED_MODIFIER.get().floatValue(), 0.5F);

                for (DeferredHolder<Item, ? extends Item> item : SSItems.ITEMS.getEntries()) {
                    if (item.get() instanceof UniqueSwordItem sword) {
                        for (DeferredHolder<SwordAbility, ? extends SwordAbility> ability : SwordAbilities.SWORD_ABILITIES.getEntries()) {
                            ItemStack stack = new ItemStack(sword);
                            AbilityUtil.setSwordAbility(stack, ability.get());
                            stack.set(DataComponents.ATTRIBUTE_MODIFIERS, UniqueSwordItem.createAttributes(stack, damage, speed));
                            items.accept(stack);
                        }
                    }
                }
            }))
            .build());

    private static DeferredItem<UniqueSwordItem> swordVariant(String name, int color) {
        if (FMLEnvironment.dist.isClient()) {
            SwordInTheStoneClient.addSword(name);
        }
        return ITEMS.register(name, () -> new UniqueSwordItem(color, new Item.Properties()));
    }
}
