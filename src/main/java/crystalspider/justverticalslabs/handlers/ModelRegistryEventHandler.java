package crystalspider.justverticalslabs.handlers;

import crystalspider.justverticalslabs.model.VerticalSlabModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * {@link ModelRegistryEvent} Handler.
 */
public class ModelRegistryEventHandler {
  /**
   * Handles the event {@link ModelRegistryEvent} to load the custom model loader {@link VerticalSlabModelLoader}.
   * 
   * @param event - {@link ModelRegistryEvent}.
   */
  @SubscribeEvent
  public void onModelRegistryEvent(ModelRegistryEvent event) {
    ModelLoaderRegistry.registerLoader(VerticalSlabModelLoader.RESOURCE_LOCATION, new VerticalSlabModelLoader());
  }
}
