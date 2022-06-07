package crystalspider.justverticalslabs.handlers;

import crystalspider.justverticalslabs.utils.VerticalSlabUtils.MapsManager;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * {@link RecipesUpdatedEvent} handler.
 */
public class RecipesUpdateEventHandler {
  /**
   * If maps are not computed yet sets {@link MapsManager#fallbackRecipeManager}, otherwise adds Vertical Slabs to the search tree.
   * 
   * @param event
   */
  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void onRecipesUpdatedEvent(RecipesUpdatedEvent event) {
    if (MapsManager.slabStateMap == null) {
      // If maps were not computed yet here it means there is no dedicated server and it's needed to computed them for the client.
      MapsManager.setFallbackRecipeManager(event.getRecipeManager());
    } else {
      MapsManager.addToSearchTree();
    }
  }
}
