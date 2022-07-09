package crystalspider.justverticalslabs.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

import com.mojang.datafixers.util.Pair;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.geometry.IModelGeometry;

/**
 * Unbaked Vertical Slab Model.
 */
public class VerticalSlabModel implements IModelGeometry<VerticalSlabModel> {
  /**
   * JSON {@link BlockModel}.
   */
  private BlockModel jsonBlockModel;

  public VerticalSlabModel(BlockModel jsonBlockModel) {
    this.jsonBlockModel = jsonBlockModel;
    JustVerticalSlabsLoader.LOGGER.trace("Loaded VerticalSlabModel.");
  }

  /**
   * Returns a new instance of {@link VerticalSlabBakedModel} ready for rendering.
   * 
   * @param owner
   * @param bakery
   * @param spriteGetter
   * @param modelTransform
   * @param overrides
   * @param modelLocation
   * @return new instance of {@link VerticalSlabBakedModel}.
   */
  @Override
  public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
    JustVerticalSlabsLoader.LOGGER.trace("Baking VerticalSlabModel...");
    return new VerticalSlabBakedModel(jsonBlockModel.bake(bakery, jsonBlockModel, spriteGetter, modelTransform, modelLocation, false));
  }

  /**
   * {@link Collection} of {@link Material Materials} this model depends on.
   * 
   * @param owner
   * @param modelGetter
   * @param missingTextureErrors
   * @return {@link Collections#emptyList()}.
   */
  @Override
  public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
    return Collections.emptyList();
  }
}
