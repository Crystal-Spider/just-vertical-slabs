package crystalspider.justverticalslabs.model.utils;

import java.util.List;
import java.util.Random;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;

/**
 * Utilities to handle BakedQuads.
 */
public class BakedQuadUtils {
  /**
   * Returns the {@link List} of all referred {@link BakedQuad BakedQuads} for the given {@link Direction}.
   * 
   * @param referredState - {@link BlockState} of the referred block.
   * @param side - {@link Direction side} of the block being rendered.
   * @param rand - {@link Random rand} parameter.
   * @param modelData - {@link IModelData model data} of the block being rendered.
   * @return {@link List} of all referred {@link BakedQuad BakedQuads} for the given {@link Direction}.
   */
  public static final List<BakedQuad> getReferredBakedQuads(BlockState referredState, Direction side, Random rand, IModelData modelData) {
    BakedModel referredBakedModel = ModelUtils.getReferredBakedModel(referredState);
    IModelData referredModelData = ModelUtils.getReferredModelData(referredState, modelData);
    List<BakedQuad> referredBakedQuads = referredBakedModel.getQuads(referredState, side, rand, referredModelData);
    for (BakedQuad referredBakedQuad : referredBakedModel.getQuads(referredState, null, rand, referredModelData)) {
      if (referredBakedQuad.getDirection() == side) {
        referredBakedQuads.add(referredBakedQuad);
      }
    }
    if (referredBakedQuads.size() == 0) {
      JustVerticalSlabsLoader.LOGGER.warn("Referred Block has no texture for " + side + " face. No texture will be generated for that face.");
    }
    return referredBakedQuads;
  }

  /**
   * Returns a new {@link BakedQuad} that mimics the {@code referredBakedQuad} by updating the sprite and position vertices.
   * 
   * @param jsonBakedQuad - original {@link BakedQuad}.
   * @param referredBakedQuad - mimicked {@link BakedQuad}.
   * @param orientation - orientation of the {@link BakedQuad}.
   * @return new {@link BakedQuad} with sprite and position vertices updated.
   */
  public static final BakedQuad getNewBakedQuad(BakedQuad jsonBakedQuad, BakedQuad referredBakedQuad, Direction orientation) {
    return new BakedQuad(
      VertexUtils.updateVertices(
        jsonBakedQuad.getVertices(),
        referredBakedQuad.getVertices(),
        jsonBakedQuad.getSprite(),
        referredBakedQuad.getSprite(),
        orientation == Direction.UP
      ),
      referredBakedQuad.getTintIndex(),
      orientation,
      referredBakedQuad.getSprite(),
      jsonBakedQuad.isShade()
    );
  }
}
