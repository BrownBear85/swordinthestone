package com.bonker.swordinthestone.common.entity;

import com.bonker.swordinthestone.SwordInTheStone;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SSEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, SwordInTheStone.MODID);


    public static final DeferredHolder<EntityType<?>, EntityType<HeightAreaEffectCloud>> HEIGHT_AREA_EFFECT_CLOUD = register("height_area_effect_cloud",
            net.minecraft.world.entity.EntityType.Builder.<HeightAreaEffectCloud>of(HeightAreaEffectCloud::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(6.0F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE));

    public static final DeferredHolder<EntityType<?>, EntityType<EnderRift>> ENDER_RIFT = register("ender_rift",
            net.minecraft.world.entity.EntityType.Builder.<EnderRift>of(EnderRift::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(6)
                    .updateInterval(Integer.MAX_VALUE) // custom networking
                    .noSave());

    public static final DeferredHolder<EntityType<?>, EntityType<SpellFireball>> SPELL_FIREBALL = register("spell_fireball",
            EntityType.Builder.<SpellFireball>of(SpellFireball::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(4)
                    .updateInterval(10));

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(String key, EntityType.Builder<T> builder) {
        return ENTITY_TYPES.register(key, () -> builder.build(key));
    }
}
