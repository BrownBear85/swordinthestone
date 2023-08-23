package com.bonker.swordinthestone.common.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface IExtraJumpsCapability extends INBTSerializable<CompoundTag> {
    int extraJumpsUsed();

    void resetExtraJumps();

    void useExtraJump();
}
