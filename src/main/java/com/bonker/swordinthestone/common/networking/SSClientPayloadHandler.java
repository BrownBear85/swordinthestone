package com.bonker.swordinthestone.common.networking;

import com.bonker.swordinthestone.common.block.entity.SSBlockEntities;
import com.bonker.swordinthestone.common.entity.SSEntityTypes;
import com.bonker.swordinthestone.common.networking.payloads.BidirectionalEnderRiftPayload;
import com.bonker.swordinthestone.common.networking.payloads.Play2ClientDeltaPayload;
import com.bonker.swordinthestone.common.networking.payloads.Play2ClientSwordStoneDataPayload;
import com.bonker.swordinthestone.common.networking.payloads.Play2ClientSwordStoneItemPayload;
import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.slf4j.Logger;

public class SSClientPayloadHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void handleEnderRift(BidirectionalEnderRiftPayload payload, IPayloadContext context) {
        Entity entity = context.player().level().getEntity(payload.entityId());
        if (entity == null || entity.getType() != SSEntityTypes.ENDER_RIFT.get()) return;
        if (((Projectile) entity).getOwner() == context.player()) return;

        entity.setPos(payload.x(), payload.y(), payload.z());
        entity.setDeltaMovement(payload.xd(), payload.yd(), payload.zd());
    }

    public static void handleSyncDelta(Play2ClientDeltaPayload payload, IPayloadContext context) {
        Vec3 delta = new Vec3(payload.xd(), payload.yd(), payload.zd());
        if (payload.entityId() != -1) {
            Entity entity = context.player().level().getEntity(payload.entityId());
            if (entity != null) entity.setDeltaMovement(delta);
        }
        context.player().setDeltaMovement(delta);
    }

    public static void handleSwordStoneData(Play2ClientSwordStoneDataPayload payload, IPayloadContext context) {
        context.player().level().getBlockEntity(payload.pos(), SSBlockEntities.SWORD_STONE_MASTER.get()).ifPresent(entity -> {
            if (payload.isProgress()) {
                entity.progress = payload.value();
            } else if (Math.abs(entity.idleTicks - payload.value()) > 5) {
                entity.idleTicks = payload.value();
            }
        });
    }

    public static void handleSwordStoneItem(Play2ClientSwordStoneItemPayload payload, IPayloadContext context) {
        context.player().level().getBlockEntity(payload.pos(), SSBlockEntities.SWORD_STONE_MASTER.get())
                .ifPresent(entity -> entity.setItem(payload.stack()));
    }
}
