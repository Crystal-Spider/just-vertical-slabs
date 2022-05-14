package crystalspider.justverticalslabs.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Translucent Vertical Slab Block.
 */
public class TranslucentVerticalSlabBlock extends VerticalSlabBlock {
  public TranslucentVerticalSlabBlock() {
    super(Material.GLASS);
  }

  // TODO: mimick property
  @Override
  public boolean useShapeForLightOcclusion(BlockState state) {
    return false;
  }

  // TODO: mimick property (maybe already in VerticalSlabBlock)
  public boolean propagatesSkylightDown(BlockState state, BlockGetter getter, BlockPos pos) {
    return !isShapeFullBlock(state.getShape(getter, pos)) && state.getFluidState().isEmpty();
  }

  // TODO: Override necessary?
  @Override
  public VoxelShape getOcclusionShape(BlockState state, BlockGetter getter, BlockPos pos) {
    return Shapes.empty();
  }

  // TODO: mimick property
  @Override
  public int getLightBlock(BlockState state, BlockGetter getter, BlockPos pos) {
    if (state.isSolidRender(getter, pos)) {
       return getter.getMaxLightLevel();
    } else {
       return state.propagatesSkylightDown(getter, pos) ? 0 : 1;
    }
 }
}
