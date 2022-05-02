package crystalspider.justverticalslabs;

import com.google.common.collect.ImmutableMap;

import crystalspider.justverticalslabs.blocks.verticalslab.VerticalSlabBlock;
import crystalspider.justverticalslabs.blocks.verticalslab.VerticalSlabBlockEntity;
import crystalspider.justverticalslabs.handlers.ModelRegistryEventHandler;
import crystalspider.justverticalslabs.handlers.ServerAboutToStartEventHandler;
import crystalspider.justverticalslabs.items.VerticalSlabBlockItem;
import crystalspider.justverticalslabs.utils.VerticalSlabUtils;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
      return VerticalSlabUtils.getItemStackWithState(VERTICAL_SLAB_BLOCK.get(), Blocks.OAK_PLANKS.defaultBlockState());
    }
  }.setRecipeFolderName(VERTICAL_SLAB_ID);

  /**
   * {@link Block Blocks} {@link DeferredRegisterdeferred register}.
   */
  public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
  /**
   * {@link Item Items} {@link DeferredRegisterdeferred register}.
   */
  public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
  /**
   * Block Entities {@link DeferredRegisterdeferred register}.
   */
  public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MODID);

  /**
   * {@link RegistryObject} for {@link VerticalSlabBlock}.
   */
  public static final RegistryObject<VerticalSlabBlock> VERTICAL_SLAB_BLOCK = BLOCKS.register(VERTICAL_SLAB_ID, () -> new VerticalSlabBlock());
  /**
   * {@link RegistryObject} for {@link VerticalSlabBlockItem}.
   */
  public static final RegistryObject<VerticalSlabBlockItem> VERTICAL_SLAB_ITEM = ITEMS.register(VERTICAL_SLAB_ID, () -> new VerticalSlabBlockItem(VERTICAL_SLAB_BLOCK.get(), new Item.Properties().tab(TAB_JUST_VERTICAL_SLABS)));
  /**
   * {@link RegistryObject} for {@link VerticalSlabBlockEntity}.
   */
  public static final RegistryObject<BlockEntityType<VerticalSlabBlockEntity>> VERTICAL_SLAB_BLOCK_ENTITY = BLOCK_ENTITIES.register(VERTICAL_SLAB_ID, () -> BlockEntityType.Builder.of(VerticalSlabBlockEntity::new, VERTICAL_SLAB_BLOCK.get()).build(null));

  public static volatile ImmutableMap<Item, Item> slabMap;

  public JustVerticalSlabsLoader() {
    MinecraftForge.EVENT_BUS.register(new ServerAboutToStartEventHandler());
    IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    BLOCKS.register(bus);
    ITEMS.register(bus);
    BLOCK_ENTITIES.register(bus);
    bus.register(new ModelRegistryEventHandler());
  }
}
