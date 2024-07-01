package com.bonker.swordinthestone.client.particle;

import com.bonker.swordinthestone.SwordInTheStone;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SSParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, SwordInTheStone.MODID);



    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> HEAL = PARTICLE_TYPES.register("heal", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FIRE = PARTICLE_TYPES.register("fire", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> AIR = PARTICLE_TYPES.register("air", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> VORTEX = PARTICLE_TYPES.register("vortex", () -> new SimpleParticleType(false));
}
