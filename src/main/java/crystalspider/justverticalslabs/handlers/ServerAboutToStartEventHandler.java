package crystalspider.justverticalslabs.handlers;

import crystalspider.justverticalslabs.utils.MapsInstantiator;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * {@link ServerAboutToStartEvent} handler.
 */
public class ServerAboutToStartEventHandler {
  /**
   * Handles the event {@link ServerAboutToStartEvent} to load the maps of slabs-blocks.
   * 
   * @param event - {@link ServerAboutToStartEvent}.
   */
  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void onServerAboutToStartEvent(ServerAboutToStartEvent event) {
    MapsInstantiator.instantiateMaps(event.getServer().getRecipeManager());
  }
}
