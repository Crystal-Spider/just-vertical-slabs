package crystalspider.justverticalslabs.blocks.verticalslab;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * TODO:
 * Set correct block properties from referringBlockState (See {@link net.minecraft.world.level.block.state.BlockBehaviour}).
 * Set crafting recipes (from block to vertical slab, from vertical slab to block, from vertical slab to slab, from slab to vertical slab).
 */
public class VerticalSlabBlock extends Block implements SimpleWaterloggedBlock, EntityBlock {
  public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
  public static final EnumProperty<StairsShape> SHAPE = BlockStateProperties.STAIRS_SHAPE;
  public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
  private static final VoxelShape[] SHAPES = makeShapes();
  private static final int[] SHAPE_BY_STATE = new int[]{
    0,  // 0  Straight    - South
    1,  // 1  Straight    - West
    2,  // 2  Straight    - North
    3,  // 3  Straight    - East
    6,  // 4  Inner Left  - South
    4,  // 5  Inner Left  - West
    5,  // 6  Inner Left  - North
    7,  // 7  Inner Left  - East
    4,  // 8  Inner Right - South
    5,  // 9  Inner Right - West
    7,  // 10 Inner Right - North
    6,  // 11 Inner Right - East
    10, // 12 Outer Left  - South
    8,  // 13 Outer Left  - West
    9,  // 14 Outer Left  - North
    11, // 15 Outer Left  - East
    8,  // 16 Outer Right - South
    9,  // 17 Outer Right - West
    11, // 18 Outer Right - North
    10  // 19 Outer Right - East
  };

  public VerticalSlabBlock() {
    super(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD));
    this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(SHAPE, StairsShape.STRAIGHT).setValue(WATERLOGGED, Boolean.valueOf(false)));
  }

  public static boolean isVerticalSlab(BlockState blockState) {
    return blockState.getBlock() instanceof VerticalSlabBlock;
  }

  private static VoxelShape[] makeShapes() {
    // S = 0, W = 1, N = 2, E = 3
    // ST = 0, IL = 1, IR = 2, OL = 3, OR = 4
    VoxelShape facingSouthShape = verticalBox(0, 1, 2, 2);
    VoxelShape facingWestShape = verticalBox(0, 0, 1, 2);
    VoxelShape facingNorthShape = verticalBox(0, 0, 2, 1);
    VoxelShape facingEastShape = verticalBox(1, 0, 2, 2);

    VoxelShape innerLeftBottomShape = Shapes.or(facingSouthShape, facingWestShape);
    VoxelShape innerLeftTopShape = Shapes.or(facingNorthShape, facingWestShape);
    VoxelShape innerRightBottomShape = Shapes.or(facingSouthShape, facingEastShape);
    VoxelShape innerRightTopShape = Shapes.or(facingNorthShape, facingEastShape);

    VoxelShape outerLeftBottomShape = verticalBox(0, 1, 1, 2);
    VoxelShape outerLeftTopShape = verticalBox(0, 0, 1, 1);
    VoxelShape outerRightBottomShape = verticalBox(1, 1, 2, 2);
    VoxelShape outerRightTopShape = verticalBox(1, 0, 2, 1);

    return new VoxelShape[]{
      facingSouthShape,       // 0
      facingWestShape,        // 1
      facingNorthShape,       // 2
      facingEastShape,        // 3
      innerLeftBottomShape,   // 4
      innerLeftTopShape,      // 5
      innerRightBottomShape,  // 6
      innerRightTopShape,     // 7
      outerLeftBottomShape,   // 8
      outerLeftTopShape,      // 9
      outerRightBottomShape,  // 10
      outerRightTopShape      // 11
    };
  }

  private static VoxelShape verticalBox(double originX, double originZ, double x, double z) {
    return Block.box(originX * 8, 0.0D, originZ * 8, x * 8, 16.0D, z * 8);
  }

  private static StairsShape getStairsShape(BlockState state, BlockGetter getter, BlockPos pos) {
    Direction direction = state.getValue(FACING);
    BlockState facingBlockState = getter.getBlockState(pos.relative(direction));
    if (isVerticalSlab(facingBlockState)) {
      Direction facingBlockDirection = facingBlockState.getValue(FACING);
      if (facingBlockDirection.getAxis() != direction.getAxis() && canTakeShape(state, getter, pos, facingBlockDirection.getOpposite())) {
        if (facingBlockDirection == direction.getClockWise()) {
          return StairsShape.OUTER_RIGHT;
        }
        return StairsShape.OUTER_LEFT;
      }
    }

    BlockState oppositeBlockState = getter.getBlockState(pos.relative(direction.getOpposite()));
    if (isVerticalSlab(oppositeBlockState)) {
      Direction oppositeBlockDirection = oppositeBlockState.getValue(FACING);
      if (oppositeBlockDirection.getAxis() != direction.getAxis() && canTakeShape(state, getter, pos, oppositeBlockDirection)) {
        if (oppositeBlockDirection == direction.getClockWise()) {
          return StairsShape.INNER_RIGHT;
        }
        return StairsShape.INNER_LEFT;
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

  @Override
  @SuppressWarnings("deprecation")
  public BlockState mirror(BlockState state, Mirror mirror) {
    Direction direction = state.getValue(FACING);
    StairsShape stairsshape = state.getValue(SHAPE);
    switch(mirror) {
      case LEFT_RIGHT:
        if (direction.getAxis() == Direction.Axis.Z) {
          switch(stairsshape) {
            case INNER_LEFT:
              return state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_RIGHT);
            case INNER_RIGHT:
              return state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_LEFT);
            case OUTER_LEFT:
              return state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_RIGHT);
            case OUTER_RIGHT:
              return state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_LEFT);
            default:
              return state.rotate(Rotation.CLOCKWISE_180);
          }
        }
        break;
      case FRONT_BACK:
        if (direction.getAxis() == Direction.Axis.X) {
          switch(stairsshape) {
            case INNER_LEFT:
              return state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_LEFT);
            case INNER_RIGHT:
              return state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_RIGHT);
            case OUTER_LEFT:
              return state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_RIGHT);
            case OUTER_RIGHT:
              return state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_LEFT);
            case STRAIGHT:
              return state.rotate(Rotation.CLOCKWISE_180);
          }
        }
        break;
      default: break;
    }
    return super.mirror(state, mirror);
 }

  @Override
  @SuppressWarnings("deprecation")
  public FluidState getFluidState(BlockState state) {
    return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
  }

  @Override
  public boolean useShapeForLightOcclusion(BlockState state) {
    return true;
  }
  
  @Override
  public boolean isPathfindable(BlockState state, BlockGetter getter, BlockPos pos, PathComputationType computationType) {
    return false;
  }
  
  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new VerticalSlabBlockEntity(pos, state);
  }
  
  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateDefinition) {
    stateDefinition.add(FACING, SHAPE, WATERLOGGED);
  }

  @Override
  public ItemStack getCloneItemStack(BlockGetter getter, BlockPos pos, BlockState state) {
    BlockEntity blockEntity = getter.getBlockEntity(pos);
    ItemStack itemStack = new ItemStack(this);
    if (blockEntity != null && blockEntity instanceof VerticalSlabBlockEntity) {
      blockEntity.saveToItem(itemStack);
    }
    return itemStack;
  }

  private int getShapeIndex(BlockState state) {
    return state.getValue(SHAPE).ordinal() * 4 + state.getValue(FACING).get2DDataValue();
  }
}
