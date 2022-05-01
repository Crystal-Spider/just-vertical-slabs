package crystalspider.justverticalslabs.utils;

import javax.annotation.Nullable;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import crystalspider.justverticalslabs.blocks.verticalslab.VerticalSlabBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;

/**
 * Utility class for common operations regarding Vertical Slabs.
 */
public class VerticalSlabUtils {
  /**
   * {@link BlockState} {@link ModelProperty} to use when building and reading {@link IModelData} of a Vertical Slab.
   */
  public static final ModelProperty<BlockState> REFERRING_BLOCK_STATE = new ModelProperty<BlockState>();

  /**
   * Builds and returns a new {@link IModelData} with {@link #REFERRING_BLOCK_STATE referringBlockState property}
   * holding the value of the given {@code referringBlockState}.
   * 
   * @param referringBlockState
   * @return Vertical Slab {@link IModelData}.
   */
  public static IModelData buildModelData(BlockState referringBlockState) {
    return new ModelDataMap.Builder().withInitial(REFERRING_BLOCK_STATE, referringBlockState).build();
  }

  /**
   * Returns a Vertical Slab {@link ItemStack} with the specified {@code referringBlockState}.
   * 
   * @param itemLike - {@link ItemLike} instance of a Vertical Slab to use to get a basic {@link ItemStack}.
   * @param referringBlockState - {@link BlockState} to save in the {@link ItemStack} NBTs.
   * @return Vertical Slab {@link ItemStack}.
   */
  public static ItemStack getItemStackWithState(ItemLike itemLike, BlockState referringBlockState) {
    ItemStack itemStack = new ItemStack(itemLike);
    CompoundTag referringBlockTag = new CompoundTag();
    referringBlockTag.put("referringBlockState", NbtUtils.writeBlockState(referringBlockState));
    BlockItem.setBlockEntityData(itemStack, JustVerticalSlabsLoader.VERTICAL_SLAB_BLOCK_ENTITY.get(), referringBlockTag);
    return itemStack;
  }

  /**
   * Returns the {@link VerticalSlabBlockEntity} for the given {@link BlockGetter} and {@link BlockPos}.
   * <br>
   * Returns {@code null} if none could be found.
   * 
   * @param getter
   * @param pos
   * @return {@link VerticalSlabBlockEntity} or {@code null}.
   */
  @Nullable
  public static VerticalSlabBlockEntity getVerticalSlabBlockEntity(BlockGetter getter, BlockPos pos) {
    BlockEntity blockEntity = getter.getBlockEntity(pos);
    if (blockEntity != null && blockEntity instanceof VerticalSlabBlockEntity) {
      return (VerticalSlabBlockEntity) blockEntity;
    }
    return null;
  }

  /**
   * Returns the {@link BlockState referringBlockState} for the given {@link BlockGetter} and {@link BlockPos}.
   * <br>
   * Returns {@code null} if none could be found.
   * 
   * @param getter
   * @param pos
   * @return {@link BlockState referringBlockState} or {@code null}.
   */
  @Nullable
  public static BlockState getReferringBlockState(BlockGetter getter, BlockPos pos) {
    VerticalSlabBlockEntity blockEntity = getVerticalSlabBlockEntity(getter, pos);
    if (blockEntity != null) {
      return blockEntity.getReferringBlockState();
    }
    return null;
  }
}
