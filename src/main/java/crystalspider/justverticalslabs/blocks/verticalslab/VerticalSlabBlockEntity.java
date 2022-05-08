package crystalspider.justverticalslabs.blocks.verticalslab;

import javax.annotation.Nullable;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import crystalspider.justverticalslabs.utils.VerticalSlabUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;

public class VerticalSlabBlockEntity extends BlockEntity {
  private BlockState referringBlockState = null;

  public VerticalSlabBlockEntity(BlockPos pos, BlockState state) {
    super(JustVerticalSlabsLoader.VERTICAL_SLAB_BLOCK_ENTITY.get(), pos, state);
  }

  @Nullable
  public BlockState getReferringBlockState() {
    return referringBlockState;
  }

  @Override
  public void saveAdditional(CompoundTag tag) {
    saveReferringBlockState(tag);
  }

  @Override
  public void load(CompoundTag tag) {
    super.load(tag);
    loadReferringBlockState(tag);
  }

  @Override
  public CompoundTag getUpdateTag() {
    return saveReferringBlockState(super.getUpdateTag());
  }

  @Override
  public Packet<ClientGamePacketListener> getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }

  @Override
  public IModelData getModelData() {
    return VerticalSlabUtils.buildModelData(referringBlockState);
  }

  private CompoundTag saveReferringBlockState(CompoundTag tag) {
    if (referringBlockState != null) {
      VerticalSlabUtils.putReferringBlockState(tag, referringBlockState);
    }
    return tag;
  }

  private void loadReferringBlockState(CompoundTag tag) {
    CompoundTag referringBlockStateTag = tag.getCompound(VerticalSlabUtils.NBT_ID);
    if (referringBlockStateTag != null) {
      referringBlockState = NbtUtils.readBlockState(referringBlockStateTag);
    } else {
      referringBlockState = Blocks.AIR.defaultBlockState();
    }
  }
}
