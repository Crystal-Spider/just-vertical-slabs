package crystalspider.justverticalslabs.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import crystalspider.justverticalslabs.blocks.VerticalSlabBlock;
import crystalspider.justverticalslabs.blocks.VerticalSlabBlockEntity;
import crystalspider.justverticalslabs.model.item.VerticalSlabItemOverrides;
import crystalspider.justverticalslabs.model.perpective.VerticalSlabPerspectiveTransformer;
import crystalspider.justverticalslabs.model.utils.BakedQuadUtils;
import crystalspider.justverticalslabs.model.utils.ModelUtils;
import crystalspider.justverticalslabs.model.utils.VertexUtils;
import crystalspider.justverticalslabs.utils.VerticalSlabUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.data.ModelData;

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
   * {@link BakedQuad} cache. Needed because {@link #getQuads(BlockState, Direction, Random, ModelData)} is called several times every little interval, thus needs to be as fast as possible.
   */
  private final HashMap<VerticalSlabModelKey, List<BakedQuad>> bakedQuadsCache = new HashMap<VerticalSlabModelKey, List<BakedQuad>>();

  /**
   * @param jsonBakedModel
   */
  public VerticalSlabBakedModel(BakedModel jsonBakedModel) {
    this.jsonBakedModel = jsonBakedModel;
    this.overrides = new VerticalSlabItemOverrides();
    JustVerticalSlabsLoader.LOGGER.trace("Baked VerticalSlabModel.");
  }

  /**
   * See {@link https://mcforge.readthedocs.io/en/1.19.x/rendering/modelloaders/bakedmodel/#isgui3d}.
   */
  @Override
  public boolean isGui3d() {
    return false;
  }

  /**
   * See {@link https://mcforge.readthedocs.io/en/1.19.x/rendering/modelloaders/bakedmodel/#iscustomrenderer}.
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
   * Handles item model transformations for different {@link TransformType}.
   * Which {@link Transformation} to use is decided by {@link VerticalSlabPerspectiveTransformer#getTransformation(TransformType)}.
   * 
   * @param transformType - {@link TransformType}.
   * @param poseStack - {@link PoseStack} to render.
   * @param applyLeftHandTransform
   * @return this model.
   */
  @Override
  public BakedModel applyTransform(TransformType transformType, PoseStack poseStack, boolean applyLeftHandTransform) {
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
   * Returns the {@link TextureAtlasSprite particle icon} of this model based on the given {@link ModelData data}.
   * In details, uses the given {@link ModelData data} to search for the {@link VerticalSlabBlockEntity#REFERRED_SLAB_STATE referredSlabState} property.
   * If such property is not null, returns the {@link BakedModel#getParticleIcon(ModelData)} of the {@link #getReferredBakedModel(BlockState) referred BakedModel},
   * otherwise returns the {@link #getParticleIcon() default particle icon} of this model.
   * 
   * @param extraData - {@link ModelData} to use to render.
   * @return {@link TextureAtlasSprite particle icon} based on the given {@link ModelData data}.
   */
  @Override
  public TextureAtlasSprite getParticleIcon(ModelData extraData) {
    BlockState referredSlabState = extraData.get(VerticalSlabUtils.REFERRED_SLAB_STATE);
    if (referredSlabState != null) {
      return ModelUtils.getReferredBakedModel(referredSlabState).getParticleIcon(ModelUtils.getReferredModelData(referredSlabState, extraData));
    }
    return getParticleIcon();
  }

  /**
   * Returns the {@link List} of {@link BakedQuad} to use to render the model based on {@link Direction side} and {@link ModelData extraData}.
   * Caches as much as possible to speed up computational time of subsequent calls.
   * {@link ModelData extraData} should contain a {@link VerticalSlabBlockEntity#REFERRED_SLAB_STATE referredSlabState} property in order to render this model correctly.
   * If such condition is not met, an {@link Collections#emptyList() empty list} is returned and thus nothing will be rendered.
   * 
   * @param state - {@link BlockState} of the block being rendered, null if an item is being rendered.
   * @param side - indicates to which culling {@link Direction face} the {@link BakedQuad baked quads} are associated to.
   *               If null, no culling {@link Direction face} is associated and the {@link BakedQuad baked quads} will always be rendered.
   * @param rand - {@link RandomSource} instance.
   * @param extraData - {@link ModelData} to use to render.
   * @param renderType - {@link RenderType}.
   * @return {@link List} of {@link BakedQuad} based on parameters.
   */
  @Nonnull
  @Override
  @SuppressWarnings("null")
  public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData extraData, @Nullable RenderType renderType) {
    BlockState referredSlabState = extraData.get(VerticalSlabUtils.REFERRED_SLAB_STATE);
    if (referredSlabState != null) {
      boolean isDouble = state != null && state.getValue(VerticalSlabBlock.DOUBLE);
      VerticalSlabModelKey verticalSlabModelKey = new VerticalSlabModelKey(side, referredSlabState, isDouble);
      if (!bakedQuadsCache.containsKey(verticalSlabModelKey)) {
        if (isDouble) {
          bakedQuadsCache.put(verticalSlabModelKey, BakedQuadUtils.getReferredBakedQuads(referredSlabState.setValue(SlabBlock.TYPE, SlabType.DOUBLE), side, rand, extraData, renderType));
        } else {
          BlockState referredBlockState = VerticalSlabUtils.getReferredBlockState(referredSlabState);
          boolean referringBlock = referredBlockState != null && VerticalSlabUtils.isTranslucent(referredSlabState);
          List<BakedQuad> bakedQuads = new ArrayList<BakedQuad>();
          for (BakedQuad jsonBakedQuad : jsonBakedModel.getQuads(state, side, rand, extraData, renderType)) {
            Direction orientation = jsonBakedQuad.getDirection();
            for (BakedQuad referredBakedQuad : BakedQuadUtils.getReferredBakedQuads(referringBlock ? referredBlockState : referredSlabState, orientation, rand, extraData, renderType)) {
              if (!VertexUtils.isInternalFace(referredBakedQuad.getVertices(), referringBlock)) {
                bakedQuads.add(BakedQuadUtils.getNewBakedQuad(jsonBakedQuad, referredBakedQuad, orientation));
              }
            }
          }
          bakedQuadsCache.put(verticalSlabModelKey, bakedQuads);
        }
      }
      return bakedQuadsCache.get(verticalSlabModelKey);
    }
    return jsonBakedModel.getQuads(state, side, rand, extraData, renderType);
  }
}
