package com.bonker.swordinthestone.common.item;

import com.bonker.swordinthestone.SwordInTheStone;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SSItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SwordInTheStone.MODID);

    public static final RegistryObject<UniqueSwordItem> FOREST_SWORD = swordVariant("forest_sword", 0x33641c);
    public static final RegistryObject<UniqueSwordItem> DESERT_SWORD = swordVariant("desert_sword", 0xdad2a3);
    public static final RegistryObject<UniqueSwordItem> ARCTIC_SWORD = swordVariant("arctic_sword", 0x85adf8);
    public static final RegistryObject<UniqueSwordItem> PLAINS_SWORD = swordVariant("plains_sword", 0x587336);
    public static final RegistryObject<UniqueSwordItem> NETHER_SWORD = swordVariant("nether_sword", 0x723232);
    public static final RegistryObject<UniqueSwordItem> END_SWORD = swordVariant("end_sword", 0xecfbaf);


    private static RegistryObject<UniqueSwordItem> swordVariant(String name, int color) {
        SwordInTheStone.SWORD_MODEL_MAP.put(new ResourceLocation(SwordInTheStone.MODID, name), new ResourceLocation(SwordInTheStone.MODID, "item/sword/" + name));
        return ITEMS.register(name, () -> new UniqueSwordItem(color, new Item.Properties()));
    }
}
