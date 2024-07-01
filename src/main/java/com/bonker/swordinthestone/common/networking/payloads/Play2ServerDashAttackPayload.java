package com.bonker.swordinthestone.common.networking.payloads;

import com.bonker.swordinthestone.SwordInTheStone;
import com.bonker.swordinthestone.common.networking.SSServerPayloadHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public record Play2ServerDashAttackPayload(int entityId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<Play2ServerDashAttackPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(SwordInTheStone.MODID, "c2s_dash_attack"));

    public static final StreamCodec<RegistryFriendlyByteBuf, Play2ServerDashAttackPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            Play2ServerDashAttackPayload::entityId,
            Play2ServerDashAttackPayload::new
    );

    public static void register(PayloadRegistrar registrar) {
        registrar.playToServer(TYPE, STREAM_CODEC, SSServerPayloadHandler::handleDashAttack);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
