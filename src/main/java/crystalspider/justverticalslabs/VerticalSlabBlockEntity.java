package crystalspider.justverticalslabs;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;

public class VerticalSlabBlockEntity extends BlockEntity {
  public static final ModelProperty<BlockState> REFERRING_BLOCK_STATE = new ModelProperty<BlockState>();

  private BlockState referringBlockState = Blocks.OAK_PLANKS.defaultBlockState();

  public VerticalSlabBlockEntity(BlockPos pos, BlockState state) {
    super(JustVerticalSlabsLoader.VERTICAL_SLAB_BLOCK_ENTITY.get(), pos, state);
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

  // The default one already calls load, so overriding should be useless.
  // @Override
  // public void handleUpdateTag(CompoundTag tag) {
  //   loadReferringBlockState(tag);
  //   super.handleUpdateTag(tag);
  // }

  @Override
  public Packet<ClientGamePacketListener> getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  } 

  @Override
  public IModelData getModelData() {
    return new ModelDataMap.Builder().withInitial(REFERRING_BLOCK_STATE, referringBlockState).build();
  }

  private CompoundTag saveReferringBlockState(CompoundTag tag) {
    tag.put("referringBlockState", NbtUtils.writeBlockState(referringBlockState));
    return tag;
  }

  private void loadReferringBlockState(CompoundTag tag) {
    referringBlockState = NbtUtils.readBlockState(tag.getCompound("referringBlockState"));
  }
}
