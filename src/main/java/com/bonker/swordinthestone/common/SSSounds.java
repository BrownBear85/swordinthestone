package com.bonker.swordinthestone.common;

import com.bonker.swordinthestone.SwordInTheStone;
import com.bonker.swordinthestone.util.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SSSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, SwordInTheStone.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> ZAP = register("zap");
    public static final DeferredHolder<SoundEvent, SoundEvent> HEAL = register("heal");
    public static final DeferredHolder<SoundEvent, SoundEvent> TOXIC = register("toxic");
    public static final DeferredHolder<SoundEvent, SoundEvent> DASH = register("dash");
    public static final DeferredHolder<SoundEvent, SoundEvent> ROCK = register("rock");
    public static final DeferredHolder<SoundEvent, SoundEvent> SWORD_PULL = register("sword_pull");
    public static final DeferredHolder<SoundEvent, SoundEvent> SUCCESS = register("success");
    public static final DeferredHolder<SoundEvent, SoundEvent> LASER = register("laser");
    public static final DeferredHolder<SoundEvent, SoundEvent> FIREBALL = register("fireball");
    public static final DeferredHolder<SoundEvent, SoundEvent> RIFT = register("rift");
    public static final DeferredHolder<SoundEvent, SoundEvent> JUMP = register("jump");
    public static final DeferredHolder<SoundEvent, SoundEvent> LAND = register("land");
    public static final DeferredHolder<SoundEvent, SoundEvent> SUCTION = register("suction");
    public static final DeferredHolder<SoundEvent, SoundEvent> VORTEX = register("vortex");
    public static final DeferredHolder<SoundEvent, SoundEvent> WHOOSH = register("whoosh");


    private static DeferredHolder<SoundEvent, SoundEvent> register(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(Util.makeResource(name)));
    }
}
