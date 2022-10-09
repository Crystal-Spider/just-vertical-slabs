package crystalspider.justverticalslabs.model.utils;

import javax.annotation.Nonnull;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

/**
 * Utilities to handle Models.
 */
public class ModelUtils {
  /**
   * Returns the {@link BakedModel} of the given {@link BlockState}.
   * 
   * @param referredState - {@link BlockState} from which retrieve the {@link BakedModel}.
   * @return the {@link BakedModel} of the given {@link BlockState}.
   */
  public static final BakedModel getReferredBakedModel(BlockState referredState) {
    return Minecraft.getInstance().getBlockRenderer().getBlockModel(referredState);
  }

  /**
   * Returns the {@link ModelData model data} of the given {@link BlockState}, or the default model data provided if no model data could be retrieved from the given {@link BlockState}.
   * 
   * @param blockState - {@link BlockState} from which retrieve the {@link ModelData model data}.
   * @param defaultData - {@link ModelData default model data} to return if no model data can be retrieved from the given {@link BlockState}.
   * @return {@link ModelData model data} of the given {@link BlockState} or the {@link ModelData default model data}.
   */
  @SuppressWarnings("null")
  public static final @Nonnull ModelData getReferredModelData(BlockState blockState, @Nonnull ModelData defaultData) {
    if (blockState.hasBlockEntity()) {
      try {
        return ((EntityBlock) blockState.getBlock()).newBlockEntity(new BlockPos(0, 0, 0), blockState).getModelData();
      } catch (Exception e) {
        JustVerticalSlabsLoader.LOGGER.warn("Referred block + {" + blockState + "} needs a BlockEntity but returned null when trying to create one, default ModelData will be used.");
        return defaultData;
      }
    }
    return defaultData;
  }
}
