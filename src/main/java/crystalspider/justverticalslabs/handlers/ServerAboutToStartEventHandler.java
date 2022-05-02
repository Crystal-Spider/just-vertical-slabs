package crystalspider.justverticalslabs.handlers;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * 
 */
public class ServerAboutToStartEventHandler {
  /**
   * 
   * 
   * @param event
   */
  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void onServerAboutToStartEvent(ServerAboutToStartEvent event) {
    Map<Item, Item> slabMap = new HashMap<Item, Item>();
    ForgeRegistries.ITEMS.tags().getTag(ItemTags.SLABS).forEach(slab -> {
      event.getServer().getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING).forEach(recipe -> {
        if (recipe.getResultItem().is(slab)) {
          recipe.getIngredients().stream().filter(ingredient -> !ingredient.test(slab.getDefaultInstance())).findFirst().ifPresent(ingredient -> {
            ItemStack block = ingredient.getItems()[0];
            if (!block.is(ItemTags.SLABS)) {
              slabMap.put(slab, block.getItem());
            }
          });
        }
      });
    });
    JustVerticalSlabsLoader.slabMap = ImmutableMap.copyOf(slabMap);
    System.out.println(JustVerticalSlabsLoader.slabMap);
  }
}
