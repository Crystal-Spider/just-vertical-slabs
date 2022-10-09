package crystalspider.justverticalslabs.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import crystalspider.justverticalslabs.JustVerticalSlabsLoader;
import crystalspider.justverticalslabs.blocks.VerticalSlabBlockEntity;
import crystalspider.justverticalslabs.items.VerticalSlabBlockItem;
import crystalspider.justverticalslabs.recipes.crafting.VerticalSlabCraftingRecipe;
import crystalspider.justverticalslabs.recipes.stonecutter.VerticalSlabStonecutterRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.searchtree.RefreshableSearchTree;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Utility class for common operations regarding Vertical Slabs.
 */
public class VerticalSlabUtils {
  /**
   * ID to use when creating a new {@link CompoundTag} to store a Vertical Slab referred Slab {@link BlockState}.
   */
  public static final String NBT_ID = "referredSlabState";

  /**
   * {@link BlockState} {@link ModelProperty} to use when building and reading {@link ModelData} of a Vertical Slab.
   */
  public static final ModelProperty<BlockState> REFERRED_SLAB_STATE = new ModelProperty<BlockState>();

  /**
   * Builds and returns a new {@link ModelData} with {@link #REFERRED_SLAB_STATE referredSlabState property}
   * holding the value of the given {@code referredSlabState}.
   * 
   * @param referredSlabState
   * @return Vertical Slab {@link ModelData}.
   */
  @Nonnull
  @SuppressWarnings("null")
  public static final ModelData buildModelData(BlockState referredSlabState) {
    return new ModelDataMap.Builder().withInitial(REFERRED_SLAB_STATE, referredSlabState).build();
  }

  /**
   * Returns a new {@link ResourceLocation} to uniquely identify a resource of this mod.
   * 
   * @param id - unique ID for the resource.
   * @return {@link ResourceLocation}.
   */
  public static final ResourceLocation getResourceLocation(String id) {
    return new ResourceLocation(JustVerticalSlabsLoader.MODID, id);
  }

  /**
   * Checks whether the given {@link BlockState referredSlabState} represents a translucent Slab.
   * 
   * @param referredSlabState
   * @return check value.
   */
  public static final boolean isTranslucent(BlockState referredSlabState) {
    return isTranslucent(referredSlabState.getBlock().asItem());
  }

  /**
   * Checks whether the given {@link Item slab} represents a translucent Slab.
   * 
   * @param slab
   * @return check value.
   */
  @SuppressWarnings("null")
  public static final boolean isTranslucent(Item slab) {
    return MapsManager.translucentMap.containsKey(slab);
  }

  /**
   * Returns the Vertical Slab {@link ItemStack} default instance.
   * 
   * @return Vertical Slab {@link ItemStack} default instance.
   */
  public static final ItemStack getDefaultInstance() {
    return getVerticalSlabItem(Blocks.OAK_SLAB.defaultBlockState(), false);
  }

  /**
   * Returns a Vertical Slab {@link ItemStack} with the specified {@code referredSlabState}.
   * 
   * @param referredSlabState
   * @param translucent - whether a Translucent or Cutout VerticalSlabBlock is to be used to generate the {@link ItemStack}.
   * @return Vertical Slab {@link ItemStack}.
   */
  public static final ItemStack getVerticalSlabItem(BlockState referredSlabState, boolean translucent) {
    return translucent ? getItemStackWithState(JustVerticalSlabsLoader.TRANSLUCENT_VERTICAL_SLAB_BLOCK.get(), referredSlabState) : getItemStackWithState(JustVerticalSlabsLoader.CUTOUT_VERTICAL_SLAB_BLOCK.get(), referredSlabState);
  }

  /**
   * Returns a {@link ItemStack} with the specified {@code referredSlabState}.
   * 
   * @param itemLike - {@link ItemLike} instance to use to get a basic {@link ItemStack}.
   * @param referredSlabState - {@link BlockState} to save in the {@link ItemStack} NBTs.
   * @return {@link ItemStack} with {@code referredSlabState} in its NBTs.
   */
  public static final ItemStack getItemStackWithState(ItemLike itemLike, BlockState referredSlabState) {
    ItemStack itemStack = new ItemStack(itemLike);
    BlockItem.setBlockEntityData(itemStack, JustVerticalSlabsLoader.VERTICAL_SLAB_BLOCK_ENTITY.get(), putReferredSlabState(new CompoundTag(), referredSlabState));
    return itemStack;
  }

