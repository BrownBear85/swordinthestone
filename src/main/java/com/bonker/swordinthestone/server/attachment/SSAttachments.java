package com.bonker.swordinthestone.server.attachment;

import com.bonker.swordinthestone.SwordInTheStone;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class SSAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, SwordInTheStone.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<DashAttachment>> DASH =
            ATTACHMENT_TYPES.register("dash", () -> AttachmentType.builder(DashAttachment::new).build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ExtraJumpsAttachment>> EXTRA_JUMPS =
            ATTACHMENT_TYPES.register("extra_jumps", () -> AttachmentType.serializable(ExtraJumpsAttachment::new).build());
}
