package crystalspider.justverticalslabs.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

/**
 * Vertical Slab custom Model Loader.
 */
public class VerticalSlabModelLoader implements IGeometryLoader<VerticalSlabModel> {
  /**
   * Returns a new {@link VerticalSlabModel} based on the "model" property read from the JSON contents.
   * 
   * @param deserializationContext - {@link JsonDeserializationContext}.
   * @param jsonObject - {@link JsonObject} containing the data read from the JSON model. 
   * @return new {@link VerticalSlabModel} based on the JSON "model" property.
   */
  @Override
  public VerticalSlabModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
    JustVerticalSlabsLoader.LOGGER.trace("Loading VerticalSlabModel...");
    return new VerticalSlabModel(deserializationContext.deserialize(jsonObject.getAsJsonObject("model"), BlockModel.class));
  }
}
