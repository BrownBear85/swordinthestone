package com.bonker.swordinthestone.client.renderer;

import com.bonker.swordinthestone.SwordInTheStone;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.registries.ForgeRegistries;
public class SSBEWLR extends BlockEntityWithoutLevelRenderer {
    public static SSBEWLR INSTANCE;
    public static IItemRenderProperties extension() {return new IItemRenderProperties() {
        @Override
        public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
            return SSBEWLR.INSTANCE;
        }
    };}

    public SSBEWLR(BlockEntityRenderDispatcher pBlockEntityRenderDispatcher, EntityModelSet pEntityModelSet) {
        super(pBlockEntityRenderDispatcher, pEntityModelSet);
    }

    @Override
    public void renderByItem(ItemStack pStack, ItemTransforms.TransformType pDisplayContext, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        ResourceLocation swordModel = SwordInTheStone.SWORD_MODEL_MAP.get(ForgeRegistries.ITEMS.getKey(pStack.getItem()));
        ResourceLocation abilityModel = SwordInTheStone.ABILITY_MODEL_MAP.get(pStack.getOrCreateTag().getString("ability"));

        pPoseStack.popPose(); // remove translations from ItemRenderer
        pPoseStack.pushPose();

        render(pStack, swordModel, pDisplayContext, pPoseStack, pBuffer, pStack.hasFoil(), pPackedLight, pPackedOverlay);
        if (abilityModel != null) render(pStack, abilityModel, pDisplayContext, pPoseStack, pBuffer, pStack.hasFoil(), pPackedLight, pPackedOverlay);
    }

    private static void render(ItemStack stack, ResourceLocation modelLoc, ItemTransforms.TransformType displayContext, PoseStack poseStack, MultiBufferSource bufferSource, boolean glint, int packedLight, int packedOverlay) {
        poseStack.pushPose();

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        BakedModel model = Minecraft.getInstance().getModelManager().getModel(modelLoc);
        model = ForgeHooksClient.handleCameraTransforms(poseStack, model, displayContext, displayContext == ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND || displayContext == ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND);
        poseStack.translate(-0.5, -0.5, -0.5); // replicate ItemRenderer translation

        boolean inGui = displayContext == ItemTransforms.TransformType.GUI;
        if (inGui) {
            Lighting.setupForFlatItems();
        }

        RenderType renderType = ItemBlockRenderTypes.getRenderType(stack, true);
        VertexConsumer vertexConsumer = ItemRenderer.getFoilBuffer(bufferSource, renderType, true, glint);
        itemRenderer.renderModelLists(model, stack, packedLight, packedOverlay, poseStack, vertexConsumer);

        if (inGui) {
            ((MultiBufferSource.BufferSource) bufferSource).endBatch();
            Lighting.setupFor3DItems();
        }

        poseStack.popPose();
    }
}
