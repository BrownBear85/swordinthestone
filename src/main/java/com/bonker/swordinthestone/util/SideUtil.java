package com.bonker.swordinthestone.util;

import com.bonker.swordinthestone.common.entity.EnderRift;
import com.bonker.swordinthestone.common.networking.payloads.BidirectionalEnderRiftPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

public class SideUtil {
    public static void controlEnderRift(EnderRift enderRift, Player player) {
        if (player != Minecraft.getInstance().player) return;

        enderRift.setDeltaMovement(enderRift.calculateDelta(player));
        Vec3 delta = enderRift.getDeltaMovement();
        PacketDistributor.sendToServer(new BidirectionalEnderRiftPayload(enderRift.getId(), enderRift.getX(), enderRift.getY(), enderRift.getZ(), delta.x(), delta.y(), delta.z()));

        enderRift.move(MoverType.SELF, enderRift.getDeltaMovement());
    }

    public static long getTimeSinceTick(long tick) {
        if (Minecraft.getInstance().level == null) return 0;
        return Minecraft.getInstance().level.getGameTime() - tick;
    }

    public static void releaseRightClick() {
        Minecraft.getInstance().options.keyUse.setDown(false);
    }
}
