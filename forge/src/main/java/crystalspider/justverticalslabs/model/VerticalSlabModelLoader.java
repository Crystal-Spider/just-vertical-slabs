package crystalspider.justverticalslabs.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import crystalspider.justverticalslabs.utils.VerticalSlabUtils;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

/**
 * Vertical Slab custom Model Loader.
 */
public class VerticalSlabModelLoader implements IModelLoader<VerticalSlabModel> {
  /**
   * {@link ResourceLocation} of this custom Model Loader used to uniquely identify it.
   */
  public static final ResourceLocation RESOURCE_LOCATION = VerticalSlabUtils.getResourceLocation(JustVerticalSlabsLoader.VERTICAL_SLAB_ID + "_loader");

  /**
   * Returns a new {@link VerticalSlabModel} based on the "model" property read from the JSON contents.
   * 
   * @param deserializationContext - {@link JsonDeserializationContext}.
   * @param modelContents - {@link JsonObject} containing the data read from the JSON model. 
   * @return new {@link VerticalSlabModel} based on the JSON "model" property.
   */
  @Override
  public VerticalSlabModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
    JustVerticalSlabsLoader.LOGGER.trace("Loading VerticalSlabModel...");
    return new VerticalSlabModel(deserializationContext.deserialize(modelContents.getAsJsonObject("model"), BlockModel.class));
  }

  @Override
  public void onResourceManagerReload(ResourceManager resourceManager) {}
}
