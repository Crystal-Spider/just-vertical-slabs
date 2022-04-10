package crystalspider.justverticalslabs;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.client.model.IModelLoader;

public class VerticalSlabModelLoader implements IModelLoader<VerticalSlabModel> {
  public static final ResourceLocation VERTICAL_SLAB_LOADER = new ResourceLocation(JustVerticalSlabsLoader.MODID, "vertical_slab_loader");

  @Override
  public void onResourceManagerReload(ResourceManager manager) {

  }

  @Override
  public VerticalSlabModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
    return new VerticalSlabModel(deserializationContext.deserialize(modelContents, BlockModel.class));
  }
}
