package com.bonker.swordinthestone.server.attachment;

import net.minecraft.world.entity.Entity;

import java.util.ArrayList;

public class DashAttachment {
    private int dashTicks = 0;
    private final ArrayList<Entity> dashedEntities = new ArrayList<>();

    public int getDashTicks() {
        return dashTicks;
    }

    public void setDashTicks(int dashTicks) {
        this.dashTicks = dashTicks;
    }

    public void addToDashed(Entity entity) {
        dashedEntities.add(entity);
    }

    public boolean isDashed(Entity entity) {
        return dashedEntities.contains(entity);
    }

    public void clearDashed() {
        dashedEntities.clear();
    }
}
