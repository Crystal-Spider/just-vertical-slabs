package crystalspider.justverticalslabs.recipes;

import com.google.common.base.Supplier;
import com.google.gson.JsonObject;

import crystalspider.justverticalslabs.utils.VerticalSlabUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * Abstract implementation of a {@link CraftingRecipe} involving a Vertical Slab.
 */
public abstract class VerticalSlabRecipe implements CraftingRecipe {
  /**
   * Width of the recipe.
   */
  private final int width;
  /**
   * Height of the recipe.
   */
  private final int height;
  /**
   * Default result item for this recipe.
   * Since {@link VerticalSlabRecipe VerticalSlabRecipes} are highly dependent on input data, this method should never be used.
   */
  @Deprecated
  private final ItemStack resultItem;

  public VerticalSlabRecipe(int width, int height, ItemStack resultItem) {
    this.width = width;
    this.height = height;
    this.resultItem = resultItem;
  }

  /**
   * Checks if the given {@code width} and {@code height} allow the recipe.
   */
  @Override
  public boolean canCraftInDimensions(int width, int height) {
    return width >= this.width && height >= this.height;
  }

  /**
   * Returns the default result item for this recipe.
   * Since {@link VerticalSlabRecipe VerticalSlabRecipes} are highly dependent on input data, this method should never be used.
   */
  @Override
  @Deprecated
  public ItemStack getResultItem() {
    return resultItem;
  }

  /**
   * Returns the recipe serializer.
   */
  @Override
  public abstract Serializer<? extends VerticalSlabRecipe> getSerializer();

  /**
   * Checks if the given {@link ItemStack} represents a valid Vertical Slab.
   * 
   * @param itemStack
   * @return whether the {@link ItemStack} is a Vertical Slab.
   */
  protected boolean isVerticalSlab(ItemStack itemStack) {
    return !itemStack.isEmpty() && VerticalSlabUtils.getReferringBlockState(itemStack) != null;
  }

  /**
   * Checks if all provided {@link ItemStack ItemStacks} represent the same kind of Vertical Slab, that is they all have the same referringBlockState.
   * Note that {@code itemStacks} must have at least one element.
   * 
   * @param itemStacks - list of {@link ItemStacks} representing Vertical Slabs.
   * @return whether all {@link ItemStack ItemStacks} represent the same kind of Vertical Slab.
   */
  protected boolean verticalSlabsMatch(ItemStack ...itemStacks) {
    boolean match = true;
    BlockState referringBlockState = VerticalSlabUtils.getReferringBlockState(itemStacks[0]);
    if (referringBlockState != null) {
      for (int i = 1; i < itemStacks.length && match; i++) {
        match = referringBlockState == VerticalSlabUtils.getReferringBlockState(itemStacks[i]);
      }
    }
    return match;
  }

  /**
   * Abstract implementation of a {@link RecipeSerializer} involving a {@link VerticalSlabRecipe}.
   */
  public static abstract class Serializer<T extends VerticalSlabRecipe> extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<T> {
    /**
     * {@link Supplier} that will be used to return a new {@link VerticalSlabRecipe} when needed.
     */
    private final Supplier<T> recipeSupplier;

    protected Serializer(Supplier<T> recipeSupplier) {
      this.recipeSupplier = recipeSupplier;
    }

    /**
     * Decodes the given {@link JsonObject} into a {@link VerticalSlabRecipe}.
     * Since no data from the {@code jsonObject} is needed, a new {@link VerticalSlabRecipe} subtype instance is simply returned.
     */
    @Override
    public final T fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
      return recipeSupplier.get();
    }

    /**
     * Decodes a {@link VerticalSlabRecipe} from the buffer sent from the server.
     * Since no data from the {@link FriendlyByteBuf} is needed, a new {@link VerticalSlabRecipe} subtype instance is simply returned.
     */
    @Override
    public final T fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
      return recipeSupplier.get();
    }

    /**
     * Encodes a {@link VerticalSlabRecipe} to the buffer to send to the client.
     * Since no data is needed, nothing is written on the buffer.
     */
    @Override
    public final void toNetwork(FriendlyByteBuf friendlyByteBuf, T referringBlockRecipe) {}
  }
}
