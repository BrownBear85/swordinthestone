package com.bonker.swordinthestone.util;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * This event is fired on the FORGE bus, only on the server side
 */
public class DoubleJumpEvent extends PlayerEvent implements ICancellableEvent {
    public DoubleJumpEvent(Player player) {
        super(player);
    }
}
