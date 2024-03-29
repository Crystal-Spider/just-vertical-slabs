package crystalspider.justverticalslabs.blocks;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import crystalspider.justverticalslabs.blocks.state.PosFunctionBi;
import crystalspider.justverticalslabs.blocks.state.PosFunctionMono;
import crystalspider.justverticalslabs.utils.VerticalSlabUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
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
import net.minecraft.world.level.block.SlabBlock;
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
import net.minecraft.world.level.block.state.properties.SlabType;
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
 */
public abstract class VerticalSlabBlock extends Block implements SimpleWaterloggedBlock, EntityBlock {
  /**
   * {@link BlockState} Property indicating the Facing Direction.
   */
  public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
  /**
   * {@link BlockState} Property indicating the Shape.
   */
  public static final EnumProperty<StairsShape> SHAPE = BlockStateProperties.STAIRS_SHAPE;
  /**
   * {@link BlockState} Property indicating whether it's waterlogged.
   */
  public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
  /**
   * {@link BlockState} Property indicating the light level.
   */
  public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL;
  /**
   * {@link BlockState} Property indicating whether the referred Block/Slab uses light occlusion.
   */
  public static final BooleanProperty OCCLUSION = BooleanProperty.create("occlusion");
  /**
   * {@link BlockState} Property indicating whether it's double (full block).
   */
  public static final BooleanProperty DOUBLE = BooleanProperty.create("double");

  /**
   * @param material
   */
  public VerticalSlabBlock(Material material) {
    super(
      BlockBehaviour.Properties.of(material)
      .isValidSpawn(Behaviour::isValidSpawn)
      .isRedstoneConductor(Behaviour::isRedstoneConductor)
      .isSuffocating(Behaviour::isSuffocating)
      .lightLevel(LightBlock.LIGHT_EMISSION)
      .dynamicShape()
    );
    this.registerDefaultState(
      this.defaultBlockState()
        .setValue(FACING, Direction.NORTH)
        .setValue(SHAPE, StairsShape.STRAIGHT)
        .setValue(WATERLOGGED, false)
        .setValue(LEVEL, 0)
        .setValue(OCCLUSION, false)
        .setValue(DOUBLE, false)
    );
  }

  /**
   * Checks if the given blockState holds a block that's a Vertical Slab.
   * 
   * @param blockState
   * @return value of the check.
   */
  public static boolean isVerticalSlab(BlockState blockState) {
    return blockState.getBlock() instanceof VerticalSlabBlock;
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateDefinition) {
    stateDefinition.add(FACING, SHAPE, WATERLOGGED, LEVEL, OCCLUSION, DOUBLE);
  }

