package com.bonker.swordinthestone.common.networking;

import com.bonker.swordinthestone.common.SSConfig;
import com.bonker.swordinthestone.common.entity.HeightAreaEffectCloud;
import com.bonker.swordinthestone.common.entity.SSEntityTypes;
import com.bonker.swordinthestone.common.networking.payloads.BidirectionalEnderRiftPayload;
import com.bonker.swordinthestone.common.networking.payloads.Play2ClientDeltaPayload;
import com.bonker.swordinthestone.common.networking.payloads.Play2ServerDashAttackPayload;
import com.bonker.swordinthestone.common.networking.payloads.Play2ServerExtraJumpPayload;
import com.bonker.swordinthestone.server.attachment.DashAttachment;
import com.bonker.swordinthestone.server.attachment.ExtraJumpsAttachment;
import com.bonker.swordinthestone.server.attachment.SSAttachments;
import com.bonker.swordinthestone.util.DoubleJumpEvent;
import com.bonker.swordinthestone.util.Util;
import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.slf4j.Logger;

public class SSServerPayloadHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void handleEnderRift(BidirectionalEnderRiftPayload payload, IPayloadContext context) {
        Player player = context.player();
        Level level = player.level();

        Entity entity = level.getEntity(payload.entityId());
        if (entity == null || entity.getType() != SSEntityTypes.ENDER_RIFT.get()) return;
        if (((Projectile) entity).getOwner() != player) return;

        Vec3 pos = new Vec3(payload.x(), payload.y(), payload.z());
        if (entity.position().distanceToSqr(pos) < 25) {
            entity.setPos(pos);
            entity.setDeltaMovement(payload.xd(), payload.yd(), payload.zd());

            Vec3 delta = entity.getDeltaMovement();
            PacketDistributor.sendToPlayersTrackingEntity(entity, new BidirectionalEnderRiftPayload(payload.entityId(), entity.getX(), entity.getY(), entity.getZ(), delta.x(), delta.y(), delta.z()));
        } else {
            LOGGER.warn("{} controlled an ender rift suspiciously", player.getScoreboardName());
        }
    }

    public static void handleDashAttack(Play2ServerDashAttackPayload payload, IPayloadContext context) {
        Player player = context.player();
        DashAttachment data = player.getData(SSAttachments.DASH);
        if (data.getDashTicks() <= 0) return;
        Level level = player.level();
        Entity entity = level.getEntity(payload.entityId());
        if (entity == null) return;
        if (player.distanceTo(entity) > player.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE) * 2) return;
        player.attackStrengthTicker = 100;
        player.attack(entity);
        player.resetAttackStrengthTicker();
        HeightAreaEffectCloud.createToxicDashCloud(player.level(), player, player.getX(), player.getY() - 0.5, player.getZ());
    }

    public static void handleExtraJump(Play2ServerExtraJumpPayload payload, IPayloadContext context) {
        ServerPlayer player = (ServerPlayer) context.player();
        ExtraJumpsAttachment extraJumps = player.getData(SSAttachments.EXTRA_JUMPS);
        if (extraJumps.hasExtraJump(player)) {
            if (!NeoForge.EVENT_BUS.post(new DoubleJumpEvent(player)).isCanceled()) {
                double forwards = 0, sideways = 0;
                if (payload.left()) sideways += 0.25;
                if (payload.right()) sideways -= 0.25;
                if (payload.forward()) forwards += 0.5;
                if (payload.backward()) forwards -= 0.5;

                Vec3 delta = Util.relativeVec(new Vec2(0, player.getYRot()), forwards, 0.5, sideways);
                extraJumps.useExtraJump();

                if (player.getVehicle() == null || !SSConfig.DOUBLE_JUMP_VEHICLE.get()) {
                    player.setDeltaMovement(delta);
                    context.reply(new Play2ClientDeltaPayload(delta.x(), delta.y(), delta.z(), -1));
                } else {
                    player.getVehicle().setDeltaMovement(delta);
                    context.reply(new Play2ClientDeltaPayload(delta.x(), delta.y(), delta.z(), player.getVehicle().getId()));
                }
            }
        }
    }
}
