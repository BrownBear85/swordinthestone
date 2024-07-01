package com.bonker.swordinthestone.common.networking.payloads;

import com.bonker.swordinthestone.SwordInTheStone;
import com.bonker.swordinthestone.common.networking.SSClientPayloadHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public record Play2ClientDeltaPayload(double xd, double yd, double zd, int entityId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<Play2ClientDeltaPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(SwordInTheStone.MODID, "s2c_delta"));

    public static final StreamCodec<ByteBuf, Play2ClientDeltaPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE,
            Play2ClientDeltaPayload::xd,
            ByteBufCodecs.DOUBLE,
            Play2ClientDeltaPayload::yd,
            ByteBufCodecs.DOUBLE,
            Play2ClientDeltaPayload::zd,
            ByteBufCodecs.INT,
            Play2ClientDeltaPayload::entityId,
            Play2ClientDeltaPayload::new
    );

    public static void register(PayloadRegistrar registrar) {
        registrar.playToClient(TYPE, STREAM_CODEC, SSClientPayloadHandler::handleSyncDelta);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