  @Override
  public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext collisionContext) {
    return Behaviour.getShape(state, getter, pos, collisionContext);
  }

  @Override
  @SuppressWarnings("deprecation")
  public VoxelShape getCollisionShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext collisionContext) {
    if (state.getValue(DOUBLE)) {
      BlockState referredBlockState = VerticalSlabUtils.getReferredBlockState(getter, pos);
      if (referredBlockState != null) {
        return referredBlockState.getCollisionShape(getter, pos, collisionContext);
      }
    }
    return super.getCollisionShape(state, getter, pos, collisionContext);
  }

  @Override
  public BlockState updateShape(BlockState state, Direction direction, BlockState blockState, LevelAccessor accessor, BlockPos pos, BlockPos blockPos) {
    if (state.getValue(WATERLOGGED)) {
      accessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(accessor));
    }
    return state.setValue(SHAPE, Behaviour.getStairsShape(state, accessor, pos));
  }

  @Override
  public boolean useShapeForLightOcclusion(BlockState state) {
    return state.getValue(LEVEL) == 0;
  }

  @Override
  public boolean placeLiquid(LevelAccessor accessor, BlockPos pos, BlockState state, FluidState fluid) {
    return state.getValue(DOUBLE) ? false : SimpleWaterloggedBlock.super.placeLiquid(accessor, pos, state, fluid);
  }

  @Override
  public BlockState rotate(BlockState state, Rotation rotation) {
    return state.getValue(DOUBLE) ? state : state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
  }

  @Override
  @SuppressWarnings("deprecation")
  public BlockState mirror(BlockState state, Mirror mirror) {
    if (!state.getValue(DOUBLE)) {
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
    }
    return super.mirror(state, mirror);
  }

  @Override
  @SuppressWarnings("deprecation")
  public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
    BlockPos pos = placeContext.getClickedPos();
    Level level = placeContext.getLevel();
    BlockState referredSlabState = VerticalSlabUtils.getReferredSlabState(placeContext.getItemInHand());
    if (referredSlabState == VerticalSlabUtils.getReferredSlabState(level, pos)) {
      BlockState blockstate = this.defaultBlockState().setValue(DOUBLE, true);
      if (referredSlabState != null) {
        BlockState referredBlockState = VerticalSlabUtils.getReferredBlockState(referredSlabState);
        BlockState referredState = referredBlockState != null ? referredBlockState : referredSlabState;
        blockstate = blockstate
          .setValue(LEVEL, Behaviour.getReferredProperty(referredState::getLightEmission, referredState::getLightEmission, level, pos))
          .setValue(OCCLUSION, referredState.useShapeForLightOcclusion());
      }
      return blockstate;
    } else {
      BlockState blockstate = this.defaultBlockState().setValue(FACING, placeContext.getHorizontalDirection()).setValue(WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER);
      if (referredSlabState != null) {
        BlockState referredBlockState = VerticalSlabUtils.getReferredBlockState(referredSlabState);
        blockstate = blockstate
          .setValue(LEVEL, Behaviour.getReferredProperty(referredSlabState::getLightEmission, referredSlabState::getLightEmission, level, pos))
          .setValue(OCCLUSION, (referredBlockState != null ? referredBlockState : referredSlabState).useShapeForLightOcclusion());
      }
      return blockstate.setValue(SHAPE, Behaviour.getStairsShape(blockstate, level, pos));
    }
  }

  @Override
  @SuppressWarnings("deprecation")
  public FluidState getFluidState(BlockState state) {
    return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
  }

  @Override
  @SuppressWarnings("deprecation")
  public float getDestroyProgress(BlockState state, Player player, BlockGetter getter, BlockPos pos) {
    BlockState referredSlabState = VerticalSlabUtils.getReferredSlabState(getter, pos);
    if (referredSlabState != null) {
      if (state.getValue(DOUBLE)) {
        referredSlabState = referredSlabState.setValue(SlabBlock.TYPE, SlabType.DOUBLE);
      }
      float result = referredSlabState.getDestroyProgress(player, getter, pos);
      return Behaviour.getReferredProperty((BlockGetter blockGetter, BlockPos blockPos, Player entity) -> result, () -> super.getDestroyProgress(state, player, getter, pos), getter, pos, player);
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
  public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float damage) {
    BlockState referredSlabState = VerticalSlabUtils.getReferredSlabState(level, pos);
    BlockState referredBlockState = VerticalSlabUtils.getReferredBlockState(referredSlabState);
    try {
      if (referredBlockState != null) {
        referredBlockState.getBlock().fallOn(level, state, pos, entity, damage);
      } else {
        if (referredSlabState != null) {
          referredSlabState.getBlock().fallOn(level, state, pos, entity, damage);
        } else {
          super.fallOn(level, state, pos, entity, damage);
        }
      }
    } catch (Exception e) {
      Behaviour.logDataWarning(e, pos);
      super.fallOn(level, state, pos, entity, damage);
    }
  }

  @Override
  public void updateEntityAfterFallOn(BlockGetter getter, Entity entity) {
    BlockPos pos = entity.getOnPos();
    BlockState referredSlabState = VerticalSlabUtils.getReferredSlabState(getter, pos);
    BlockState referredBlockState = VerticalSlabUtils.getReferredBlockState(referredSlabState);
    try {
      if (referredBlockState != null) {
        referredBlockState.getBlock().updateEntityAfterFallOn(getter, entity);
      } else {
        if (referredSlabState != null) {
          referredSlabState.getBlock().updateEntityAfterFallOn(getter, entity);
        } else {
          super.updateEntityAfterFallOn(getter, entity);
        }
      }
    } catch (Exception e) {
      Behaviour.logDataWarning(e, pos);
      super.updateEntityAfterFallOn(getter, entity);
    }
  }

  @Override
  public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
    BlockState referredSlabState = VerticalSlabUtils.getReferredSlabState(level, pos);
    if (referredSlabState != null) {
      BlockState referredBlockState = VerticalSlabUtils.getReferredBlockState(referredSlabState);
      try {
        if (referredBlockState != null) {
          referredBlockState.entityInside(level, pos, entity);
        } else {
          referredSlabState.entityInside(level, pos, entity);
        }
      } catch (Exception e) {
        Behaviour.logDataWarning(e, pos);
      }
    } 
  }

  @Override
  public float getFriction(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
    BlockState referredSlabState = VerticalSlabUtils.getReferredSlabState(level, pos);
    BlockState referredBlockState = VerticalSlabUtils.getReferredBlockState(referredSlabState);
    if (referredSlabState != null) {
      if (referredBlockState != null) {
        return Behaviour.getReferredProperty(referredBlockState::getFriction, () -> super.getFriction(state, level, pos, entity), level, pos, entity);
      }
      return Behaviour.getReferredProperty(referredSlabState::getFriction, () -> super.getFriction(state, level, pos, entity), level, pos, entity);
    }
    return super.getFriction(state, level, pos, entity);
  }

  @Override
  public float getExplosionResistance(BlockState state, BlockGetter getter, BlockPos pos, Explosion explosion) {
    BlockState referredSlabState = VerticalSlabUtils.getReferredSlabState(getter, pos);
    if (referredSlabState != null) {
      if (state.getValue(DOUBLE)) {
        referredSlabState = referredSlabState.setValue(SlabBlock.TYPE, SlabType.DOUBLE);
      }
      return Behaviour.getReferredProperty(referredSlabState::getExplosionResistance, () -> super.getExplosionResistance(state, getter, pos, explosion), getter, pos, explosion);
    }
    return super.getExplosionResistance(state, getter, pos, explosion);
  }

  @Override
  public float getEnchantPowerBonus(BlockState state, LevelReader level, BlockPos pos) {
    BlockState referredSlabState = VerticalSlabUtils.getReferredSlabState(level, pos);
    if (referredSlabState != null) {
      BlockState referredBlockState = VerticalSlabUtils.getReferredBlockState(referredSlabState);
      if (referredBlockState != null) {
        return Behaviour.getReferredProperty(referredBlockState::getEnchantPowerBonus, () -> 0F, level, pos) / (state.getValue(DOUBLE) ? 1 : 2);
      }
      if (state.getValue(DOUBLE)) {
        referredSlabState = referredSlabState.setValue(SlabBlock.TYPE, SlabType.DOUBLE);
      }
      return Behaviour.getReferredProperty(referredSlabState::getEnchantPowerBonus, () -> 0F, level, pos);
    }
    return super.getEnchantPowerBonus(state, level, pos);
  }

  @Override
  public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
    BlockState referredSlabState = VerticalSlabUtils.getReferredSlabState(level, pos);
    if (referredSlabState != null) {
      if (state.getValue(DOUBLE)) {
        referredSlabState = referredSlabState.setValue(SlabBlock.TYPE, SlabType.DOUBLE);
      }
      return Behaviour.getReferredProperty(referredSlabState::getSoundType, referredSlabState::getSoundType, level, pos, entity);
    }
    return super.getSoundType(state, level, pos, entity);
  }

  @Override
  public int getFlammability(BlockState state, BlockGetter getter, BlockPos pos, Direction direction) {
    BlockState referredSlabState = VerticalSlabUtils.getReferredSlabState(getter, pos);
    if (referredSlabState != null) {
      if (state.getValue(DOUBLE)) {
        referredSlabState = referredSlabState.setValue(SlabBlock.TYPE, SlabType.DOUBLE);
      }
      return Behaviour.getReferredProperty(referredSlabState::getFlammability, () -> super.getFlammability(state, getter, pos, direction), getter, pos, direction);
    }
    return super.getFlammability(state, getter, pos, direction);
  }

  @Override
  public int getFireSpreadSpeed(BlockState state, BlockGetter getter, BlockPos pos, Direction direction) {
    BlockState referredSlabState = VerticalSlabUtils.getReferredSlabState(getter, pos);
    if (referredSlabState != null) {
      if (state.getValue(DOUBLE)) {
        referredSlabState = referredSlabState.setValue(SlabBlock.TYPE, SlabType.DOUBLE);
      }
      return Behaviour.getReferredProperty(referredSlabState::getFireSpreadSpeed, () -> super.getFireSpreadSpeed(state, getter, pos, direction), getter, pos, direction);
    }
    return super.getFireSpreadSpeed(state, getter, pos, direction);
  }

  @Override
  public boolean isFireSource(BlockState state, LevelReader level, BlockPos pos, Direction direction) {
    BlockState referredSlabState = VerticalSlabUtils.getReferredSlabState(level, pos);
    if (referredSlabState != null) {
      if (state.getValue(DOUBLE)) {
        referredSlabState = referredSlabState.setValue(SlabBlock.TYPE, SlabType.DOUBLE);
      }
      return Behaviour.getReferredProperty(referredSlabState::isFireSource, () -> super.isFireSource(state, level, pos, direction), level, pos, direction);
    }
    return super.isFireSource(state, level, pos, direction);
  }

  @Override
  public boolean isValidSpawn(BlockState state, BlockGetter getter, BlockPos pos, SpawnPlacements.Type type, EntityType<?> entityType) {
    return Behaviour.isValidSpawn(state, getter, pos, type, entityType);
  }

  @Override
  public boolean shouldCheckWeakPower(BlockState state, LevelReader level, BlockPos pos, Direction side) {
    return Behaviour.shouldCheckWeakPower(state, level, pos, side);
  }

  @Override
  public boolean canEntityDestroy(BlockState state, BlockGetter getter, BlockPos pos, Entity entity) {
    BlockState referredSlabState = VerticalSlabUtils.getReferredSlabState(getter, pos);
    if (referredSlabState != null) {
      if (state.getValue(DOUBLE)) {
        referredSlabState = referredSlabState.setValue(SlabBlock.TYPE, SlabType.DOUBLE);
      }
      return Behaviour.getReferredProperty(referredSlabState::canEntityDestroy, () -> super.canEntityDestroy(state, getter, pos, entity), getter, pos, entity);
    }
    return super.canEntityDestroy(state, getter, pos, entity);
  }

  @Override
  public boolean canDropFromExplosion(BlockState state, BlockGetter getter, BlockPos pos, Explosion explosion) {
    BlockState referredSlabState = VerticalSlabUtils.getReferredSlabState(getter, pos);
    if (referredSlabState != null) {
      if (state.getValue(DOUBLE)) {
        referredSlabState = referredSlabState.setValue(SlabBlock.TYPE, SlabType.DOUBLE);
      }
      return Behaviour.getReferredProperty(referredSlabState::canDropFromExplosion, () -> true, getter, pos, explosion);
    }
    return super.canDropFromExplosion(state, getter, pos, explosion);
  }

  @Override
  public boolean canHarvestBlock(BlockState state, BlockGetter getter, BlockPos pos, Player player) {
    BlockState referredSlabState = VerticalSlabUtils.getReferredSlabState(getter, pos);
    if (referredSlabState != null) {
      if (state.getValue(DOUBLE)) {
        referredSlabState = referredSlabState.setValue(SlabBlock.TYPE, SlabType.DOUBLE);
      }
      return Behaviour.getReferredProperty(referredSlabState::canHarvestBlock, () -> super.canHarvestBlock(state, getter, pos, player), getter, pos, player);
    }
    return super.canHarvestBlock(state, getter, pos, player);
  }

  @Override
  public boolean isPossibleToRespawnInThis() {
    return false;
  }

  @Override
  public boolean isPathfindable(BlockState state, BlockGetter getter, BlockPos pos, PathComputationType computationType) {
    return false;
  }

  @Override
  public boolean canPlaceLiquid(BlockGetter getter, BlockPos pos, BlockState state, Fluid fluid) {
    return state.getValue(DOUBLE) ? false : SimpleWaterloggedBlock.super.canPlaceLiquid(getter, pos, state, fluid);
  }

  @Override
  public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
    BlockState blockState = VerticalSlabUtils.getReferredSlabState(context.getItemInHand());
    if (!state.getValue(DOUBLE) && state.getValue(SHAPE) == StairsShape.STRAIGHT && blockState != null && blockState == VerticalSlabUtils.getReferredSlabState(context.getLevel(), context.getClickedPos())) {
      if (context.replacingClickedOnBlock()) {
        return context.getClickedFace() == state.getValue(FACING).getOpposite();
      }
      return true;
    }
    return false;
  }

  @Override
  public boolean canBeReplaced(BlockState state, Fluid fluid) {
    return false;
  }

  @Override
  public boolean canSustainPlant(BlockState state, BlockGetter getter, BlockPos pos, Direction facing, IPlantable plantable) {
    if (state.getValue(DOUBLE)) {
      BlockState referredSlabState = VerticalSlabUtils.getReferredSlabState(getter, pos);
      if (referredSlabState != null) {
        BlockState referredBlockState = VerticalSlabUtils.getReferredBlockState(referredSlabState);
        if (referredBlockState != null) {
          return Behaviour.getReferredProperty(referredBlockState::canSustainPlant, () -> false, getter, pos, facing, plantable);
        }
        return Behaviour.getReferredProperty(referredSlabState.setValue(SlabBlock.TYPE, SlabType.DOUBLE)::canSustainPlant, () -> false, getter, pos, facing, plantable);
      }
    }
    return false;
  }

  @Override
  public boolean collisionExtendsVertically(BlockState state, BlockGetter level, BlockPos pos, Entity collidingEntity) {
    return false;
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new VerticalSlabBlockEntity(pos, state);
  }

  /**
   * Helper for Vertical Slab Block behaviours.
   */
  protected static class Behaviour {
    /**
     * Array of all shapes a single Vertical Slab can have.
     */
    private static final VoxelShape[] VERTICAL_SHAPES = Behaviour.makeVerticalShapes();
    /**
     * Array of all shapes a double Vertical Slab can have.
     */
    private static final VoxelShape[] FULL_SHAPES = Behaviour.makeFullShapes();
    /**
     * Hardcoded index values for {@link #VERTICAL_SHAPES Vertical Slab shapes}.
     */
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

    /**
     * Returns the desired Block Property using {@code posSensitiveFunction} and {@code fallbackFunction} on a best-effort base.
     * 
     * @param <T> extraParameter of a position sensitive function.
     * @param <R> type of the desired property.
     * @param <G> {@link BlockGetter} subclass used to retrieve position sensitive data.
     * @param posSensitiveFunction {@link PosFunctionMono} to use when possible.
     * @param fallbackFunction {@link Supplier} for the property in case position sensitive data can't be retrieved.
     * @param getter {@link BlockGetter} subclass instance used to retrieve position sensitive data.
     * @param pos {@link BlockPos position} of the block.
     * @return the most accurate possible desired Block Property.
     */
    protected static final <R, G extends BlockGetter> R getReferredProperty(BiFunction<G, BlockPos, R> posSensitiveFunction, Supplier<R> fallbackFunction, G getter, BlockPos pos) {
      try {
        return posSensitiveFunction.apply(getter, pos);
      } catch (Exception e) {
        logDataWarning(e, pos);
        return fallbackFunction.get();
      }
    }
    
    /**
     * Returns the desired Block Property using {@code posSensitiveFunction} and {@code fallbackFunction} on a best-effort base.
     * 
     * @param <T> extraParameter of a position sensitive function.
     * @param <R> type of the desired property.
     * @param <G> {@link BlockGetter} subclass used to retrieve position sensitive data.
     * @param posSensitiveFunction {@link PosFunctionMono} to use when possible.
     * @param fallbackFunction {@link Supplier} for the property in case position sensitive data can't be retrieved.
     * @param getter {@link BlockGetter} subclass instance used to retrieve position sensitive data.
     * @param pos {@link BlockPos position} of the block.
     * @param extraParameter extra parameter used to retrieved data.
     * @return the most accurate possible desired Block Property.
     */
    protected static final <T, R, G extends BlockGetter> R getReferredProperty(PosFunctionMono<G, T, R> posSensitiveFunction, Supplier<R> fallbackFunction, G getter, BlockPos pos, T extraParameter) {
      try {
        return posSensitiveFunction.apply(getter, pos, extraParameter);
      } catch (Exception e) {
        logDataWarning(e, pos);
        return fallbackFunction.get();
      }
    }

    /**
     * Returns the desired Block Property using {@code posSensitiveFunction} and {@code fallbackFunction} on a best-effort base.
     * 
     * @param <T1> extraParameter1 of a position sensitive function.
     * @param <T2> extraParameter2 of a position sensitive function.
     * @param <R> type of the desired property.
     * @param <G> {@link BlockGetter} subclass used to retrieve position sensitive data.
     * @param posSensitiveFunction {@link PosFunctionBi} to use when possible.
     * @param fallbackFunction {@link Supplier} for the property in case position sensitive data can't be retrieved.
     * @param getter {@link BlockGetter} subclass instance used to retrieve position sensitive data.
     * @param pos {@link BlockPos position} of the block.
     * @param extraParameter1 extra parameter used to retrieved data.
     * @param extraParameter2 extra parameter used to retrieved data.
     * @return the most accurate possible desired Block Property.
     */
    protected static final <T1, T2, R, G extends BlockGetter> R getReferredProperty(PosFunctionBi<G, T1, T2, R> posSensitiveFunction, Supplier<R> fallbackFunction, G getter, BlockPos pos, T1 extraParameter1, T2 extraParameter2) {
      try {
        return posSensitiveFunction.apply(getter, pos, extraParameter1, extraParameter2);
      } catch (Exception e) {
        logDataWarning(e, pos);
        return fallbackFunction.get();
      }
    }
    
    /**
     * Logs a warning with the given {@link Exception} for the Vertical Slab in the give {@link BlockPos position}.
     * 
     * @param e - {@link Exception} that prevented to retrieve position sensitive data.
     * @param position - {@link BlockPos} of the block for which position sensitive data was needed.
     */
    protected static void logDataWarning(Exception e, BlockPos pos) {
      JustVerticalSlabsLoader.LOGGER.warn("Position sensitive data for Vertical Slab in position " + formatPosition(pos) + " could not be retrieved from referred BlockState as an Exception was thrown:", e);
      JustVerticalSlabsLoader.LOGGER.debug("Switching to NON position sensitive data.");
    }
    
    /**
     * Formats the given {@link BlockPos} to a string ready to be logged.
     * 
     * @param position
     * @return formatted position.
     */
    protected static String formatPosition(BlockPos position) {
      return "[" + position.getX() + ", " + position.getY() + ", " + position.getZ() + "]";
    }
    
    /**
     * Makes all the possible vertical {@link VoxelShape shapes} a Vertical Slab can have.
     * 
     * @return {@link VoxelShape}[]
     */
    private static VoxelShape[] makeVerticalShapes() {
      VoxelShape[] shapes = new VoxelShape[12 * 16];
      for (double h = 16; h > 8; h--) {
        VoxelShape facingSouthShape = Behaviour.verticalBox(0, 1, 2, 2, h);
        VoxelShape facingWestShape = Behaviour.verticalBox(0, 0, 1, 2, h);
        VoxelShape facingNorthShape = Behaviour.verticalBox(0, 0, 2, 1, h);
        VoxelShape facingEastShape = Behaviour.verticalBox(1, 0, 2, 2, h);
    
        VoxelShape innerLeftBottomShape = Shapes.or(facingSouthShape, facingWestShape);
        VoxelShape innerLeftTopShape = Shapes.or(facingNorthShape, facingWestShape);
        VoxelShape innerRightBottomShape = Shapes.or(facingSouthShape, facingEastShape);
        VoxelShape innerRightTopShape = Shapes.or(facingNorthShape, facingEastShape);
    
        VoxelShape outerLeftBottomShape = Behaviour.verticalBox(0, 1, 1, 2, h);
        VoxelShape outerLeftTopShape = Behaviour.verticalBox(0, 0, 1, 1, h);
        VoxelShape outerRightBottomShape = Behaviour.verticalBox(1, 1, 2, 2, h);
        VoxelShape outerRightTopShape = Behaviour.verticalBox(1, 0, 2, 1, h);
    
        int index = (int) (16 - h) * 12;
        shapes[index] = facingSouthShape;
        shapes[index + 1] = facingWestShape;
        shapes[index + 2] = facingNorthShape;
        shapes[index + 3] = facingEastShape;
        shapes[index + 4] = innerLeftBottomShape;
        shapes[index + 5] = innerLeftTopShape;
        shapes[index + 6] = innerRightBottomShape;
        shapes[index + 7] = innerRightTopShape;
        shapes[index + 8] = outerLeftBottomShape;
        shapes[index + 9] = outerLeftTopShape;
        shapes[index + 10] = outerRightBottomShape;
        shapes[index + 11] = outerRightTopShape;
      }
      return shapes;
    }
    
    /**
     * Makes all the possible full block {@link VoxelShape shapes} a Double Vertical Slab can have.
     * 
     * @return {@link VoxelShape}[]
     */
    private static VoxelShape[] makeFullShapes() {
      VoxelShape[] shapes = new VoxelShape[8];
      for (double h = 8; h > 0; h--) {
        shapes[(int)(8 - h)] = Block.box(0, 0, 0, 16, 8 + h, 16);
      }
      return shapes;
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
    private static VoxelShape verticalBox(double originX, double originZ, double x, double z, double height) {
      return Block.box(originX * 8, 0, originZ * 8, x * 8, height, z * 8);
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
      Direction direction = state.getValue(VerticalSlabBlock.FACING);
      BlockState facingBlockState = getter.getBlockState(pos.relative(direction));
      if (VerticalSlabBlock.isVerticalSlab(facingBlockState) && !facingBlockState.getValue(VerticalSlabBlock.DOUBLE)) {
        Direction facingBlockDirection = facingBlockState.getValue(VerticalSlabBlock.FACING);
        if (facingBlockDirection.getAxis() != direction.getAxis() && canTakeShape(state, getter.getBlockState(pos.relative(facingBlockDirection.getOpposite())))) {
          if (facingBlockDirection == direction.getClockWise()) {
            return StairsShape.OUTER_RIGHT;
          }
          return StairsShape.OUTER_LEFT;
        }
      }
    
      BlockState oppositeBlockState = getter.getBlockState(pos.relative(direction.getOpposite()));
      if (VerticalSlabBlock.isVerticalSlab(oppositeBlockState) && !oppositeBlockState.getValue(VerticalSlabBlock.DOUBLE)) {
        Direction oppositeBlockDirection = oppositeBlockState.getValue(VerticalSlabBlock.FACING);
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
      return !VerticalSlabBlock.isVerticalSlab(blockState) || blockState.getValue(VerticalSlabBlock.FACING) != state.getValue(VerticalSlabBlock.FACING) || blockState.getValue(VerticalSlabBlock.DOUBLE);
    }

    /**
     * Returns the correct shape for a Vertical Slab.
     * 
     * @param state
     * @param getter
     * @param pos
     * @param collisionContext
     * @return the correct shape for a Vertical Slab.
     */
    private static VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext collisionContext) {
      return state.getValue(DOUBLE) ? FULL_SHAPES[getHeightDiff(getter, pos, collisionContext)] : VERTICAL_SHAPES[getShapeIndex(state, getter, pos, collisionContext)];
    }

    /**
   * Returns the height difference between a full height block and the referred Block/Slab.
   * Best effort based.
   * 
   * @param getter
   * @param pos
   * @param collisionContext
   * @return height difference between full height and referred block/slab.
   */
    private static int getHeightDiff(BlockGetter getter, BlockPos pos, CollisionContext collisionContext) {
      int heightDiff = getHeightDiff(VerticalSlabUtils.getReferredBlockState(getter, pos), getter, pos, collisionContext, 1);
      if (heightDiff == 0 || heightDiff >= 8) {
        return getHeightDiff(VerticalSlabUtils.getReferredSlabState(getter, pos), getter, pos, collisionContext, 0.5);
      }
      return heightDiff;
    }

    /**
     * Returns the height difference between a full height block and the referred BlockState.
     * Best effort based.
     * 
     * @param referredState
     * @param getter
     * @param pos
     * @param collisionContext
     * @param maxHeight
     * @return height difference between full height and referred block/slab by the referred BlockState.
     */
    private static int getHeightDiff(BlockState referredState, BlockGetter getter, BlockPos pos, CollisionContext collisionContext, double maxHeight) {
      if (referredState != null) {
        VoxelShape referredShape = getReferredProperty(referredState::getShape, () -> Shapes.empty(), getter, pos, collisionContext);
        if (!referredShape.isEmpty()) {
          return (int) Math.round((maxHeight - referredShape.bounds().getYsize()) * 16);
        }
      }
      return 0;
    }

    /**
     * Returns the index to use to get the {@link VoxelShape shape}.
     * 
     * @param state
     * @param getter
     * @param pos
     * @param collisionContext
     * @return index to use to get the {@link VoxelShape shape}.
     */
    private static int getShapeIndex(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext collisionContext) {
      return SHAPE_BY_STATE[state.getValue(SHAPE).ordinal() * 4 + state.getValue(FACING).get2DDataValue()] + 12 * getHeightDiff(getter, pos, collisionContext);
    }

    /**
     * Checks whether a Vertical Slab should be a valid spawn for the given {@link EntityType} depending on the referredBlock/referredSlab.
     * <p>
     * {@link SpawnPlacements.Type Type} sensitive.
     * 
     * @param state
     * @param getter
     * @param pos
     * @param type
     * @param entityType
     * @return whether a Vertical Slab should be a valid spawn for the given {@link EntityType}.
     */
    private static boolean isValidSpawn(BlockState state, BlockGetter getter, BlockPos pos, SpawnPlacements.Type type, EntityType<?> entityType) {
      if (state.getValue(DOUBLE)) {
        BlockState referredSlabState = VerticalSlabUtils.getReferredSlabState(getter, pos);
        if (referredSlabState != null) {
          BlockState referredBlockState = VerticalSlabUtils.getReferredBlockState(referredSlabState);
          if (getter instanceof LevelReader) {
            if (referredBlockState != null) {
              return getReferredProperty(referredBlockState::isValidSpawn, () -> false, (LevelReader) getter, pos, type, entityType);
            }
            return getReferredProperty(referredSlabState.setValue(SlabBlock.TYPE, SlabType.DOUBLE)::isValidSpawn, () -> false, (LevelReader) getter, pos, type, entityType);
          }
          return isValidSpawn(referredBlockState, referredSlabState, getter, pos, entityType);
        }
      }
      return false;
    }

    /**
     * Checks whether a Vertical Slab should be a valid spawn for the given {@link EntityType} depending on the referredBlock/referredSlab.
     * 
     * @param state
     * @param getter
     * @param pos
     * @param entityType
     * @return whether a Vertical Slab should be a valid spawn for the given {@link EntityType}.
     */
    private static boolean isValidSpawn(BlockState state, BlockGetter getter, BlockPos pos, EntityType<?> entityType) {
      if (state.getValue(DOUBLE)) {
        BlockState referredSlabState = VerticalSlabUtils.getReferredSlabState(getter, pos);
        if (referredSlabState != null) {
          return isValidSpawn(VerticalSlabUtils.getReferredBlockState(referredSlabState), referredSlabState, getter, pos, entityType);
        }
      }
      return false;
    }

    /**
     * Base logic to retrieve the check for a valid spawn. 
     * 
     * @param referredBlockState
     * @param referredSlabState
     * @param getter
     * @param pos
     * @param entityType
     * @return result of the check.
     */
    private static boolean isValidSpawn(BlockState referredBlockState, BlockState referredSlabState, BlockGetter getter, BlockPos pos, EntityType<?> entityType) {
      if (referredBlockState != null) {
        return getReferredProperty(referredBlockState::isValidSpawn, () -> false, getter, pos, entityType);
      }
      return getReferredProperty(referredSlabState.setValue(SlabBlock.TYPE, SlabType.DOUBLE)::isValidSpawn, () -> false, getter, pos, entityType);
    }

    /**
     * Checks whether a Vertical Slab should act as a redstone conductor depending on the referredBlock/referredSlab.
     * <p>
     * Side sensitive.
     * 
     * @param state
     * @param level
     * @param pos
     * @param side
     * @return whether a Vertical Slab should act as a redstone conductor.
     */
    private static boolean shouldCheckWeakPower(BlockState state, LevelReader level, BlockPos pos, Direction side) {
      if (state.getValue(DOUBLE)) {
        BlockState referredSlabState = VerticalSlabUtils.getReferredSlabState(level, pos);
        if (referredSlabState != null) {
          BlockState referredBlockState = VerticalSlabUtils.getReferredBlockState(referredSlabState);
          if (referredBlockState != null) {
            return getReferredProperty(referredBlockState::shouldCheckWeakPower, () -> false, level, pos, side);
          }
          return getReferredProperty(referredSlabState.setValue(SlabBlock.TYPE, SlabType.DOUBLE)::shouldCheckWeakPower, () -> false, level, pos, side);
        }
      }
      return false;
    }

    /**
     * Checks whether a Vertical Slab should act as a redstone conductor depending on the referredBlock/referredSlab.
     * 
     * @param state
     * @param level
     * @param pos
     * @return whether a Vertical Slab should act as a redstone conductor.
     */
    private static boolean isRedstoneConductor(BlockState state, BlockGetter getter, BlockPos pos) {
      if (state.getValue(DOUBLE)) {
        BlockState referredSlabState = VerticalSlabUtils.getReferredSlabState(getter, pos);
        if (referredSlabState != null) {
          BlockState referredBlockState = VerticalSlabUtils.getReferredBlockState(referredSlabState);
          if (referredBlockState != null) {
            return getReferredProperty(referredBlockState::isRedstoneConductor, () -> false, getter, pos);
          }
          return getReferredProperty(referredSlabState.setValue(SlabBlock.TYPE, SlabType.DOUBLE)::isRedstoneConductor, () -> false, getter, pos);
        }
      }
      return false;
    }

    /**
     * Checks whether a Vertical Slab should be suffocating depending on the referredBlock/referredSlab.
     * 
     * @param state
     * @param getter
     * @param pos
     * @return whether a Vertical Slab is suffocating.
     */
    private static boolean isSuffocating(BlockState state, BlockGetter getter, BlockPos pos) {
      if (state.getValue(DOUBLE)) {
        BlockState referredSlabState = VerticalSlabUtils.getReferredSlabState(getter, pos);
        if (referredSlabState != null) {
          BlockState referredBlockState = VerticalSlabUtils.getReferredBlockState(referredSlabState);
          if (referredBlockState != null) {
            return getReferredProperty(referredBlockState::isSuffocating, () -> false, getter, pos);
          }
          return getReferredProperty(referredSlabState.setValue(SlabBlock.TYPE, SlabType.DOUBLE)::isSuffocating, () -> false, getter, pos);
        }
      }
      return false;
    }
  }
}