  /**
   * Puts the given {@code referredSlabState} in the given {@link CompoundTag}.
   * 
   * @param compoundTag
   * @param referredSlabState
   * @return modified {@link CompoundTag}.
   */
  public static final CompoundTag putReferredSlabState(CompoundTag compoundTag, BlockState referredSlabState) {
    compoundTag.put(VerticalSlabUtils.NBT_ID, NbtUtils.writeBlockState(referredSlabState));
    return compoundTag;
  }

  /**
   * Returns the {@link VerticalSlabBlockEntity} for the given {@link BlockGetter} and {@link BlockPos}.
   * Creates a new {@link VerticalSlabBlockEntity} if the {@link Block} has one, but it has not been placed in the world yet (this can happen on world load).
   * Returns {@code null} if none could be found.
   * 
   * @param getter
   * @param pos
   * @return {@link VerticalSlabBlockEntity} or {@code null}.
   */
  @Nullable
  public static final VerticalSlabBlockEntity getVerticalSlabBlockEntity(BlockGetter getter, BlockPos pos) {
    BlockEntity blockEntity = getter.getBlockEntity(pos);
    if (blockEntity != null && blockEntity instanceof VerticalSlabBlockEntity) {
      return (VerticalSlabBlockEntity) blockEntity;
    }
    return null;
  }

  /**
   * Returns the {@link VerticalSlabBlockEntity} for the given {@link BlockGetter} and {@link BlockPos} if it exists.
   * Returns {@code null} if none could be found.
   * 
   * @param getter
   * @param pos
   * @return {@link VerticalSlabBlockEntity} or {@code null}.
   */
  @Nullable
  public static final VerticalSlabBlockEntity getSafeVerticalSlabBlockEntity(BlockGetter getter, BlockPos pos) {
    BlockEntity blockEntity = getter.getExistingBlockEntity(pos);
    if (blockEntity != null && blockEntity instanceof VerticalSlabBlockEntity) {
      return (VerticalSlabBlockEntity) blockEntity;
    }
    return null;
  }

  /**
   * Returns the {@link BlockState referredSlabState} for the given {@link BlockGetter} and {@link BlockPos}.
   * Creates a new {@link VerticalSlabBlockEntity} if the {@link Block} has one, but it has not been placed in the world yet (this can happen on world load).
   * Returns {@code null} if none could be found.
   * 
   * @param getter
   * @param pos
   * @return {@link BlockState referredSlabState} or {@code null}.
   */
  @Nullable
  public static final BlockState getReferredSlabState(BlockGetter getter, BlockPos pos) {
    VerticalSlabBlockEntity blockEntity = getVerticalSlabBlockEntity(getter, pos);
    if (blockEntity != null) {
      BlockState referredSlabState = blockEntity.getReferredSlabState();
      return referredSlabState;
    }
    return null;
  }

  /**
   * Returns the {@link BlockState referredSlabState} for the given {@link BlockGetter} and {@link BlockPos} if it exists.
   * Returns {@code null} if none could be found.
   * 
   * @param getter
   * @param pos
   * @return {@link BlockState referredSlabState} or {@code null}.
   */
  @Nullable
  public static final BlockState getSafeReferredSlabState(BlockGetter getter, BlockPos pos) {
    VerticalSlabBlockEntity blockEntity = getSafeVerticalSlabBlockEntity(getter, pos);
    if (blockEntity != null) {
      BlockState referredSlabState = blockEntity.getReferredSlabState();
      return referredSlabState;
    }
    return null;
  }

  /**
   * Returns the {@link BlockState referredSlabState} for the given {@link ItemStack}.
   * Returns {@code null} if none could be found.
   * 
   * @param itemStack
   * @return {@link BlockState referredSlabState} or {@code null}.
   */
  @Nullable
  public static final BlockState getReferredSlabState(ItemStack itemStack) {
    if (itemStack.getItem() instanceof VerticalSlabBlockItem) {
      CompoundTag compoundTag = itemStack.getTagElement("BlockEntityTag");
      if (compoundTag != null) {
        compoundTag = compoundTag.getCompound(VerticalSlabUtils.NBT_ID);
        if (compoundTag != null) {
          return NbtUtils.readBlockState(compoundTag);
        }
      }
    }
    return null;
  }

