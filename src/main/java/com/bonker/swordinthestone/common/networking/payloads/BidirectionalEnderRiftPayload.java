package com.bonker.swordinthestone.common.networking.payloads;

import com.bonker.swordinthestone.SwordInTheStone;
import com.bonker.swordinthestone.common.networking.SSClientPayloadHandler;
import com.bonker.swordinthestone.common.networking.SSServerPayloadHandler;
import com.bonker.swordinthestone.util.Util;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public record BidirectionalEnderRiftPayload(int entityId, double x, double y, double z, double xd, double yd, double zd) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<BidirectionalEnderRiftPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(SwordInTheStone.MODID, "rift"));

    public static final StreamCodec<ByteBuf, BidirectionalEnderRiftPayload> STREAM_CODEC = Util.streamCodec7(
            ByteBufCodecs.VAR_INT,
            BidirectionalEnderRiftPayload::entityId,
            ByteBufCodecs.DOUBLE,
            BidirectionalEnderRiftPayload::x,
            ByteBufCodecs.DOUBLE,
            BidirectionalEnderRiftPayload::y,
            ByteBufCodecs.DOUBLE,
            BidirectionalEnderRiftPayload::z,
            ByteBufCodecs.DOUBLE,
            BidirectionalEnderRiftPayload::xd,
            ByteBufCodecs.DOUBLE,
            BidirectionalEnderRiftPayload::yd,
            ByteBufCodecs.DOUBLE,
            BidirectionalEnderRiftPayload::zd,
            BidirectionalEnderRiftPayload::new
    );

    public static void register(PayloadRegistrar registrar) {
        registrar.playBidirectional(TYPE, STREAM_CODEC, new DirectionalPayloadHandler<>(
                SSClientPayloadHandler::handleEnderRift,
                SSServerPayloadHandler::handleEnderRift)
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
