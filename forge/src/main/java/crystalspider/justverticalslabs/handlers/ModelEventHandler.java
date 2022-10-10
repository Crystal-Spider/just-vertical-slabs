package crystalspider.justverticalslabs.handlers;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import crystalspider.justverticalslabs.model.VerticalSlabModelLoader;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * {@link ModelEvent} Handler.
 */
public class ModelEventHandler {
  /**
   * Handles the event {@link ModelEvent} to load the custom model loader {@link VerticalSlabModelLoader}.
   * 
   * @param event - {@link ModelEvent}.
   */
  @SubscribeEvent
  public void onModelEvent(ModelEvent.RegisterGeometryLoaders event) {
    event.register(JustVerticalSlabsLoader.VERTICAL_SLAB_ID + "_loader", new VerticalSlabModelLoader());
  }
}