  /**
   * Returns the {@link BlockState referredBlockState} for the given {@link BlockGetter} and {@link BlockPos}.
   * Returns {@code null} if none could be found.
   * 
   * @param getter
   * @param pos
   * @return {@link BlockState referredBlockState} or {@code null}.
   */
  @Nullable
  public static final BlockState getReferredBlockState(BlockGetter getter, BlockPos pos) {
    return getReferredBlockState(getReferredSlabState(getter, pos));
  }

  /**
   * Returns the {@link BlockState referredBlockState} for the given {@link BlockState referredSlabState}.
   * Returns {@code null} if none could be found.
   * 
   * @param referredSlabState
   * @return {@link BlockState referredBlockState} or {@code null}.
   */
  @Nullable
  @SuppressWarnings("null")
  public static final BlockState getReferredBlockState(BlockState referredSlabState) {
    if (referredSlabState != null && MapsManager.slabMap.containsKey(referredSlabState.getBlock().asItem())) {
      return Block.byItem(MapsManager.slabMap.get(referredSlabState.getBlock().asItem())).defaultBlockState();
    }
    return null;
  }

  /**
   * Utility to manage all maps.
   */
  public static class MapsManager {
    /**
     * {@link ImmutableMap} linking Oxidizable Slab {@link Item Items} to their respective Waxed Slab {@link Item Items}.
     */
    @Nullable
    public static volatile ImmutableMap<Item, Item> waxingMap;
    /**
     * {@link ImmutableMap} linking Stonecuttable Block {@link Item Items} to their respective Slab {@link Item Items}.
     */
    @Nullable
    public static volatile ImmutableMap<Item, Item> stonecuttingMap;
    /**
     * {@link ImmutableMap} linking Block {@link Item Items} to their respective Slab {@link Item Items}.
     */
    @Nullable
    public static volatile ImmutableMap<Item, Item> blockMap;
    /**
     * {@link ImmutableMap} linking Slab {@link Item Items} to their respective Block {@link Item Items}.
     */
    @Nullable
    public static volatile ImmutableMap<Item, Item> slabMap;
    /**
     * {@link ImmutableMap} linking Translucent Slab {@link Item Items} to their respective {@link BlockState BlockStates}.
     */
    @Nullable
    public static volatile ImmutableMap<Item, BlockState> translucentMap;
    /**
     * {@link ImmutableMap} linking Slab {@link Item Items} to their respective {@link BlockState BlockStates}.
     */
    @Nullable
    public static volatile ImmutableMap<Item, BlockState> slabStateMap;

    /**
     * Contains all {@link BlockState} of Vertical Slab Items already added into the search tree to prevent adding duplicates.
     */
    private static final HashMap<BlockState, Boolean> inSearchTree = new HashMap<BlockState, Boolean>();

    /**
     * {@link RecipeManager} to use in {@link #computeMaps()}.
     */
    @Nullable
    private static RecipeManager fallbackRecipeManager;

    /**
     * Sets {@link #fallbackRecipeManager}.
     * 
     * @param recipeManager
     */
    public static final void setFallbackRecipeManager(RecipeManager recipeManager) {
      fallbackRecipeManager = recipeManager;
    }

    /**
     * Adds all Vertical Vlabs to the search tree if not already present.
     */
    @SuppressWarnings("null")
    public static final void addToSearchTree() {
      int intialSize = inSearchTree.size();
      RefreshableSearchTree<ItemStack> creativeSearchTree = Minecraft.getInstance().getSearchTree(SearchRegistry.CREATIVE_NAMES);
      for(BlockState referredSlabState : MapsManager.slabStateMap.values()) {
        if (!inSearchTree.containsKey(referredSlabState)) {
          creativeSearchTree.add(VerticalSlabUtils.getVerticalSlabItem(referredSlabState, VerticalSlabUtils.isTranslucent(referredSlabState)));
          inSearchTree.put(referredSlabState, true);
        }
      }
      if (intialSize != inSearchTree.size()) {
        creativeSearchTree.refresh();
      }
    }

