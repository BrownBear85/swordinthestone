package com.bonker.swordinthestone.server.attachment;

import com.bonker.swordinthestone.common.SSAttributes;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.IntTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

public class ExtraJumpsAttachment implements INBTSerializable<IntTag> {
    private int extraJumps = 0;

    public boolean hasExtraJump(Player player) {
        return player.getAttributeValue(SSAttributes.JUMPS) - extraJumps > 0;
    }

    public void resetExtraJumps() {
        extraJumps = 0;
    }

    public void useExtraJump() {
        extraJumps++;
    }

    @Override
    public @UnknownNullability IntTag serializeNBT(HolderLookup.Provider provider) {
        return IntTag.valueOf(extraJumps);
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, IntTag nbt) {
        extraJumps = nbt.getAsInt();
    }
}
