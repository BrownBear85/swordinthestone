package com.bonker.swordinthestone.util;

import net.minecraft.network.chat.Style;
import net.minecraft.util.FastColor;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;

import javax.annotation.Nullable;

public class Color {
    private final int value, cooldown;
    private final Style style;
    private final float[] diffusedColor;
    @Nullable
    private TooltipColors tooltipColors;

    public Color(int value) {
        this.value = value;
        this.cooldown = value | 0x7F000000;
        this.style = Style.EMPTY.withColor(value);
        this.diffusedColor = diffuseColor(value);
    }

    public int getValue() {
        return value;
    }

    public int getCooldownColor() {
        return cooldown;
    }

    public Style getStyle() {
        return style;
    }

    public float[] getDiffusedColor() {
        return diffusedColor;
    }

    public void makeTooltipColors(int abilityRGB, int swordRGB) {
        int bgStart = FastColor.ARGB32.multiply(swordRGB | 0xF0000000, 0xFF444444);
        int bgEnd = FastColor.ARGB32.multiply(abilityRGB | 0xF0000000, 0xFF333333);
        int borderStart = abilityRGB | 0xF0000000;
        int borderEnd = (borderStart & 0xFEFEFE) >> 1 | borderStart & 0xFF000000;

        this.tooltipColors = new TooltipColors(bgStart, bgEnd, borderStart, borderEnd);
    }

    public void applyTooltipColors(RenderTooltipEvent.Color event) {
        if (tooltipColors == null) return;

        event.setBackgroundStart(tooltipColors.bgStart);
        event.setBackgroundEnd(tooltipColors.bgEnd);
        event.setBorderStart(tooltipColors.borderStart);
        event.setBorderEnd(tooltipColors.borderEnd);
    }

    public static float[] diffuseColor(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        return new float[] {r / 255F, g / 255F, b / 255F};
    }

    public static Color uniqueSwordColor(int abilityRGB, int swordRGB) {
        int r1 = (abilityRGB >> 16) & 0xFF; // diffuse ability color
        int g1 = (abilityRGB >> 8) & 0xFF;
        int b1 = abilityRGB & 0xFF;

        int r2 = (swordRGB >> 16) & 0xFF; // diffuse sword color
        int g2 = (swordRGB >> 8) & 0xFF;
        int b2 = swordRGB & 0xFF;

        int r3 = (r1 + r2) / 2; // mix colors
        int g3 = (g1 + g2) / 2;
        int b3 = (b1 + b2) / 2;

        int greatestInAbility; // find the greatest color value in the ability color (r, g, or b)
        if (r1 >= g1 && r1 >= b1) {
            greatestInAbility = r1;
        } else if (g1 >= r1 && g1 >= b1) {
            greatestInAbility = g1;
        } else {
            greatestInAbility = b1;
        }

        int addToMixed; // find how much to add to each color value of the mixed color to make it just as bright as the ability color
        if (r3 >= g3 && r3 >= b3) {
            addToMixed = greatestInAbility - r3;
        } else if (g3 >= r3 && g3 >= b3) {
            addToMixed = greatestInAbility - g3;
        } else {
            addToMixed = greatestInAbility - b3;
        }

        r3 += addToMixed; // add to each color value of the mixed color
        g3 += addToMixed;
        b3 += addToMixed;

        int mixed = r3; // pack mixed rgb as int
        mixed = (mixed << 8) + g3;
        mixed = (mixed << 8) + b3;

        Color color = new Color(mixed);
        color.makeTooltipColors(abilityRGB, swordRGB);
        return color;
    }

    private record TooltipColors(int bgStart, int bgEnd, int borderStart, int borderEnd) { }
}
