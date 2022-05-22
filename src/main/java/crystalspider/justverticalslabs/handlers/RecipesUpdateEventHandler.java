package crystalspider.justverticalslabs.handlers;

import crystalspider.justverticalslabs.utils.VerticalSlabUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.searchtree.MutableSearchTree;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * {@link RecipesUpdatedEvent} handler.
 */
public class RecipesUpdateEventHandler {
  /**
   * Handles the event {@link RecipesUpdatedEvent} to add each existing Vertical Slab to the search tree.
   * 
   * @param event
   */
  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void onRecipesUpdatedEvent(RecipesUpdatedEvent event) {
    MutableSearchTree<ItemStack> creativeSearchTree = Minecraft.getInstance().getSearchTree(SearchRegistry.CREATIVE_NAMES);
    for(BlockState referredSlabState : VerticalSlabUtils.slabStateMap.values()) {
      creativeSearchTree.add(VerticalSlabUtils.getVerticalSlabItem(referredSlabState, VerticalSlabUtils.isTranslucent(referredSlabState)));
    }
    creativeSearchTree.refresh();
  }
}
