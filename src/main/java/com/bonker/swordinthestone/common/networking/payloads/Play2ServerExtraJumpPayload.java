package com.bonker.swordinthestone.common.networking.payloads;

import com.bonker.swordinthestone.SwordInTheStone;
import com.bonker.swordinthestone.common.networking.SSServerPayloadHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public record Play2ServerExtraJumpPayload(boolean left, boolean right, boolean forward, boolean backward) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<Play2ServerExtraJumpPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(SwordInTheStone.MODID, "c2s_extra_jump"));

    public static final StreamCodec<RegistryFriendlyByteBuf, Play2ServerExtraJumpPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            Play2ServerExtraJumpPayload::left,
            ByteBufCodecs.BOOL,
            Play2ServerExtraJumpPayload::right,
            ByteBufCodecs.BOOL,
            Play2ServerExtraJumpPayload::forward,
            ByteBufCodecs.BOOL,
            Play2ServerExtraJumpPayload::backward,
            Play2ServerExtraJumpPayload::new
    );

    public static void register(PayloadRegistrar registrar) {
        registrar.playToServer(TYPE, STREAM_CODEC, SSServerPayloadHandler::handleExtraJump);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
