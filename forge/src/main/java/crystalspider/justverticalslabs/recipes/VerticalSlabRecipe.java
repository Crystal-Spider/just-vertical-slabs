package crystalspider.justverticalslabs.recipes;

import java.util.function.Supplier;

import com.google.gson.JsonObject;

import crystalspider.justverticalslabs.utils.VerticalSlabUtils;
import crystalspider.justverticalslabs.utils.VerticalSlabUtils.MapsManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * Abstract implementation of a {@link Recipe} involving a Vertical Slab.
 */
public interface VerticalSlabRecipe<C extends Container> extends Recipe<C> {
  /**
   * Returns the {@link ItemStack} with the result of this recipe from the given {@link Container}.
   */
  @Override
  public abstract ItemStack assemble(C container);

  /**
   * Checks if the given {@link Container} contains the correct items in the correct position to craft this recipe.
   */
  @Override
  public abstract boolean matches(C container, Level level);

  /**
   * Returns the recipe serializer.
   */
  @Override
  public abstract Serializer<C, ? extends VerticalSlabRecipe<C>> getSerializer();

  /**
   * Vertical Slab recipes are special since they make use of NBTs.
   */
  @Override
  default boolean isSpecial() {
    return true;
  }

  /**
   * Checks if the given {@link ItemStack} represents a valid Vertical Slab.
   * 
   * @param itemStack
   * @return whether the {@link ItemStack} is a Vertical Slab.
   */
  default boolean isVerticalSlab(ItemStack itemStack) {
    BlockState referredSlabState = VerticalSlabUtils.getReferredSlabState(itemStack);
    return !itemStack.isEmpty() && referredSlabState != null && MapsManager.slabStateMap.containsKey(referredSlabState.getBlock().asItem());
  }

  /**
   * Abstract implementation of a {@link RecipeSerializer} involving a {@link VerticalSlabRecipe}.
   */
  public static abstract class Serializer<C extends Container, T extends VerticalSlabRecipe<C>> extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<T> {
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
    public final void toNetwork(FriendlyByteBuf friendlyByteBuf, T verticalSlabRecipe) {}
  }
}
