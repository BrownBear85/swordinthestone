package com.bonker.swordinthestone.util;

import com.bonker.swordinthestone.SwordInTheStone;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.mojang.datafixers.util.Function7;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Util {
    public static Vec3 toVec3(Vector3f vector3f) {
        return new Vec3(vector3f.x(), vector3f.y(), vector3f.z());
    }

    public static <B, C, T1, T2, T3, T4, T5, T6, T7> StreamCodec<B, C> streamCodec7(
            final StreamCodec<? super B, T1> pCodec1,
            final Function<C, T1> pGetter1,
            final StreamCodec<? super B, T2> pCodec2,
            final Function<C, T2> pGetter2,
            final StreamCodec<? super B, T3> pCodec3,
            final Function<C, T3> pGetter3,
            final StreamCodec<? super B, T4> pCodec4,
            final Function<C, T4> pGetter4,
            final StreamCodec<? super B, T5> pCodec5,
            final Function<C, T5> pGetter5,
            final StreamCodec<? super B, T6> pCodec6,
            final Function<C, T6> pGetter6,
            final StreamCodec<? super B, T7> pCodec7,
            final Function<C, T7> pGetter7,
            final Function7<T1, T2, T3, T4, T5, T6, T7, C> pFactory
    ) {
        return new StreamCodec<B, C>() {
            @Override
            public C decode(B p_330310_) {
                T1 t1 = pCodec1.decode(p_330310_);
                T2 t2 = pCodec2.decode(p_330310_);
                T3 t3 = pCodec3.decode(p_330310_);
                T4 t4 = pCodec4.decode(p_330310_);
                T5 t5 = pCodec5.decode(p_330310_);
                T6 t6 = pCodec6.decode(p_330310_);
                T7 t7 = pCodec7.decode(p_330310_);
                return pFactory.apply(t1, t2, t3, t4, t5, t6, t7);
            }

            @Override
            public void encode(B p_332052_, C p_331912_) {
                pCodec1.encode(p_332052_, pGetter1.apply(p_331912_));
                pCodec2.encode(p_332052_, pGetter2.apply(p_331912_));
                pCodec3.encode(p_332052_, pGetter3.apply(p_331912_));
                pCodec4.encode(p_332052_, pGetter4.apply(p_331912_));
                pCodec5.encode(p_332052_, pGetter5.apply(p_331912_));
                pCodec6.encode(p_332052_, pGetter6.apply(p_331912_));
                pCodec7.encode(p_332052_, pGetter7.apply(p_331912_));
            }
        };
    }

    public static Vec3 relativeVec(Vec2 rotation, double forwards, double up, double left) {
        float f = Mth.cos((rotation.y + 90.0F) * Mth.DEG_TO_RAD);
        float f1 = Mth.sin((rotation.y + 90.0F) * Mth.DEG_TO_RAD);
        float f2 = Mth.cos(-rotation.x * Mth.DEG_TO_RAD);
        float f3 = Mth.sin(-rotation.x * Mth.DEG_TO_RAD);
        float f4 = Mth.cos((-rotation.x + 90.0F) * Mth.DEG_TO_RAD);
        float f5 = Mth.sin((-rotation.x + 90.0F) * Mth.DEG_TO_RAD);
        Vec3 vec31 = new Vec3(f * f2, f3, f1 * f2);
        Vec3 vec32 = new Vec3(f * f4, f5, f1 * f4);
        Vec3 vec33 = vec31.cross(vec32).scale(-1.0D);
        double d0 = vec31.x * forwards + vec32.x * up + vec33.x * left;
        double d1 = vec31.y * forwards + vec32.y * up + vec33.y * left;
        double d2 = vec31.z * forwards + vec32.z * up + vec33.z * left;
        return new Vec3(d0, d1, d2);
    }

    public static Vec3 calculateViewVector(float pXRot, float pYRot) {
        float f = pXRot * ((float)Math.PI / 180F);
        float f1 = -pYRot * ((float)Math.PI / 180F);
        float f2 = Mth.cos(f1);
        float f3 = Mth.sin(f1);
        float f4 = Mth.cos(f);
        float f5 = Mth.sin(f);
        return new Vec3(f3 * f4, -f5, f2 * f4);
    }

    public static List<MobEffectInstance> copyWithDuration(List<MobEffectInstance> effects, Int2IntFunction durationMapper) {
        return effects.stream().map(effect -> new MobEffectInstance(effect.getEffect(), effect.mapDuration(durationMapper), effect.getAmplifier())).toList();
    }

    public static List<BlockPos> betweenClosed(BlockPos firstPos, BlockPos secondPos) {
        ImmutableList.Builder<BlockPos> builder = ImmutableList.builder();
        for (BlockPos blockPos : BlockPos.betweenClosed(firstPos, secondPos)) {
            builder.add(blockPos.immutable());
        }
        return builder.build();
    }

    @SuppressWarnings("unchecked")
    public static <T extends Projectile> List<T> getOwnedProjectiles(Entity owner, Class<T> clazz, ServerLevel level) {
        return Streams.stream(level.getAllEntities()).filter(clazz::isInstance)
                .map(entity -> (T) entity)
                .filter(e -> e.getOwner() == owner)
                .collect(Collectors.toList());
    }
    
    public static ResourceLocation makeResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(SwordInTheStone.MODID, path);
    }
    
    public static <T> TagKey<T> makeTag(ResourceKey<Registry<T>> registryKey, String path) {
        return TagKey.create(registryKey, Util.makeResource(path));
    }

    public static float randomFloatMultiple(RandomSource random, float max, float base) {
        if (max % base > 0.001) {
            return random.nextFloat() * max;
        }

        int intervals = Mth.floor(max / base);
        return base * random.nextInt(intervals + 1);
    }

    public static float constrictToMultiple(float value, float base) {
        float floor = Mth.floor(value / base) * base;
        float ceil = floor + base;
        return value - floor > value - ceil ? floor : ceil;
    }

    public static class SwordSpinAnimation {
        private static final float[] swordSpinAnimation = net.minecraft.Util.make(new float[200], (floats) -> {
            for(int i = 0; i < floats.length; ++i) {
                floats[i] = swordSpinFunc(i);
            }
        });

        // represents a mathematical function: (paste into desmos) y\ =\ \frac{360\cdot4}{1+10^{-0.05\left(x-100\right)}}
        private static float swordSpinFunc(float animationTick) {
            return (360 * 4 / (1 + (float) Math.pow(10, -0.05 * (animationTick - 100))));
        }

        public static float swordSpin(float animationTick) {
            animationTick = Mth.clamp(animationTick, 0, swordSpinAnimation.length - 1);
            float value1 = swordSpinAnimation[Mth.floor(animationTick)];
            float value2 = swordSpinAnimation[Mth.ceil(animationTick)];
            float partialTick = animationTick % 1;
            return value1 + (value2 - value1) * partialTick;
        }
    }
}
