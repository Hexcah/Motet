package hexca.motet;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

@Mod(Motet.MODID)
public class Motet {
    public static final String MODID = "motet";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, MODID);
    public static final Supplier<Block> TOTEM_UPGRADE_1 = BLOCKS.register("totem_upgrade_1",
            () -> new Block(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final Supplier<Item> TOTEM_UPGRADE_1_ITEM = ITEMS.register("totem_upgrade_1",
            () -> new BlockItem(TOTEM_UPGRADE_1.get(), new Item.Properties()));
    public static final Supplier<Block> TOTEM_UPGRADE_2 = BLOCKS.register("totem_upgrade_2",
            () -> new Block(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final Supplier<Item> TOTEM_UPGRADE_2_ITEM = ITEMS.register("totem_upgrade_2",
            () -> new BlockItem(TOTEM_UPGRADE_2.get(), new Item.Properties()));

    public Motet(IEventBus modEventBus, ModContainer modContainer) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        ModEffects.register(modEventBus);
        NeoForge.EVENT_BUS.addListener(Motet::onBlockDrops);
        modEventBus.addListener(MotetTotems::register);
        modEventBus.addListener(Motet::addCreativeTabContents);
        modContainer.registerConfig(ModConfig.Type.COMMON, MotetConfig.SPEC);
    }

    private static void addCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(TOTEM_UPGRADE_1_ITEM.get());
            event.accept(TOTEM_UPGRADE_2_ITEM.get());
        }
    }

    private static void onBlockDrops(BlockDropsEvent event) {
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(event.getState().getBlock());
        if (!"totemic".equals(blockId.getNamespace())
                || !("totem_base".equals(blockId.getPath()) || "totem_pole".equals(blockId.getPath()))) {
            return;
        }
        Item item = BuiltInRegistries.ITEM.get(blockId);
        if (event.getBreaker() instanceof Player player && player.getAbilities().instabuild) {
            event.getDrops().removeIf(drop -> drop.getItem().is(item));
            return;
        }

        boolean hasLootTableDrop = event.getDrops().stream().anyMatch(drop -> drop.getItem().is(item));
        if (event.getBreaker() == null && !hasLootTableDrop) {
            return;
        }

        ItemStack totemStack = createTotemStack(event, blockId, item);
        if (totemStack.isEmpty()) {
            return;
        }

        event.getDrops().removeIf(drop -> drop.getItem().is(item));
        event.getDrops().add(new ItemEntity(event.getLevel(),
                event.getPos().getX() + 0.5,
                event.getPos().getY() + 0.5,
                event.getPos().getZ() + 0.5,
                totemStack));
    }

    private static ItemStack createTotemStack(BlockDropsEvent event, ResourceLocation blockId, Item item) {
        if (item == null || event.getBlockEntity() == null) {
            return ItemStack.EMPTY;
        }

        CompoundTag blockEntityData = event.getBlockEntity().saveWithoutMetadata(event.getLevel().registryAccess());
        Registry<?> woodTypes = BuiltInRegistries.REGISTRY.get(
                ResourceLocation.fromNamespaceAndPath("totemic", "b_wood_type"));
        DataComponentType<?> woodTypeComponent = BuiltInRegistries.DATA_COMPONENT_TYPE.get(
                ResourceLocation.fromNamespaceAndPath("totemic", "wood_type"));
        if (woodTypes == null || woodTypeComponent == null) {
            return ItemStack.EMPTY;
        }

        Object woodType = woodTypes.get(ResourceLocation.parse(blockEntityData.getString("Wood")));
        if (woodType == null) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = new ItemStack(item);
        setComponent(stack, woodTypeComponent, woodType);
        if ("totem_pole".equals(blockId.getPath())) {
            Registry<?> carvings = BuiltInRegistries.REGISTRY.get(
                    ResourceLocation.fromNamespaceAndPath("totemic", "c_totem_carving"));
            DataComponentType<?> carvingComponent = BuiltInRegistries.DATA_COMPONENT_TYPE.get(
                    ResourceLocation.fromNamespaceAndPath("totemic", "carving"));
            if (carvings == null || carvingComponent == null) {
                return ItemStack.EMPTY;
            }
            Object carving = carvings.get(ResourceLocation.parse(blockEntityData.getString("Carving")));
            if (carving == null) {
                return ItemStack.EMPTY;
            }
            setComponent(stack, carvingComponent, carving);
        }
        return stack;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void setComponent(ItemStack stack, DataComponentType<?> type, Object value) {
        stack.set((DataComponentType) type, value);
    }
}
