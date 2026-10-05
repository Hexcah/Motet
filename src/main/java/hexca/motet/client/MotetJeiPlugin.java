package hexca.motet.client;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public final class MotetJeiPlugin implements IModPlugin {
    private static final ResourceLocation PLUGIN_ID =
            ResourceLocation.fromNamespaceAndPath("motet", "jei_plugin");
    private static final ResourceLocation TOTEM_BASE_ID =
            ResourceLocation.fromNamespaceAndPath("totemic", "totem_base");

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        Item totemBase = BuiltInRegistries.ITEM.get(TOTEM_BASE_ID);
        registration.registerSubtypeInterpreter(totemBase, new ISubtypeInterpreter<ItemStack>() {
            @Override
            public Object getSubtypeData(ItemStack stack, UidContext context) {
                return stack.getComponents().toString();
            }

            @Override
            public String getLegacyStringSubtypeInfo(ItemStack stack, UidContext context) {
                return stack.getComponents().toString();
            }
        });
    }

    @Override
    public void registerExtraIngredients(IExtraIngredientRegistration registration) {
        Item totemBase = BuiltInRegistries.ITEM.get(TOTEM_BASE_ID);
        if (totemBase == null) {
            throw new IllegalStateException("Totemic totem base item is not registered");
        }

        Registry<?> woodTypeRegistry = BuiltInRegistries.REGISTRY.get(
                ResourceLocation.fromNamespaceAndPath("totemic", "b_wood_type"));
        if (woodTypeRegistry == null) {
            throw new IllegalStateException("Totemic wood type registry is not available");
        }

        DataComponentType<?> woodTypeComponent = BuiltInRegistries.DATA_COMPONENT_TYPE.get(
                ResourceLocation.fromNamespaceAndPath("totemic", "wood_type"));
        if (woodTypeComponent == null) {
            throw new IllegalStateException("Totemic wood type component is not registered");
        }

        java.util.List<ItemStack> variants = new java.util.ArrayList<>();
        woodTypeRegistry.forEach(woodType -> {
            ItemStack totemBaseVariant = new ItemStack(totemBase);
            setComponent(totemBaseVariant, woodTypeComponent, woodType);
            variants.add(totemBaseVariant);
        });
        registration.addExtraItemStacks(variants);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void setComponent(ItemStack stack, DataComponentType<?> type, Object value) {
        stack.set((DataComponentType) type, value);
    }
}
