package crystalspider.justverticalslabs.handlers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import crystalspider.justverticalslabs.recipes.crafting.VerticalSlabCraftingRecipe;
import crystalspider.justverticalslabs.recipes.stonecutter.VerticalSlabStonecutterRecipe;
import crystalspider.justverticalslabs.utils.VerticalSlabUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * {@link ServerAboutToStartEvent} handler.
 */
public class ServerAboutToStartEventHandler {
  /**
   * Handles the event {@link ServerAboutToStartEvent} to load the maps of slabs-blocks.
   * Searches through all {@link Item Items} with the {@link ItemTags#SLABS slabs} tag and associates them to the block they're made from.
   * 
   * @param event - {@link ServerAboutToStartEvent}.
   */
  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void onServerAboutToStartEvent(ServerAboutToStartEvent event) {
    Map<Item, BlockState> slabStateMap = new LinkedHashMap<Item, BlockState>(), translucentMap = new LinkedHashMap<Item, BlockState>();
    Map<Item, Item> slabMap = new HashMap<Item, Item>(), blockMap = new HashMap<Item, Item>(), waxingMap = new HashMap<Item, Item>(), stonecuttingMap = new HashMap<Item, Item>();
    RecipeManager recipeManager = event.getServer().getRecipeManager();
    for (Item slab : ForgeRegistries.ITEMS.tags().getTag(ItemTags.SLABS)) {
      JustVerticalSlabsLoader.LOGGER.debug("Adding " + slab + " to " + JustVerticalSlabsLoader.MODID + " mod maps...");
      slabStateMap.put(slab, Block.byItem(slab).defaultBlockState());
      if (isTranslucent(slab.getDefaultInstance().toString())) {
        translucentMap.put(slab, Block.byItem(slab).defaultBlockState());
      }
      for (CraftingRecipe recipe : recipeManager.getAllRecipesFor(RecipeType.CRAFTING)) {
        if (recipe.getResultItem().is(slab) && !(recipe instanceof VerticalSlabCraftingRecipe)) {
          NonNullList<Ingredient> ingredients = recipe.getIngredients();
          // A slab can be connected to its block only if there exists a crafting recipe that uses at least one block and, if more, the blocks used are all the same.
          List<Ingredient> blockIngredients = getBlockIngredients(ingredients);
          if (sameIngredients(blockIngredients)) {
            blockIngredients.stream().findFirst().ifPresent(ingredient -> {
              for (ItemStack itemStack : ingredient.getItems()) {
                if (isPlain(itemStack.toString())) {
                  slabMap.put(slab, itemStack.getItem());
                }
                blockMap.putIfAbsent(itemStack.getItem(), slab);
              }
              // If no plain block is connected to this slab that means the slab is not plain either, so add the first (and theoretically only) block item.
              if (!slabMap.containsKey(slab)) {
                slabMap.put(slab, ingredient.getItems()[0].getItem());
              }
            });
          } else if (ingredients.stream().anyMatch(ingredient -> ingredient.test(Items.HONEYCOMB.getDefaultInstance()))) {
            List<Ingredient> notHoneyIngredients = ingredients.stream().filter(ingredient -> !ingredient.test(Items.HONEYCOMB.getDefaultInstance())).toList();
            if (notHoneyIngredients.size() == 1) {
              ItemStack oxidizableSlab = notHoneyIngredients.get(0).getItems()[0];
              if (oxidizableSlab.is(ItemTags.SLABS)) {
                waxingMap.put(oxidizableSlab.getItem(), slab);
              }
            }
          }
        }
      }
      for (StonecutterRecipe recipe : recipeManager.getAllRecipesFor(RecipeType.STONECUTTING)) {
        if (recipe.getResultItem().is(slab) && !(recipe instanceof VerticalSlabStonecutterRecipe)) {
          Ingredient ingredient = recipe.getIngredients().get(0);
          for (ItemStack itemStack : ingredient.getItems()) {
            Item block = itemStack.getItem();
            if (!stonecuttingMap.containsKey(block)) {
              // Put the block in the map if it's the first one that can be stonecut in the slab.
              stonecuttingMap.put(block, slab);
            } else if (slabMap.get(slab) == block) {
              // If another block was associated to the slab in the stonecuttingMap, but the slabMap has this block associated to the slab,
              // that means this block is the plain one for the slab so overwrite the block in the stonecuttingMap.
              stonecuttingMap.put(block, slab);
            }
          }
        }
      }
    }
    VerticalSlabUtils.slabStateMap = ImmutableMap.copyOf(slabStateMap);
    VerticalSlabUtils.translucentMap = ImmutableMap.copyOf(translucentMap);
    VerticalSlabUtils.slabMap = ImmutableMap.copyOf(slabMap);
    VerticalSlabUtils.blockMap = ImmutableMap.copyOf(blockMap);
    VerticalSlabUtils.stonecuttingMap = ImmutableMap.copyOf(stonecuttingMap);
    VerticalSlabUtils.waxingMap = ImmutableMap.copyOf(waxingMap);
    JustVerticalSlabsLoader.LOGGER.debug(JustVerticalSlabsLoader.MODID + " mod maps generated.");
  }

  /**
   * Checks if the given {@link CraftingRecipe} is a recipe with all ingredients holding blocks and no slab.
   * 
   * @param recipe
   * @return whether the {@code recipe} uses only items that are blocks and not slabs.
   */
  private List<Ingredient> getBlockIngredients(NonNullList<Ingredient> ingredients) {
    return ingredients.stream().filter(ingredient -> Arrays.stream(ingredient.getItems()).allMatch(itemStack -> !itemStack.is(ItemTags.SLABS) && itemStack.getItem() instanceof BlockItem)).toList();
  }

  /**
   * Checks if there is at least one ingredient and, if more, checks that all ingredients hold the same items.
   * 
   * @param ingredients
   * @return whether all ingredients match, false if no ingredient is present.
   */
  private boolean sameIngredients(List<Ingredient> ingredients) {
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

  /**
   * Checks if the given {@link ItemStack} name represents a plain block.
   * 
   * @param itemStackName
   * @return whether the given {@link ItemStack} name represents a plain block.
   */
  private boolean isPlain(String itemStackName) {
    return !(
      itemStackName.contains("chiseled") ||
      itemStackName.contains("pillar") ||
      itemStackName.contains("smooth") ||
      itemStackName.contains("cut")
    );
  }

  /**
   * Checks if the given {@link ItemStack} name represents a translucent block.
   * 
   * @param itemStackName
   * @return whether the given {@link ItemStack} name represents a translucent block.
   */
  private boolean isTranslucent(String itemStackName) {
    return (
      itemStackName.contains("spawner") ||
      itemStackName.contains("glass") ||
      itemStackName.contains("slime") ||
      itemStackName.contains("honey") ||
      itemStackName.contains("ice")
    );
  }
}
