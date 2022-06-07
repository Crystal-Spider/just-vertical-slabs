package crystalspider.justverticalslabs.handlers;

import crystalspider.justverticalslabs.utils.VerticalSlabUtils.MapsManager;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * {@link ServerAboutToStartEvent} handler.
 */
public class ServerAboutToStartEventHandler {
  /**
   * Handles the event {@link ServerAboutToStartEvent} to compute the maps.
   * Compute maps for the server, either dedicated or multiplayer.
   * 
   * @param event - {@link ServerAboutToStartEvent}.
   */
  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void onServerAboutToStartEvent(ServerAboutToStartEvent event) {
    MapsManager.computeMaps(event.getServer().getRecipeManager());
  }
}
