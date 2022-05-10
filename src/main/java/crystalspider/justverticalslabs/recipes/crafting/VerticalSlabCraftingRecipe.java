package crystalspider.justverticalslabs.recipes.crafting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Supplier;

import crystalspider.justverticalslabs.recipes.VerticalSlabRecipe;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/**
 * Abstract implementation of a {@link CraftingRecipe} involving a Vertical Slab.
 */
public abstract class VerticalSlabCraftingRecipe implements VerticalSlabRecipe<CraftingContainer>, CraftingRecipe {
  /**
   * Width of the recipe.
   */
  private final int width;
  /**
   * Height of the recipe.
   */
  private final int height;

  public VerticalSlabCraftingRecipe(int width, int height) {
    this.width = width;
    this.height = height;
  }

  /**
   * Returns the default result item for this recipe.
   * Since {@link VerticalSlabRecipe VerticalSlabRecipes} are highly dependent on input data, this method should never be used.
   */
  @Override
  @Deprecated
  public final ItemStack getResultItem() {
    return ItemStack.EMPTY;
  }

  /**
   * Checks if the given {@code width} and {@code height} allow the recipe.
   */
  @Override
  public boolean canCraftInDimensions(int width, int height) {
    return width >= this.width && height >= this.height;
  }

  /**
   * Returns the {@link ItemStack} with the result of this recipe from the given {@link CraftingContainer}.
   */
  @Override
  public final ItemStack assemble(CraftingContainer craftingContainer) {
    return assemble(craftingContainer.getItem(getMatchIndex(craftingContainer)));
  }

  /**
   * Checks if the given {@link CraftingContainer} contains the correct items in the correct position to craft this recipe.
   */
  @Override
  public final boolean matches(CraftingContainer craftingContainer, Level level) {
    return getMatchIndex(craftingContainer) != null;
  }

  /**
   * Returns the {@link ItemStack} with the result of this recipe from the given {@code matchedItem}.
   * {@code matchedItem} value depends on the value from {@link #getMatchIndex(CraftingContainer)}.
   */
  @Nonnull
  protected abstract ItemStack assemble(@Nonnull ItemStack matchedItem);

  /**
   * Returns the index of the first item in the valid pattern.
   * Returns null if none could be found.
   * 
   * @param craftingContainer
   * @return index of the valid item or null.
   */
  @Nullable
  protected abstract Integer getMatchIndex(CraftingContainer craftingContainer);

  /**
   * Abstract implementation of a {@link RecipeSerializer} involving a {@link VerticalSlabCraftingRecipe}.
   */
  public static abstract class Serializer<T extends VerticalSlabCraftingRecipe> extends VerticalSlabRecipe.Serializer<CraftingContainer, T> {
    protected Serializer(Supplier<T> recipeSupplier) {
      super(recipeSupplier);
    }
  }
}
