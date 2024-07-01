package com.bonker.swordinthestone.common.networking.payloads;

import com.bonker.swordinthestone.SwordInTheStone;
import com.bonker.swordinthestone.common.networking.SSClientPayloadHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public record Play2ClientSwordStoneDataPayload(BlockPos pos, boolean isProgress, short value) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<Play2ClientSwordStoneDataPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(SwordInTheStone.MODID, "s2c_stone_data"));

    public static final StreamCodec<ByteBuf, Play2ClientSwordStoneDataPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            Play2ClientSwordStoneDataPayload::pos,
            ByteBufCodecs.BOOL,
            Play2ClientSwordStoneDataPayload::isProgress,
            ByteBufCodecs.SHORT,
            Play2ClientSwordStoneDataPayload::value,
            Play2ClientSwordStoneDataPayload::new
    );

    public static void register(PayloadRegistrar registrar) {
        registrar.playToClient(TYPE, STREAM_CODEC, SSClientPayloadHandler::handleSwordStoneData);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
