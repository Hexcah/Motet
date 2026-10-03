package ltd.hexca.totemictotemupgrades;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

@Mod(Totemictotemupgrades.MODID)
public class Totemictotemupgrades {
    public static final String MODID = "totemictotemupgrades";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, MODID);
    public static final Supplier<Block> TOTEM_UPGRADE_1 = BLOCKS.register("totem_upgrade_1",
            () -> new Block(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final Supplier<Item> TOTEM_UPGRADE_1_ITEM = ITEMS.register("totem_upgrade_1",
            () -> new BlockItem(TOTEM_UPGRADE_1.get(), new Item.Properties()));

    public Totemictotemupgrades(IEventBus modEventBus, ModContainer modContainer) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        modEventBus.addListener(Totemictotemupgrades::addCreativeTabContents);
        modContainer.registerConfig(ModConfig.Type.COMMON, TotemictotemupgradesConfig.SPEC);
    }

    private static void addCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(TOTEM_UPGRADE_1_ITEM.get());
        }
    }
}
