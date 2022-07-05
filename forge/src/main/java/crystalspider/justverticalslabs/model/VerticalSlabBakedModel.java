package crystalspider.justverticalslabs.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import crystalspider.justverticalslabs.blocks.VerticalSlabBlock;
import crystalspider.justverticalslabs.blocks.VerticalSlabBlockEntity;
import crystalspider.justverticalslabs.model.item.VerticalSlabItemOverrides;
import crystalspider.justverticalslabs.model.perpective.VerticalSlabPerspectiveTransformer;
import crystalspider.justverticalslabs.utils.VerticalSlabUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;

/**
 * Vertical Slab Baked Model ready for rendering.
 */
public class VerticalSlabBakedModel implements IDynamicBakedModel {
  /**
   * Original {@link BakedModel} read from JSON.
   */
  private final BakedModel jsonBakedModel;
  /**
   * {@link ItemOverrides} to use to render items.
   */
  private final ItemOverrides overrides;
  /**
   * {@link BakedQuad} cache. Needed because {@link #getQuads(BlockState, Direction, Random, IModelData)} is called several times every little interval, thus needs to be as fast as possible.
   */
  private final HashMap<VerticalSlabModelKey, List<BakedQuad>> bakedQuadsCache = new HashMap<VerticalSlabModelKey, List<BakedQuad>>();

  public VerticalSlabBakedModel(BakedModel jsonBakedModel) {
    this.jsonBakedModel = jsonBakedModel;
    this.overrides = new VerticalSlabItemOverrides();
    JustVerticalSlabsLoader.LOGGER.trace("Baked VerticalSlabModel.");
  }

  /**
   * See {@link https://mcforge.readthedocs.io/en/1.18.x/rendering/modelloaders/bakedmodel/#isgui3d}.
   */
  @Override
  public boolean isGui3d() {
    return false;
  }

  /**
   * See {@link https://mcforge.readthedocs.io/en/1.18.x/rendering/modelloaders/bakedmodel/#iscustomrenderer}.
   */
  @Override
  public boolean isCustomRenderer() {
    return false;
  }

  /**
   * Whether to enable lighting in GUI when rendered as an item.
   */
  @Override
  public boolean usesBlockLight() {
    return true;
  }

  /**
   * Whether to render the block using ambient occlusion.
   */
  @Override
  public boolean useAmbientOcclusion() {
    return true;
  }

  /**
   * {@link ItemOverrides} to use to render items.
   */
  @Override
  public ItemOverrides getOverrides() {
    return overrides;
  }

  /**
   * Whether {@link #handlePerspective(TransformType, PoseStack)} will be used.
   */
  @Override
  public boolean doesHandlePerspectives() {
    return true;
  }

  /**
   * Handles item model transformations for different {@link TransformType}.
   * Which {@link Transformation} to use is decided by {@link VerticalSlabPerspectiveTransformer#getTransformation(TransformType)}.
   * 
   * @param transformType - {@link TransformType}.
   * @param poseStack - {@link PoseStack} to render.
   * @return this model.
   */
  @Override
  public BakedModel handlePerspective(TransformType transformType, PoseStack poseStack) {
    Transformation transformation = VerticalSlabPerspectiveTransformer.getTransformation(transformType);
    if (!transformation.isIdentity()) {
      transformation.push(poseStack);
    }
    return this;
  }

  /**
   * Returns the default {@link TextureAtlasSprite particle icon} of this model.
   * 
   * @return default {@link TextureAtlasSprite particle icon}.
   */
  @Override
  @SuppressWarnings("deprecation")
  public TextureAtlasSprite getParticleIcon() {
    return Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(MissingTextureAtlasSprite.getLocation());
  }

  /**
   * Returns the {@link TextureAtlasSprite particle icon} of this model based on the given {@link IModelData data}.
   * In details, uses the given {@link IModelData data} to search for the {@link VerticalSlabBlockEntity#REFERRED_SLAB_STATE referredSlabState} property.
   * If such property is not null, returns the {@link BakedModel#getParticleIcon(IModelData)} of the {@link #getReferredBakedModel(BlockState) referred BakedModel},
   * otherwise returns the {@link #getParticleIcon() default particle icon} of this model.
   * 
   * @param extraData - {@link IModelData} to use to render.
   * @return {@link TextureAtlasSprite particle icon} based on the given {@link IModelData data}.
   */
  @Override
  public TextureAtlasSprite getParticleIcon(IModelData extraData) {
    BlockState referredSlabState = extraData.getData(VerticalSlabUtils.REFERRED_SLAB_STATE);
    if (referredSlabState != null) {
      return getReferredBakedModel(referredSlabState).getParticleIcon(getReferredModelData(referredSlabState, extraData));
    }
    return getParticleIcon();
  }

