package crystalspider.justverticalslabs;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.StairsShape;

public class VerticalSlabBlock extends Block implements SimpleWaterloggedBlock, EntityBlock {
  public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
  public static final EnumProperty<StairsShape> SHAPE = BlockStateProperties.STAIRS_SHAPE;
  public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
  private static final VoxelShape[] SHAPES = makeShapes();
  private static final int[] SHAPE_BY_STATE = new int[]{0, 1, 2, 3, 5, 4, 6, 7, 5, 6, 7, 5, 9, 8, 10, 11, 9, 10, 11, 9};

  public VerticalSlabBlock(Properties properties) {
    super(properties);
    this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(SHAPE, StairsShape.STRAIGHT).setValue(WATERLOGGED, Boolean.valueOf(false)));
  }

  public static boolean isVerticalSlab(BlockState blockState) {
    return blockState.getBlock() instanceof VerticalSlabBlock;
  }

  private static VoxelShape[] makeShapes() {
    // S = 0, W = 1, N = 2, E = 3
    // ST = 0, IL = 1, IR = 2, OL = 3, OR = 4
    VoxelShape S = verticalBox(0, 2, 1, 2);
    VoxelShape W = verticalBox(0, 1, 0, 2);
    VoxelShape N = verticalBox(0, 2, 0, 1);
    VoxelShape E = verticalBox(1, 2, 0, 2);

    VoxelShape ISW = Shapes.or(S, W);
    VoxelShape INW = Shapes.or(N, W);
    VoxelShape ISE = Shapes.or(S, E);
    VoxelShape INE = Shapes.or(N, E);

    VoxelShape OSW = verticalBox(0, 1, 1, 2);
    VoxelShape ONW = verticalBox(0, 1, 0, 1);
    VoxelShape OSE = verticalBox(1, 2, 1, 2);
    VoxelShape ONE = verticalBox(1, 2, 0, 1);

    return new VoxelShape[]{S, W, N, E, ISW, ISE, INW, INE, OSW, OSE, ONW, ONE};
  }

  private static VoxelShape verticalBox(double originX, double x, double originZ, double z) {
    return Block.box(originX * 8, 0.0D, originZ * 8, x * 8, 16.0D, z * 8);
  }

  private static StairsShape getStairsShape(BlockState state, BlockGetter getter, BlockPos pos) {
    Direction direction = state.getValue(FACING);
    BlockState facingBlockState = getter.getBlockState(pos.relative(direction));
    if (isVerticalSlab(facingBlockState)) {
      Direction facingBlockDirection = facingBlockState.getValue(FACING);
      if (facingBlockDirection.getAxis() != direction.getAxis() && canTakeShape(state, getter, pos, facingBlockDirection.getOpposite())) {
        if (facingBlockDirection == direction.getCounterClockWise()) {
          return StairsShape.OUTER_LEFT;
        }
        return StairsShape.OUTER_RIGHT;
      }
    }

    BlockState oppositeBlockState = getter.getBlockState(pos.relative(direction.getOpposite()));
    if (isVerticalSlab(oppositeBlockState)) {
      Direction oppositeBlockDirection = oppositeBlockState.getValue(FACING);
      if (oppositeBlockDirection.getAxis() != direction.getAxis() && canTakeShape(state, getter, pos, oppositeBlockDirection)) {
        if (oppositeBlockDirection == direction.getCounterClockWise()) {
          return StairsShape.INNER_LEFT;
        }
        return StairsShape.INNER_RIGHT;
      }
    }

    return StairsShape.STRAIGHT;
  }

  private static boolean canTakeShape(BlockState state, BlockGetter getter, BlockPos pos, Direction direction) {
    BlockState blockstate = getter.getBlockState(pos.relative(direction));
    return !isVerticalSlab(blockstate) || blockstate.getValue(FACING) != state.getValue(FACING);
  }

  @Override
  public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext collisionContext) {
    return SHAPES[SHAPE_BY_STATE[this.getShapeIndex(state)]];
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
    BlockPos pos = placeContext.getClickedPos();
    BlockState blockstate = this.defaultBlockState().setValue(FACING, placeContext.getHorizontalDirection()).setValue(WATERLOGGED, Boolean.valueOf(placeContext.getLevel().getFluidState(pos).getType() == Fluids.WATER));
    return blockstate.setValue(SHAPE, getStairsShape(blockstate, placeContext.getLevel(), pos));
  }

  @Override
  public BlockState updateShape(BlockState state, Direction direction, BlockState blockState, LevelAccessor accessor, BlockPos pos, BlockPos blockPos) {
    if (state.getValue(WATERLOGGED)) {
      accessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(accessor));
    }
    return state.setValue(SHAPE, getStairsShape(state, accessor, pos));
  }

  @Override
  public BlockState rotate(BlockState state, Rotation rotation) {
    return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
  }

  // TODO
  public BlockState mirror(BlockState p_56919_, Mirror p_56920_) {
    Direction direction = p_56919_.getValue(FACING);
    StairsShape stairsshape = p_56919_.getValue(SHAPE);
    switch(p_56920_) {
      case LEFT_RIGHT:
        if (direction.getAxis() == Direction.Axis.Z) {
          switch(stairsshape) {
            case INNER_LEFT:
              return p_56919_.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_RIGHT);
            case INNER_RIGHT:
              return p_56919_.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_LEFT);
            case OUTER_LEFT:
              return p_56919_.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_RIGHT);
            case OUTER_RIGHT:
              return p_56919_.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_LEFT);
            default:
              return p_56919_.rotate(Rotation.CLOCKWISE_180);
          }
        }
        break;
      case FRONT_BACK:
        if (direction.getAxis() == Direction.Axis.X) {
          switch(stairsshape) {
            case INNER_LEFT:
              return p_56919_.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_LEFT);
            case INNER_RIGHT:
              return p_56919_.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_RIGHT);
            case OUTER_LEFT:
              return p_56919_.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_RIGHT);
            case OUTER_RIGHT:
              return p_56919_.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_LEFT);
            case STRAIGHT:
              return p_56919_.rotate(Rotation.CLOCKWISE_180);
          }
        }
        break;
      default: break;
    }
    return super.mirror(p_56919_, p_56920_);
 }

  @Override
  public FluidState getFluidState(BlockState state) {
    return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
  }

  @Override
  public boolean useShapeForLightOcclusion(BlockState state) {
    return true;
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateDefinition) {
    stateDefinition.add(FACING, SHAPE, WATERLOGGED);
  }

  @Override
  public boolean isPathfindable(BlockState state, BlockGetter getter, BlockPos pos, PathComputationType computationType) {
    return false;
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new VerticalSlabBlockEntity(pos, state);
  }

  private int getShapeIndex(BlockState state) {
    return state.getValue(SHAPE).ordinal() * 4 + state.getValue(FACING).get2DDataValue();
  }
}
