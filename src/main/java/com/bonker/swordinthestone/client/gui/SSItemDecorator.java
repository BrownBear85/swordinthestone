package com.bonker.swordinthestone.client.gui;

import com.bonker.swordinthestone.common.ability.SwordAbility;
import com.bonker.swordinthestone.util.AbilityUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraftforge.client.IItemDecorator;

public class SSItemDecorator {
    public static final IItemDecorator ITEM_DECORATOR = (font, stack, xOffset, yOffset, blitOffset) -> {
        SwordAbility ability = AbilityUtil.getSwordAbility(stack);
        float progress = ability.getProgress(stack);

        if (progress > 0) {
            int color = ability.getColor().getCooldownColor();
            int red = FastColor.ARGB32.red(color);
            int green = FastColor.ARGB32.green(color);
            int blue = FastColor.ARGB32.blue(color);
            int alpha = FastColor.ARGB32.alpha(color);

            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder builder = tesselator.getBuilder();

            if (ability.progressIsCooldown(stack)) {
                int minY = yOffset + Mth.floor(16 * progress);
                int maxY = minY + Mth.ceil(16 * (1 - progress));

                fillRect(builder, xOffset, minY, xOffset + 16, maxY, red, green, blue, alpha);
            } else {
                fillRect(builder, xOffset, yOffset + 11, xOffset + Math.round(16 * progress), yOffset + 16, red, green, blue, alpha);
            }

            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();
        }
        return false;
    };

    private static void fillRect(BufferBuilder builder, int minX, int minY, int maxX, int maxY, int red, int green, int blue, int alpha) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        builder.vertex(minX, minY, 0).color(red, green, blue, alpha).endVertex();
        builder.vertex(minX, maxY, 0).color(red, green, blue, alpha).endVertex();
        builder.vertex(maxX, maxY, 0).color(red, green, blue, alpha).endVertex();
        builder.vertex(maxX, minY, 0).color(red, green, blue, alpha).endVertex();
        BufferUploader.drawWithShader(builder.end());
    }
}
