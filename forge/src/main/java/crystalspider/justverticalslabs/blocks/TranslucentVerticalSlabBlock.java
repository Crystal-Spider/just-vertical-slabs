package crystalspider.justverticalslabs.blocks;

import java.util.Arrays;

import crystalspider.justverticalslabs.utils.VerticalSlabUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.material.FluidState;
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
    BlockState referredBlockState = VerticalSlabUtils.getReferredBlockState(getter, pos);
    if (referredBlockState != null) {
      return getReferredProperty(referredBlockState::propagatesSkylightDown, () -> true, getter, pos);
    }
    return true;
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

  @Override
  public boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter level, BlockPos pos, FluidState fluidState) {
    return true;
  }

  @Override
  public boolean supportsExternalFaceHiding(BlockState state) {
    return true;
  }

  @Override
  public boolean hidesNeighborFace(BlockGetter getter, BlockPos pos, BlockState state, BlockState neighborState, Direction direction) {
    BlockState referredSlabState = VerticalSlabUtils.getSafeReferredSlabState(getter, pos);
    BlockState neighborReferredSlabState = VerticalSlabUtils.getSafeReferredSlabState(getter, pos.relative(direction));
    if (referredSlabState != null && neighborReferredSlabState != null) {
      return (
        referredSlabState == neighborReferredSlabState &&
        new Grid(state.getValue(FACING), state.getValue(SHAPE), state.getValue(DOUBLE)).isConnected(new Grid(neighborState.getValue(FACING), neighborState.getValue(SHAPE), neighborState.getValue(DOUBLE)), direction)
      );
    }
    /**
     * TODO:
     * - Apply a mixin on Block#shouldRenderFace to hide Vertical Slab faces when against mimicked blocks.
     * - Hide mimicked blocks faces.
     */
    return false;
  }

  /**
   * Grid like structure representing the disposition of a Vertical Slab in a single block.
   */
  private class Grid {
    /**
     * Number of quadrants in a grid.
     */
    private final short count = 4;

    /**
     * Value of the quadrants: false if empty, true if full.
     */
    private final boolean[] quadrants = new boolean[count];

    /**
     * @param facing
     * @param stairsShape
     * @param isDouble
     */
    Grid(Direction facing, StairsShape stairsShape, boolean isDouble) {
      if (isDouble) {
        Arrays.fill(quadrants, true);
      } else {
        switch (stairsShape) {
          case STRAIGHT:
            quadrants[2] = true;
            quadrants[3] = true;
            break;
          case INNER_LEFT:
            quadrants[1] = true;
            quadrants[2] = true;
            quadrants[3] = true;
            break;
          case INNER_RIGHT:
            quadrants[0] = true;
            quadrants[2] = true;
            quadrants[3] = true;
            break;
          case OUTER_LEFT:
            quadrants[2] = true;
            break;
          case OUTER_RIGHT:
            quadrants[3] = true;
            break;
        }
        rotateQuadrants(facing);
      }
    }

    /**
     * Checks whether this Vertical Slab {@link Grid} has a matching connection with the given {@code neighbor} for the specified {@code connection direction}.
     * 
     * @param neighbor - {@link Grid} of the neighboring Vertical Slab.
     * @param direction - {@link Direction} of the connection, if any.
     * @return whether this Vertical Slab {@link Grid} has a matching connection with the given {@code neighbor}.
     */
    boolean isConnected(Grid neighbor, Direction direction) {
      if (direction.getAxis() == Axis.Y) {
        return Arrays.equals(quadrants, neighbor.quadrants);
      }
      switch (direction) {
        case NORTH: return quadrants[0] == neighbor.quadrants[3] && quadrants[1] == neighbor.quadrants[2] && (quadrants[0] || quadrants[1]);
        case SOUTH: return quadrants[3] == neighbor.quadrants[0] && quadrants[2] == neighbor.quadrants[1] && (quadrants[3] || quadrants[2]);
        case WEST: return quadrants[0] == neighbor.quadrants[1] && quadrants[3] == neighbor.quadrants[2] && (quadrants[0] || quadrants[3]);
        case EAST: return quadrants[1] == neighbor.quadrants[0] && quadrants[2] == neighbor.quadrants[3] && (quadrants[1] || quadrants[2]);
        default: return false;
      }
    }

    /**
     * Rotate the value of the quadrants for the given {@link Direction}.
     * 
     * @param facing
     */
    private void rotateQuadrants(Direction facing) {
      for (short s = 1; s <= facing.get2DDataValue(); s++) {
        boolean tmp = quadrants[count - 1];
        for (short c = count - 1; c > 0; c--) {
          quadrants[c] = quadrants[c - 1];
        }
        quadrants[0] = tmp;
      }
    }
  }
}
