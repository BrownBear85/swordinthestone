package com.bonker.swordinthestone.common.networking.payloads;

import com.bonker.swordinthestone.SwordInTheStone;
import com.bonker.swordinthestone.common.networking.SSClientPayloadHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public record Play2ClientSwordStoneItemPayload(BlockPos pos, ItemStack stack) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<Play2ClientSwordStoneItemPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(SwordInTheStone.MODID, "s2c_stone_item"));

    public static final StreamCodec<RegistryFriendlyByteBuf, Play2ClientSwordStoneItemPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            Play2ClientSwordStoneItemPayload::pos,
            ItemStack.OPTIONAL_STREAM_CODEC,
            Play2ClientSwordStoneItemPayload::stack,
            Play2ClientSwordStoneItemPayload::new
    );

    public static void register(PayloadRegistrar registrar) {
        registrar.playToClient(TYPE, STREAM_CODEC, SSClientPayloadHandler::handleSwordStoneItem);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
