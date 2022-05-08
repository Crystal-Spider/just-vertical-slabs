package crystalspider.justverticalslabs.recipes.stonecutter;

import java.util.function.Supplier;

import crystalspider.justverticalslabs.recipes.VerticalSlabRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.Level;

/**
 * Abstract implementation of a {@link StonecutterRecipe} involving a Vertical Slab.
 */
public abstract class VerticalSlabStonecutterRecipe extends StonecutterRecipe implements VerticalSlabRecipe<Container> {
  public VerticalSlabStonecutterRecipe(ResourceLocation resourceLocation, String string, ItemStack resultItem) {
    super(resourceLocation, string, Ingredient.EMPTY, resultItem);
  }

  @Override
  public abstract boolean matches(Container container, Level level);

  @Override
  public abstract Serializer<? extends VerticalSlabStonecutterRecipe> getSerializer();
  
  /**
   * Abstract implementation of a {@link RecipeSerializer} involving a {@link VerticalSlabStonecutterRecipe}.
   */
  public static abstract class Serializer<T extends VerticalSlabStonecutterRecipe> extends VerticalSlabRecipe.Serializer<Container, T> {
    protected Serializer(Supplier<T> recipeSupplier) {
      super(recipeSupplier);
    }
  }
}
