package com.bonker.swordinthestone.common.block.entity;

import com.bonker.swordinthestone.common.SSConfig;
import com.bonker.swordinthestone.common.SSSounds;
import com.bonker.swordinthestone.common.block.SSBlocks;
import com.bonker.swordinthestone.common.block.SwordStoneBlock;
import com.bonker.swordinthestone.common.item.UniqueSwordItem;
import com.bonker.swordinthestone.common.networking.payloads.Play2ClientSwordStoneDataPayload;
import com.bonker.swordinthestone.common.networking.payloads.Play2ClientSwordStoneItemPayload;
import com.bonker.swordinthestone.util.AbilityUtil;
import com.bonker.swordinthestone.util.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;

public class SwordStoneMasterBlockEntity extends BlockEntity implements ISwordStoneBlockEntity {
    public static final String ITEM_TAG = "Item";
    public static final String VARIANT_TAG = "variant";
    public static final String FILLED_SWORD = "filled";

    public static final int BEACON_ANIMATION_TIME = 140;
    public static final int BEACON_ANIMATION_CYCLE = 600;
    public static final int SHAKE_ANIMATION_TIME = 10;
    public static final int REQUIRED_SHAKES = 47;
    public static final int IDLE_TIME = 100;

    private static final ParticleOptions PARTICLE_FALLBACK = ParticleTypes.NOTE;

    private ItemStack stack = ItemStack.EMPTY;
    public short progress = 0;
    public int ticksSinceLastInteraction = 0;
    public int idleTicks;
    public boolean hasSword = false;
    private boolean sendSyncPacket = false;
    private String variant = "";

    private final AABB renderBox;
    private final BlockPos[] blocks;
    private ParticleOptions particle;

    public SwordStoneMasterBlockEntity(BlockPos pPos, BlockState pState) {
        super(SSBlockEntities.SWORD_STONE_MASTER.get(), pPos, pState);

        double centerX = pPos.getX() + centerXOffset();
        double centerZ = pPos.getZ() + centerZOffset();
        renderBox = new AABB(centerX - 1, pPos.getY(), centerZ - 1, centerX + 1, pPos.getY() + 256, centerZ + 1);
        blocks = new BlockPos[] {pPos, pPos.south(), pPos.east(), pPos.south().east()};
    }

    @Override
    public InteractionResult interact(Player pPlayer) {
        assert level != null;

        if (cannotInteract() || ticksSinceLastInteraction <= SHAKE_ANIMATION_TIME) return InteractionResult.PASS;

        ticksSinceLastInteraction = 0;
        if (++progress >= REQUIRED_SHAKES) {
            progress = 0;
            finish(pPlayer);
        } else {
            level.playSound(pPlayer, getBlockPos(), SSSounds.SWORD_PULL.get(), SoundSource.BLOCKS, Mth.clamp(progress - 2, 0, 10) * 0.1F, Math.max(progress * 0.03F, 1.1F));
        }
        level.playSound(pPlayer, getBlockPos(), SSSounds.ROCK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);

        if (!level.isClientSide) {
            PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, new ChunkPos(getBlockPos()), new Play2ClientSwordStoneDataPayload(getBlockPos(), true, progress));
        }

        Vec3 position = new Vec3(getBlockPos().getX() + centerXOffset(), getBlockPos().getY() + 1, getBlockPos().getZ() + centerZOffset());
        if (level.isClientSide) {
            for (int i = 0; i < 10; i++) {
                level.addParticle(getShakeParticle(), position.x, position.y, position.z, level.random.nextFloat() - 0.5F, level.random.nextFloat() * 0.5F, level.random.nextFloat() - 0.5F);
            }
        }

