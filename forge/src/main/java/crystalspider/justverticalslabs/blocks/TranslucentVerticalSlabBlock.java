package crystalspider.justverticalslabs.blocks;

import crystalspider.justverticalslabs.utils.VerticalSlabUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Translucent Vertical Slab Block.
 */
public class TranslucentVerticalSlabBlock extends VerticalSlabBlock {
  public TranslucentVerticalSlabBlock() {
    super(Material.GLASS);
  }

  @Override
  public VoxelShape getVisualShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
    return Shapes.empty();
  }

  @Override
  public VoxelShape getOcclusionShape(BlockState state, BlockGetter getter, BlockPos pos) {
    return Shapes.empty();
  }

  @Override
  public boolean useShapeForLightOcclusion(BlockState state) {
    return state.getValue(OCCLUSION);
  }

  @Override
  public boolean propagatesSkylightDown(BlockState state, BlockGetter getter, BlockPos pos) {
    return true; // TODO: Change when adding double feature.
  }

  @Override
  public float getShadeBrightness(BlockState state, BlockGetter getter, BlockPos pos) {
    BlockState referredSlabState = VerticalSlabUtils.getReferredSlabState(getter, pos);
    if (referredSlabState != null) {
      BlockState referredBlockState = VerticalSlabUtils.getReferredBlockState(referredSlabState);
      if (referredBlockState != null) {
        return getReferredProperty(referredBlockState::getShadeBrightness, () -> 1.0F, getter, pos);
      }
      return getReferredProperty(referredSlabState::getShadeBrightness, () -> 1.0F, getter, pos);
    }
    return 1.0F;
  }

  @Override
  @SuppressWarnings("deprecation")
  public int getLightBlock(BlockState state, BlockGetter getter, BlockPos pos) {
    BlockState referredSlabState = VerticalSlabUtils.getReferredSlabState(getter, pos);
    BlockState referredBlockState = VerticalSlabUtils.getReferredBlockState(referredSlabState);
    if (referredBlockState != null) {
      return getReferredProperty(referredBlockState::getLightBlock, () -> super.getLightBlock(state, getter, pos), getter, pos);
    } else {
      if (referredSlabState != null) {
        return getReferredProperty(referredSlabState::getLightBlock, () -> super.getLightBlock(state, getter, pos), getter, pos);
      }
    }
    return super.getLightBlock(state, getter, pos);
 }
}
