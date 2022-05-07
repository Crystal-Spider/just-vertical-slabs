package crystalspider.justverticalslabs;

import crystalspider.justverticalslabs.blocks.verticalslab.VerticalSlabBlock;
import crystalspider.justverticalslabs.blocks.verticalslab.VerticalSlabBlockEntity;
import crystalspider.justverticalslabs.handlers.ModelRegistryEventHandler;
import crystalspider.justverticalslabs.handlers.RecipeUpdateEventHandler;
import crystalspider.justverticalslabs.handlers.ServerAboutToStartEventHandler;
import crystalspider.justverticalslabs.items.VerticalSlabBlockItem;
import crystalspider.justverticalslabs.recipes.crafting.recipes.BlockToVerticalSlabCraftingRecipe;
import crystalspider.justverticalslabs.recipes.crafting.recipes.SlabToVerticalSlabCraftingRecipe;
import crystalspider.justverticalslabs.recipes.crafting.recipes.VerticalSlabToBlockCraftingRecipe;
import crystalspider.justverticalslabs.recipes.crafting.recipes.VerticalSlabToSlabCraftingRecipe;
import crystalspider.justverticalslabs.recipes.stonecutter.recipes.BlockToVerticalSlabStonecutterRecipe;
import crystalspider.justverticalslabs.utils.VerticalSlabUtils;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * JustVerticalSlabs mod loader.
 */
@Mod(JustVerticalSlabsLoader.MODID)
public class JustVerticalSlabsLoader {
  /**
   * JustVerticalSlab mod ID.
   */
  public static final String MODID = "justverticalslabs";
  /**
   * Vertical Slab resource ID.
   */
  public static final String VERTICAL_SLAB_ID = "vertical_slab";

  /**
   * {@link CreativeModTab} for {@link VerticalSlabBlockItem Vertical Slab Items}.
   */
  public static final CreativeModeTab TAB_JUST_VERTICAL_SLABS = new CreativeModeTab("justVerticalSlabs") {
    public ItemStack makeIcon() {
      return VerticalSlabUtils.getDefaultInstance();
    }
  }.setRecipeFolderName(VERTICAL_SLAB_ID);

  /**
   * {@link Block Blocks} {@link DeferredRegisterdeferred register}.
   */
  public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
  /**
   * Block Entities {@link DeferredRegisterdeferred register}.
   */
  public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MODID);
  /**
   * {@link Item Items} {@link DeferredRegisterdeferred register}.
   */
  public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
  /**
   * {@link RecipeSerializer Recipe Serializer} {@link DeferredRegisterdeferred register}.
   */
  public static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);

  /**
   * {@link RegistryObject} for {@link VerticalSlabBlock}.
   */
  public static final RegistryObject<VerticalSlabBlock> VERTICAL_SLAB_BLOCK = BLOCKS.register(VERTICAL_SLAB_ID, VerticalSlabBlock::new);
  /**
   * {@link RegistryObject} for {@link VerticalSlabBlockEntity}.
   */
  public static final RegistryObject<BlockEntityType<VerticalSlabBlockEntity>> VERTICAL_SLAB_BLOCK_ENTITY = BLOCK_ENTITIES.register(VERTICAL_SLAB_ID + "_block_entity", () -> BlockEntityType.Builder.of(VerticalSlabBlockEntity::new, VERTICAL_SLAB_BLOCK.get()).build(null));
  /**
   * {@link RegistryObject} for {@link VerticalSlabBlockItem}.
   */
  public static final RegistryObject<VerticalSlabBlockItem> VERTICAL_SLAB_ITEM = ITEMS.register(VERTICAL_SLAB_ID, () -> new VerticalSlabBlockItem(VERTICAL_SLAB_BLOCK.get(), new Item.Properties().tab(TAB_JUST_VERTICAL_SLABS)));
  /**
   * {@link RegistryObject} for {@link VerticalSlabToBlockCraftingRecipe} {@link VerticalSlabToBlockCraftingRecipe.Serializer Serializer}.
   */
  public static final RegistryObject<VerticalSlabToBlockCraftingRecipe.Serializer> REFERRING_BLOCK_RECIPE_SERIALIZER = RECIPES.register(VerticalSlabToBlockCraftingRecipe.Serializer.ID, VerticalSlabToBlockCraftingRecipe.Serializer::new);
  /**
   * {@link RegistryObject} for {@link VerticalSlabToSlabCraftingRecipe} {@link VerticalSlabToSlabCraftingRecipe.Serializer Serializer}.
   */
  public static final RegistryObject<VerticalSlabToSlabCraftingRecipe.Serializer> SLAB_RECIPE_SERIALIZER = RECIPES.register(VerticalSlabToSlabCraftingRecipe.Serializer.ID, VerticalSlabToSlabCraftingRecipe.Serializer::new);
  /**
   * {@link RegistryObject} for {@link SlabToVerticalSlabCraftingRecipe} {@link SlabToVerticalSlabCraftingRecipe.Serializer Serializer}.
   */
  public static final RegistryObject<SlabToVerticalSlabCraftingRecipe.Serializer> SLAB_VERTICAL_SLAB_RECIPE_SERIALIZER = RECIPES.register(SlabToVerticalSlabCraftingRecipe.Serializer.ID, SlabToVerticalSlabCraftingRecipe.Serializer::new);
  /**
   * {@link RegistryObject} for {@link BlockToVerticalSlabCraftingRecipe} {@link BlockToVerticalSlabCraftingRecipe.Serializer Serializer}.
   */
  public static final RegistryObject<BlockToVerticalSlabCraftingRecipe.Serializer> BLOCK_VERTICAL_SLAB_RECIPE_SERIALIZER = RECIPES.register(BlockToVerticalSlabCraftingRecipe.Serializer.ID, BlockToVerticalSlabCraftingRecipe.Serializer::new);
  public static final RegistryObject<BlockToVerticalSlabStonecutterRecipe.Serializer> TEST_RECIPE_SERIALIZER = RECIPES.register(BlockToVerticalSlabStonecutterRecipe.Serializer.ID, BlockToVerticalSlabStonecutterRecipe.Serializer::new);

  public JustVerticalSlabsLoader() {
    MinecraftForge.EVENT_BUS.register(new ServerAboutToStartEventHandler());
    MinecraftForge.EVENT_BUS.register(new RecipeUpdateEventHandler());
    IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    BLOCKS.register(bus);
    BLOCK_ENTITIES.register(bus);
    ITEMS.register(bus);
    RECIPES.register(bus);
    bus.register(new ModelRegistryEventHandler());
  }
}