        return InteractionResult.SUCCESS;
    }

    private void finish(Player player) {
        assert level != null;

        for (BlockPos pos : blocks) {
            BlockState state = level.getBlockState(pos);
            level.addDestroyBlockEffect(pos, state);
            if (state.is(SSBlocks.SWORD_STONE.get())) {
                hasSword = false;
            }
        }

        Vec3 position = new Vec3(getBlockPos().getX() + centerXOffset(), getBlockPos().getY() + 1.3, getBlockPos().getZ() + centerZOffset());

        if (level.isClientSide) {
            level.addParticle(ParticleTypes.EXPLOSION, position.x, position.y, position.z, 1.0D, 0.0D, 0.0D);
        }

        level.playSound(player, position.x, position.y, position.z, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 1.0F, 1.2F);
        level.playSound(player, position.x, position.y, position.z, SSSounds.SUCCESS.get(), SoundSource.BLOCKS, 1.0F, 1.0F);

        ItemEntity item = new ItemEntity(level, position.x(), position.y(), position.z(), getItem());
        item.setDeltaMovement(0, 0.3, 0);
        item.setPickUpDelay(20);
        level.addFreshEntity(item);

        setItem(ItemStack.EMPTY);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState ignored, SwordStoneMasterBlockEntity entity) {
        if (!level.isClientSide && !entity.variant.equals(FILLED_SWORD)) {
            entity.fillSword();
        }

        if (entity.sendSyncPacket && !level.isClientSide) {
            sendSyncPacket(entity);
            entity.sendSyncPacket = false;
        }

        if (!entity.hasSword) return;

        entity.ticksSinceLastInteraction++;
        if (entity.progress > 0 && entity.ticksSinceLastInteraction >= IDLE_TIME) {
            entity.progress = 0;
        }

        if (SSConfig.SWORD_BEACON_ENABLED.get()) {
            if (entity.isIdle()) {
                entity.idleTicks++;

                if (!level.isClientSide) {
                    if (entity.idleTicks >= BEACON_ANIMATION_CYCLE) {
                        entity.idleTicks = 0;

                        PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, new ChunkPos(entity.getBlockPos()), new Play2ClientSwordStoneDataPayload(blockPos, false, (short) 0));
                    }
                } else if (entity.idleTicks == 75) {
                    level.playLocalSound(entity.getBlockPos(), SSSounds.LASER.get(), SoundSource.BLOCKS, 4.5F, 0.6F + level.random.nextFloat() * 0.8F, false);
                }
            } else {
                entity.idleTicks = 0;
            }
        }
    }

    public boolean cannotInteract() {
        return !hasSword || (idleTicks > 0 && idleTicks < SwordStoneMasterBlockEntity.BEACON_ANIMATION_TIME);
    }

    @Override
    @Nullable
    public SwordStoneMasterBlockEntity getMaster() {
        return this;
    }

    private ParticleOptions getShakeParticle() {
        if (level != null && particle == null) particle = new BlockParticleOption(ParticleTypes.BLOCK, level.getBlockState(getBlockPos()));
        return particle == null ? PARTICLE_FALLBACK : particle;
    }

    private void fillSword() {
        setItem(UniqueSwordItem.getRandom(variant, level == null ? RandomSource.create() : level.random));
        variant = FILLED_SWORD;
        sendSyncPacket = true;
    }

    private static void sendSyncPacket(SwordStoneMasterBlockEntity entity) {
        PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) entity.level, new ChunkPos(entity.getBlockPos()), new Play2ClientSwordStoneItemPayload(entity.getBlockPos(), entity.stack));
    }

    public void setItem(ItemStack stack) {
        assert level != null;

        this.stack = stack.copy();

        hasSword = !stack.isEmpty();
        setChanged();
        if (!level.isClientSide) {
            sendSyncPacket(this);
        }
    }

    /** @return a copy of the held stack */
    public ItemStack getItem() {
        return stack.copy();
    }

    public int getBeamColor() {
        if (stack.getItem() instanceof UniqueSwordItem uniqueSwordItem) {
            Color color = UniqueSwordItem.COLOR_TABLE.get(uniqueSwordItem, AbilityUtil.getSwordAbility(stack));
            if (color != null) return color.getValue();
        }
        return AbilityUtil.getSwordAbility(stack).getColor().getValue();
    }

    public Direction getDirection() {
        if (level == null) return Direction.NORTH;
        BlockState state = level.getBlockState(getBlockPos());
        if (!state.hasProperty(SwordStoneBlock.FACING)) return Direction.NORTH;
        return state.getValue(SwordStoneBlock.FACING);
    }

    public boolean isIdle() {
        return progress == 0;
    }

    public double centerXOffset() {
        return getDirection() == Direction.NORTH || getDirection() == Direction.WEST ? 1 : 0;
    }

    public double centerZOffset() {
        return getDirection() == Direction.NORTH || getDirection() == Direction.EAST ? 1 : 0;
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        if (!pTag.contains(ITEM_TAG)) {
            stack = ItemStack.EMPTY;
        } else {
            stack = ItemStack.parse(pRegistries, pTag.getCompound(ITEM_TAG)).orElse(ItemStack.EMPTY);
        }
        hasSword = !stack.isEmpty();
        variant = pTag.getString(VARIANT_TAG);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        if (!stack.isEmpty()) {
            pTag.put(ITEM_TAG, stack.save(pRegistries));
        }
        pTag.putString(VARIANT_TAG, variant);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

//    @Override
//    public AABB getRenderBoundingBox() {
//        return renderBox;
//    }
}
