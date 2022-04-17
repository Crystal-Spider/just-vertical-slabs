package crystalspider.justverticalslabs.models;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.client.model.IModelLoader;

public class VerticalSlabModelLoader implements IModelLoader<VerticalSlabModel> {
  public static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(JustVerticalSlabsLoader.MODID, "vertical_slab_loader");

  @Override
  public VerticalSlabModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
    return new VerticalSlabModel(deserializationContext.deserialize(modelContents.getAsJsonObject("model"), BlockModel.class));
  }

  @Override
  public void onResourceManagerReload(ResourceManager p_10758_) {
    // TODO Auto-generated method stub
  }
}