    /**
     * Same as {@link #computeMaps(RecipeManager)}, but tries to use {@link #fallbackRecipeManager} instead of a given one.
     */
    public static final void computeMaps() {
      if (fallbackRecipeManager != null) {
        computeMaps(fallbackRecipeManager);
      } else {
        JustVerticalSlabsLoader.LOGGER.warn("Tried to compute " + JustVerticalSlabsLoader.MODID + " mod maps without a RecipeManager: maps will be empty.");
        MapsManager.slabStateMap = ImmutableMap.of();
        MapsManager.translucentMap = ImmutableMap.of();
        MapsManager.slabMap = ImmutableMap.of();
        MapsManager.blockMap = ImmutableMap.of();
        MapsManager.stonecuttingMap = ImmutableMap.of();
        MapsManager.waxingMap = ImmutableMap.of();
      }
    }

    /**
     * Searches through all {@link Item Items} with the {@link ItemTags#SLABS slabs} tag and associates them to the block they're made from.
     * Also computes other utility maps.
     * 
     * @param recipeManager
     */
    public static final void computeMaps(RecipeManager recipeManager) {
      JustVerticalSlabsLoader.LOGGER.debug("Computing " + JustVerticalSlabsLoader.MODID + " mod maps.");
      Map<Item, BlockState> slabStateMap = new LinkedHashMap<Item, BlockState>(), translucentMap = new LinkedHashMap<Item, BlockState>();
      Map<Item, Item> slabMap = new HashMap<Item, Item>(), blockMap = new HashMap<Item, Item>(), waxingMap = new HashMap<Item, Item>(), stonecuttingMap = new HashMap<Item, Item>();
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
                ItemStack[] items = ingredient.getItems();
                for (ItemStack itemStack : items) {
                  if (isPlain(itemStack.toString())) {
                    slabMap.put(slab, itemStack.getItem());
                  }
                  blockMap.putIfAbsent(itemStack.getItem(), slab);
                }
                // If no plain block is connected to this slab that means the slab is not plain either, so add the first (and theoretically only) block item.
                if (!slabMap.containsKey(slab) && items.length > 0) {
                  slabMap.put(slab, items[0].getItem());
                }
              });
            } else if (ingredients.stream().anyMatch(ingredient -> ingredient.test(Items.HONEYCOMB.getDefaultInstance()))) {
              List<Ingredient> notHoneyIngredients = ingredients.stream().filter(ingredient -> !ingredient.test(Items.HONEYCOMB.getDefaultInstance())).toList();
                ItemStack[] items = notHoneyIngredients.get(0).getItems();
                if (notHoneyIngredients.size() == 1 && items.length > 0) {
                ItemStack oxidizableSlab = items[0];
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
      MapsManager.slabStateMap = ImmutableMap.copyOf(slabStateMap);
      MapsManager.translucentMap = ImmutableMap.copyOf(translucentMap);
      MapsManager.slabMap = ImmutableMap.copyOf(slabMap);
      MapsManager.blockMap = ImmutableMap.copyOf(blockMap);
      MapsManager.stonecuttingMap = ImmutableMap.copyOf(stonecuttingMap);
      MapsManager.waxingMap = ImmutableMap.copyOf(waxingMap);
      JustVerticalSlabsLoader.LOGGER.debug(JustVerticalSlabsLoader.MODID + " mod maps generated.");
    }

    /**
     * Checks if the given {@link CraftingRecipe} is a recipe with all ingredients holding blocks and no slab.
     * 
     * @param recipe
     * @return whether the {@code recipe} uses only items that are blocks and not slabs.
     */
    private static final List<Ingredient> getBlockIngredients(NonNullList<Ingredient> ingredients) {
      return ingredients.stream().filter(ingredient -> Arrays.stream(ingredient.getItems()).allMatch(itemStack -> !itemStack.is(ItemTags.SLABS) && itemStack.getItem() instanceof BlockItem)).toList();
    }

    /**
     * Checks if there is at least one ingredient and, if more, checks that all ingredients hold the same items.
     * 
     * @param ingredients
     * @return whether all ingredients match, false if no ingredient is present.
     */
    private static final boolean sameIngredients(List<Ingredient> ingredients) {
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
    private static final boolean isPlain(String itemStackName) {
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
    private static final boolean isTranslucent(String itemStackName) {
      return (
        itemStackName.contains("transparent") ||
        itemStackName.contains("spawner") ||
        itemStackName.contains("glass") ||
        itemStackName.contains("slime") ||
        itemStackName.contains("honey") ||
        itemStackName.contains("ice")
      );
    }
  }
}