  /**
   * Returns the {@link List} of {@link BakedQuad} to use to render the model based on {@link Direction side} and {@link IModelData modelData}.
   * Caches as much as possible to speed up computational time of subsequent calls.
   * {@link IModelData modelData} should contain a {@link VerticalSlabBlockEntity#REFERRED_SLAB_STATE referredSlabState} property in order to render this model correctly.
   * If such condition is not met, an {@link Collections#emptyList() empty list} is returned and thus nothing will be rendered.
   * 
   * @param state - {@link BlockState} of the block being rendered, null if an item is being rendered.
   * @param side - indicates to which culling {@link Direction face} the {@link BakedQuad baked quads} are associated to.
   *               If null, no culling {@link Direction face} is associated and the {@link BakedQuad baked quads} will always be rendered.
   * @param rand - {@link Random} instance.
   * @param modelData - {@link IModelData} to use to render.
   * @return {@link List} of {@link BakedQuad} based on parameters.
   */
  @Override
  public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData modelData) {
    BlockState referredSlabState = modelData.getData(VerticalSlabUtils.REFERRED_SLAB_STATE);
    if (referredSlabState != null) {
      boolean isDouble = state != null && state.getValue(VerticalSlabBlock.DOUBLE);
      VerticalSlabModelKey verticalSlabModelKey = new VerticalSlabModelKey(side, referredSlabState, isDouble);
      if (!bakedQuadsCache.containsKey(verticalSlabModelKey)) {
        if (isDouble) {
          bakedQuadsCache.put(verticalSlabModelKey, getReferredBakedQuads(referredSlabState.setValue(SlabBlock.TYPE, SlabType.DOUBLE), side, rand, modelData));
        } else {
          BlockState referredBlockState = VerticalSlabUtils.getReferredBlockState(referredSlabState);
          List<BakedQuad> bakedQuads = new ArrayList<BakedQuad>();
          for (BakedQuad jsonBakedQuad : jsonBakedModel.getQuads(state, side, rand, modelData)) {
            Direction orientation = jsonBakedQuad.getDirection();
            for (BakedQuad referredBakedQuad : getReferredBakedQuads(referredBlockState != null && VerticalSlabUtils.isTranslucent(referredSlabState) ? referredBlockState : referredSlabState, orientation, rand, modelData)) {
              bakedQuads.add(getNewBakedQuad(jsonBakedQuad, referredBakedQuad.getSprite(), referredBakedQuad.getVertices(), referredBakedQuad.getTintIndex(), orientation));
            }
          }
          bakedQuadsCache.put(verticalSlabModelKey, bakedQuads);
        }
      }
      return bakedQuadsCache.get(verticalSlabModelKey);
    }
    return jsonBakedModel.getQuads(state, side, rand, modelData);
  }

  /**
   * Returns the {@link BakedModel} of the given {@link BlockState}.
   * 
   * @param referredState - {@link BlockState} from which retrieve the {@link BakedModel}.
   * @return the {@link BakedModel} of the given {@link BlockState}.
   */
  private BakedModel getReferredBakedModel(BlockState referredState) {
    return Minecraft.getInstance().getBlockRenderer().getBlockModel(referredState);
  }

  /**
   * Returns the {@link IModelData model data} of the given {@link BlockState}, or the default model data provided if no model data could be retrieved from the given {@link BlockState}.
   * 
   * @param blockState - {@link BlockState} from which retrieve the {@link IModelData model data}.
   * @param defaultData - {@link IModelData default model data} to return if no model data can be retrieved from the given {@link BlockState}.
   * @return {@link IModelData model data} of the given {@link BlockState} or the {@link IModelData default model data}.
   */
  private IModelData getReferredModelData(BlockState blockState, IModelData defaultData) {
    return blockState.hasBlockEntity() ? ((EntityBlock) blockState.getBlock()).newBlockEntity(new BlockPos(0, 0, 0), blockState).getModelData() : defaultData;
  }

  /**
   * Returns the {@link List} of all referred {@link BakedQuad BakedQuads} for the given {@link Direction}.
   * 
   * @param referredState - {@link BlockState} of the referred block.
   * @param side - {@link Direction side} of the block being rendered.
   * @param rand - {@link Random rand} parameter.
   * @param modelData - {@link IModelData model data} of the block being rendered.
   * @return {@link List} of all referred {@link BakedQuad BakedQuads} for the given {@link Direction}.
   */
  private List<BakedQuad> getReferredBakedQuads(BlockState referredState, Direction side, Random rand, IModelData modelData) {
    BakedModel referredBakedModel = getReferredBakedModel(referredState);
    IModelData referredModelData = getReferredModelData(referredState, modelData);
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
   * Returns a new {@link BakedQuad} that uses the given sprite and orientation instead of its default ones.
   * 
   * @param jsonBakedQuad - original {@link BakedQuad}.
   * @param referredSprite - new {@link TextureAtlasSprite sprite}.
   * @param orientation - face this {@link BakedQuad} is associated to (not the culling face).
   * @return new {@link BakedQuad} using the given sprite and orientation.
   */
  private BakedQuad getNewBakedQuad(BakedQuad jsonBakedQuad, TextureAtlasSprite referredSprite, int[] referredVertices, int tintIndex, Direction orientation) {
    return new BakedQuad(updateVertices(jsonBakedQuad.getVertices(), referredVertices, jsonBakedQuad.getSprite(), referredSprite, orientation == Direction.UP), tintIndex, orientation, referredSprite, jsonBakedQuad.isShade());
  }

  /**
   * Returns the updated vertices.
   * Updates UV vertex elements to use the new sprite instead of the old one.
   * Updates the Y Position vertex element to make the new sprite adhere to the correct shape.
   * 
   * @param vertices
   * @param oldSprite
   * @param newSprite
   * @return updated vertices.
   */
  private int[] updateVertices(int[] vertices, int[] referredVertices, TextureAtlasSprite oldSprite, TextureAtlasSprite newSprite, boolean faceUp) {
    int[] updatedVertices = vertices.clone();
    for (int vertexIndex = 0; vertexIndex < DefaultVertexFormat.BLOCK.getVertexSize(); vertexIndex += DefaultVertexFormat.BLOCK.getIntegerSize()) {
      float y = Float.intBitsToFloat(referredVertices[vertexIndex + 1]);
      // Lower only top face since RenderType CutoutMipped will remove extra transparent texture bits that go out of the shape.
      if (faceUp && y > 0 && y < 1 && y != 0.5) {
        updatedVertices[vertexIndex + 1] = Float.floatToRawIntBits(y < 0.5 ? y + 0.5F : y);
      }
      updatedVertices[vertexIndex + 4] = changeUVertexSprite(oldSprite, newSprite, updatedVertices[vertexIndex + 4]);
      updatedVertices[vertexIndex + 5] = changeVVertexSprite(oldSprite, newSprite, updatedVertices[vertexIndex + 5]);
    }
    return updatedVertices;
  }

  /**
   * Changes the U Vertex value to reference the new sprite.
   * Uses the same formula as {@link net.minecraft.client.renderer.block.model.FaceBakery#fillVertex(int[], int, Vector3f, TextureAtlasSprite, BlockFaceUV) FaceBakery.fillVertex(int[], int, Vector3f, TextureAtlasSprite, BlockFaceUV)} (index = i + 4),
   * but gets the double parameter from {@link #getUV(float, float, float)}.
   * 
   * @param oldSprite
   * @param newSprite
   * @param vertex
   * @return updated U Vertex.
   */
  private int changeUVertexSprite(TextureAtlasSprite oldSprite, TextureAtlasSprite newSprite, int vertex) {
    return Float.floatToRawIntBits(newSprite.getU(getUV(Float.intBitsToFloat(vertex), oldSprite.getU0(), oldSprite.getU1())));
  }

  /**
   * Changes the V Vertex value to reference the new sprite.
   * Uses the same formula as {@link net.minecraft.client.renderer.block.model.FaceBakery#fillVertex(int[], int, Vector3f, TextureAtlasSprite, BlockFaceUV) FaceBakery.fillVertex(int[], int, Vector3f, TextureAtlasSprite, BlockFaceUV)} (index = i + 5),
   * but gets the double parameter from {@link #getUV(float, float, float)}.
   * 
   * @param oldSprite
   * @param newSprite
   * @param vertex
   * @return updated V Vertex.
   */
  private int changeVVertexSprite(TextureAtlasSprite oldSprite, TextureAtlasSprite newSprite, int vertex) {
    return Float.floatToRawIntBits(newSprite.getV(getUV(Float.intBitsToFloat(vertex), oldSprite.getV0(), oldSprite.getV1())));
  }

  /**
   * Inverse formula for {@link TextureAtlasSprite#getU(double)} and {@link TextureAtlasSprite#getV(double)}.
   * Given the internal values of the sprite, returns the parameter needed to get the given result for either of the above functions.
   * 
   * @param uv - {@link TextureAtlasSprite#getU(double)} or {@link TextureAtlasSprite#getV(double)} result.
   * @param uv0 - {@link TextureAtlasSprite#u0} or {@link TextureAtlasSprite#v0}.
   * @param uv1 - {@link TextureAtlasSprite#u1} or {@link TextureAtlasSprite#v1}.
   * @return parameter to get the given result with either {@link TextureAtlasSprite#getU(double)} or {@link TextureAtlasSprite#getV(double)}.
   */
  private double getUV(float uv, float uv0, float uv1) {
    return (double) (uv - uv0) * 16.0F / (uv1 - uv0);
  }
}