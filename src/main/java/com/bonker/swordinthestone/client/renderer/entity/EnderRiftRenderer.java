package com.bonker.swordinthestone.client.renderer.entity;

import com.bonker.swordinthestone.common.entity.EnderRift;
import com.bonker.swordinthestone.util.Util;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class EnderRiftRenderer extends EntityRenderer<EnderRift> {
    private static final ResourceLocation TEXTURE_LOCATION = Util.makeResource("textures/entity/ender_rift.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(TEXTURE_LOCATION);

    public EnderRiftRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected int getBlockLightLevel(EnderRift entity, BlockPos pos) {
        return 15;
    }

    @Override
    public void render(EnderRift entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        PoseStack.Pose stackPose = poseStack.last();
        Matrix4f matrix4f = stackPose.pose();
        Matrix3f normal = stackPose.normal();
        VertexConsumer consumer = buffer.getBuffer(RENDER_TYPE);
        vertex(consumer, matrix4f, stackPose, packedLight, 0.0F, 0, 0, 1);
        vertex(consumer, matrix4f, stackPose, packedLight, 1.0F, 0, 1, 1);
        vertex(consumer, matrix4f, stackPose, packedLight, 1.0F, 1, 1, 0);
        vertex(consumer, matrix4f, stackPose, packedLight, 0.0F, 1, 0, 0);
        poseStack.popPose();
        super.render(entity, yaw, partialTick, poseStack, buffer, packedLight);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix4f, PoseStack.Pose pose, int lightMapUV, float x, int y, int u, int v) {
        consumer.addVertex(matrix4f, x - 0.5F, (float)y - 0.25F, 0.0F).setColor(255, 255, 255, 255).setUv((float)u, (float)v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(lightMapUV).setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(EnderRift entity) {
        return TEXTURE_LOCATION;
    }
}
