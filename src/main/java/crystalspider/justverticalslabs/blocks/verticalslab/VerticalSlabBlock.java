package crystalspider.justverticalslabs.blocks.verticalslab;

import javax.annotation.Nullable;

import crystalspider.justverticalslabs.utils.VerticalSlabUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.LightBlock;
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.IPlantable;

/**
 * Vertical Slab Block.
 * TODO: return jumpFactor and speedFactor based on referringBlockState.
 */
public class VerticalSlabBlock extends Block implements SimpleWaterloggedBlock, EntityBlock {
  public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
  public static final EnumProperty<StairsShape> SHAPE = BlockStateProperties.STAIRS_SHAPE;
  public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
  public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL;
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
    super(
      BlockBehaviour.Properties.of(Material.AIR)
      .isValidSpawn((state, getter, pos, entityType) -> false)
      .isRedstoneConductor((state, getter, pos) -> false)
      .isSuffocating((state, getter, pos) -> false)
      .lightLevel(LightBlock.LIGHT_EMISSION)
    );
    this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(SHAPE, StairsShape.STRAIGHT).setValue(WATERLOGGED, Boolean.valueOf(false)).setValue(LEVEL, Integer.valueOf(0)));
  }

  /**
   * Checks if the given blockState holds a block that's a Vertical Slab.
   * 
   * @param blockState
   * @return
   */
  public static boolean isVerticalSlab(BlockState blockState) {
    return blockState.getBlock() instanceof VerticalSlabBlock;
  }

  /**
   * Makes all the possible {@link VoxelShape shapes} a Vertical Slab can have.
   * 
   * @return
   */
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

  /**
   * Creates a {@link VoxelShape vertical shaped box}.
   * 
   * @param originX
   * @param originZ
   * @param x
   * @param z
   * @return
   */
  private static VoxelShape verticalBox(double originX, double originZ, double x, double z) {
    return Block.box(originX * 8, 0.0D, originZ * 8, x * 8, 16.0D, z * 8);
  }

  /**
   * Returns the appropriate {@link StairsShape} to use for this block depending on the neighbouring blocks.
   * Logic is the same used for {@link net.minecraft.world.level.block.StairBlock StairBlock}.
   * 
   * @param state - {@link BlockState} of the block of which to get the {@link StairsShape}.
   * @param getter
   * @param pos - {@link BlockPos position} of the block of which to get the {@link StairsShape}.
   * @return
   */
  private static StairsShape getStairsShape(BlockState state, BlockGetter getter, BlockPos pos) {
    Direction direction = state.getValue(FACING);
    BlockState facingBlockState = getter.getBlockState(pos.relative(direction));
    if (isVerticalSlab(facingBlockState)) {
      Direction facingBlockDirection = facingBlockState.getValue(FACING);
      if (facingBlockDirection.getAxis() != direction.getAxis() && canTakeShape(state, getter.getBlockState(pos.relative(facingBlockDirection.getOpposite())))) {
        if (facingBlockDirection == direction.getClockWise()) {
          return StairsShape.OUTER_RIGHT;
        }
        return StairsShape.OUTER_LEFT;
      }
    }

    BlockState oppositeBlockState = getter.getBlockState(pos.relative(direction.getOpposite()));
    if (isVerticalSlab(oppositeBlockState)) {
      Direction oppositeBlockDirection = oppositeBlockState.getValue(FACING);
      if (oppositeBlockDirection.getAxis() != direction.getAxis() && canTakeShape(state, getter.getBlockState(pos.relative(oppositeBlockDirection)))) {
        if (oppositeBlockDirection == direction.getClockWise()) {
          return StairsShape.INNER_RIGHT;
        }
        return StairsShape.INNER_LEFT;
      }
    }

    return StairsShape.STRAIGHT;
  }

  /**
   * Checks if the block from the first {@link BlockState} can combine its shape with the block from the second {@link BlockState}.
   * 
   * @param state
   * @param blockState
   * @return
   */
  private static boolean canTakeShape(BlockState state, BlockState blockState) {
    return !isVerticalSlab(blockState) || blockState.getValue(FACING) != state.getValue(FACING);
  }

  @Override
  public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext collisionContext) {
    return SHAPES[SHAPE_BY_STATE[this.getShapeIndex(state)]];
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
    BlockPos pos = placeContext.getClickedPos();
    Level level = placeContext.getLevel();
    BlockState blockstate = this.defaultBlockState().setValue(FACING, placeContext.getHorizontalDirection()).setValue(WATERLOGGED, Boolean.valueOf(level.getFluidState(pos).getType() == Fluids.WATER));
    BlockState referringBlockState = VerticalSlabUtils.getReferringBlockState(placeContext.getItemInHand());
    if (referringBlockState != null) {
      blockstate = blockstate.setValue(LEVEL, Integer.valueOf(referringBlockState.getLightEmission(level, pos)));
    }
    return blockstate.setValue(SHAPE, getStairsShape(blockstate, level, pos));
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
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateDefinition) {
    stateDefinition.add(FACING, SHAPE, WATERLOGGED, LEVEL);
  }

  @Override
  public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
    return false;
  }

  @Override
  public boolean canBeReplaced(BlockState state, Fluid fluid) {
    return false;
  }

  @Override
  @SuppressWarnings("deprecation")
  public float getDestroyProgress(BlockState state, Player player, BlockGetter getter, BlockPos pos) {
    BlockState referringBlockState = VerticalSlabUtils.getReferringBlockState(getter, pos);
    if (referringBlockState != null) {
      return referringBlockState.getDestroyProgress(player, getter, pos);
    }
    return super.getDestroyProgress(state, player, getter, pos);
 }

  @Override
  public ItemStack getCloneItemStack(BlockGetter getter, BlockPos pos, BlockState state) {
    VerticalSlabBlockEntity blockEntity = VerticalSlabUtils.getVerticalSlabBlockEntity(getter, pos);
    ItemStack itemStack = new ItemStack(this);
    if (blockEntity != null) {
      blockEntity.saveToItem(itemStack);
    }
    return itemStack;
  }

  @Override
  public boolean isPossibleToRespawnInThis() {
    return false;
  }

  @Override
  public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float damage) {
    BlockState referringBlockState = VerticalSlabUtils.getReferringBlockState(level, pos);
    if (referringBlockState != null) {
      referringBlockState.getBlock().fallOn(level, state, pos, entity, damage);
    } else {
      super.fallOn(level, state, pos, entity, damage);
    }
  }

  @Override
  public void updateEntityAfterFallOn(BlockGetter getter, Entity entity) {
    BlockState referringBlockState = VerticalSlabUtils.getReferringBlockState(getter, entity.getOnPos());
    if (referringBlockState != null) {
      referringBlockState.getBlock().updateEntityAfterFallOn(getter, entity);
    } else {
      super.updateEntityAfterFallOn(getter, entity);
    }
  }

  @Override
  public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
    return false;
  }

  @Override
  public float getFriction(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
    BlockState referringBlockState = VerticalSlabUtils.getReferringBlockState(level, pos);
    if (referringBlockState != null) {
      return referringBlockState.getFriction(level, pos, entity);
    }
    return super.getFriction(state, level, pos, entity);
  }

  @Override
  public boolean canHarvestBlock(BlockState state, BlockGetter getter, BlockPos pos, Player player) {
    BlockState referringBlockState = VerticalSlabUtils.getReferringBlockState(getter, pos);
    if (referringBlockState != null) {
      return referringBlockState.canHarvestBlock(getter, pos, player);
    }
    return super.canHarvestBlock(state, getter, pos, player);
  }

  @Override
  public float getExplosionResistance(BlockState state, BlockGetter getter, BlockPos pos, Explosion explosion) {
    BlockState referringBlockState = VerticalSlabUtils.getReferringBlockState(getter, pos);
    if (referringBlockState != null) {
      return referringBlockState.getExplosionResistance(getter, pos, explosion);
    }
    return super.getExplosionResistance(state, getter, pos, explosion);
  }

  @Override
  public float getEnchantPowerBonus(BlockState state, LevelReader level, BlockPos pos) {
    BlockState referringBlockState = VerticalSlabUtils.getReferringBlockState(level, pos);
    if (referringBlockState != null) {
      return referringBlockState.getEnchantPowerBonus(level, pos) / 2;
    }
    return super.getEnchantPowerBonus(state, level, pos);
  }

  @Override
  public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
    BlockState referringBlockState = VerticalSlabUtils.getReferringBlockState(level, pos);
    if (referringBlockState != null) {
      return referringBlockState.getSoundType(level, pos, entity);
    }
    return super.getSoundType(state, level, pos, entity);
  }

  @Override
  public int getFlammability(BlockState state, BlockGetter getter, BlockPos pos, Direction direction) {
    BlockState referringBlockState = VerticalSlabUtils.getReferringBlockState(getter, pos);
    if (referringBlockState != null) {
      return referringBlockState.getFlammability(getter, pos, direction);
    }
    return super.getFlammability(state, getter, pos, direction);
  }

  @Override
  public int getFireSpreadSpeed(BlockState state, BlockGetter getter, BlockPos pos, Direction direction) {
    BlockState referringBlockState = VerticalSlabUtils.getReferringBlockState(getter, pos);
    if (referringBlockState != null) {
      return referringBlockState.getFireSpreadSpeed(getter, pos, direction);
    }
    return super.getFireSpreadSpeed(state, getter, pos, direction);
  }

  @Override
  public boolean isFireSource(BlockState state, LevelReader level, BlockPos pos, Direction direction) {
    BlockState referringBlockState = VerticalSlabUtils.getReferringBlockState(level, pos);
    if (referringBlockState != null) {
      return referringBlockState.isFireSource(level, pos, direction);
    }
    return super.isFireSource(state, level, pos, direction);
  }

  @Override
  public boolean canEntityDestroy(BlockState state, BlockGetter getter, BlockPos pos, Entity entity) {
    BlockState referringBlockState = VerticalSlabUtils.getReferringBlockState(getter, pos);
    if (referringBlockState != null) {
      return referringBlockState.canEntityDestroy(getter, pos, entity);
    }
    return super.canEntityDestroy(state, getter, pos, entity);
  }

  @Override
  public boolean canDropFromExplosion(BlockState state, BlockGetter getter, BlockPos pos, Explosion explosion) {
    BlockState referringBlockState = VerticalSlabUtils.getReferringBlockState(getter, pos);
    if (referringBlockState != null) {
      return referringBlockState.canDropFromExplosion(getter, pos, explosion);
    }
    return super.canDropFromExplosion(state, getter, pos, explosion);
  }

  @Override
  public boolean collisionExtendsVertically(BlockState state, BlockGetter level, BlockPos pos, Entity collidingEntity) {
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
