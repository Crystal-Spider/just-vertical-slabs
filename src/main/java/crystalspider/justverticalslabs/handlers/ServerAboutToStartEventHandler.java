package crystalspider.justverticalslabs.handlers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import crystalspider.justverticalslabs.recipes.crafting.VerticalSlabCraftingRecipe;
import crystalspider.justverticalslabs.recipes.stonecutter.VerticalSlabStonecutterRecipe;
import crystalspider.justverticalslabs.utils.VerticalSlabUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * {@link ServerAboutToStartEvent} handler.
 */
public class ServerAboutToStartEventHandler {
  /**
   * Handles the event {@link ServerAboutToStartEvent} to load the map of slabs-blocks.
   * Searches through all {@link Item Items} with the {@link ItemTags#SLABS slabs} tag and associates them to the block they're made from.
   * 
   * @param event - {@link ServerAboutToStartEvent}.
   */
  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void onServerAboutToStartEvent(ServerAboutToStartEvent event) {
    Map<Item, Item> slabMap = new LinkedHashMap<Item, Item>(), blockMap = new HashMap<Item, Item>(), stonecuttingMap = new HashMap<Item, Item>();
    RecipeManager recipeManager = event.getServer().getRecipeManager();
    for (Item slab : ForgeRegistries.ITEMS.tags().getTag(ItemTags.SLABS)) {
      for (CraftingRecipe recipe : recipeManager.getAllRecipesFor(RecipeType.CRAFTING)) {
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        if (recipe.getResultItem().is(slab) && !(recipe instanceof VerticalSlabCraftingRecipe) && isRecipeWithBlocks(ingredients) && sameIngredients(ingredients)) {
          ingredients.stream().findFirst().ifPresent(ingredient -> {
            for (ItemStack itemStack : ingredient.getItems()) {
              if (isPlain(itemStack)) {
                slabMap.put(slab, itemStack.getItem());
              }
              blockMap.put(itemStack.getItem(), slab);
            }
            if (!slabMap.containsKey(slab)) {
              slabMap.put(slab, ingredient.getItems()[0].getItem());
            }
          });
        }
      }
      for (StonecutterRecipe recipe : recipeManager.getAllRecipesFor(RecipeType.STONECUTTING)) {
        if (recipe.getResultItem().is(slab) && !(recipe instanceof VerticalSlabStonecutterRecipe)) {
          Ingredient ingredient = recipe.getIngredients().get(0);
          for (ItemStack itemStack : ingredient.getItems()) {
            Item block = itemStack.getItem();
            if (!stonecuttingMap.containsKey(block)) {
              stonecuttingMap.put(block, slab);
            } else if (slabMap.get(slab) == block) {
              stonecuttingMap.put(block, slab);
            }
          }
        }
      }
    }
    VerticalSlabUtils.slabMap = ImmutableMap.copyOf(slabMap);
    VerticalSlabUtils.blockMap = ImmutableMap.copyOf(blockMap);
    VerticalSlabUtils.stonecuttingMap = ImmutableMap.copyOf(stonecuttingMap);
  }

  /**
   * Checks if the given {@link CraftingRecipe} is a recipe with all ingredients holding blocks and no slab.
   * 
   * @param recipe
   * @return whether the {@code recipe} uses only items that are blocks and not slabs.
   */
  private boolean isRecipeWithBlocks(NonNullList<Ingredient> ingredients) {
    return ingredients.stream().allMatch(ingredient -> Arrays.stream(ingredient.getItems()).allMatch(itemStack -> !itemStack.is(ItemTags.SLABS) && itemStack.getItem() instanceof BlockItem));
  }

  /**
   * Checks if there is at least one ingredient and, if more, checks that all ingredients hold the same items.
   * 
   * @param ingredients
   * @return whether all ingredients match, false if no ingredient is present.
   */
  private boolean sameIngredients(NonNullList<Ingredient> ingredients) {
    if (ingredients.size() > 0) {
      boolean same = true;
      Ingredient firstIngredient = ingredients.get(0);
      for (int i = 1; i < ingredients.size() && same; i++) {
        ItemStack[] blocks = ingredients.get(i).getItems();
        for (int j = 0; j < blocks.length && same; j++) {
          same = firstIngredient.test(blocks[j]);
        }
      }
      return same;
    }
    return false;
  }

  private boolean isPlain(ItemStack itemStack) {
    return !(itemStack.toString().contains("chiseled") || itemStack.toString().contains("pillar"));
  }
}
