package hexca.motet.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@JeiPlugin
public final class MotetJeiPlugin implements IModPlugin {
    private static final ResourceLocation PLUGIN_ID =
            ResourceLocation.fromNamespaceAndPath("motet", "jei_plugin");
    private static final String JEI_MANIFEST = "/data/motet/jei/totem_recipes.json";

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        Set<Item> variantItems = new HashSet<>();
        for (ItemStack stack : loadGeneratedRecipeOutputs()) {
            if (variantItems.add(stack.getItem())) {
                registration.registerSubtypeInterpreter(stack.getItem(), new ISubtypeInterpreter<ItemStack>() {
                    @Override
                    public Object getSubtypeData(ItemStack subtypeStack, UidContext context) {
                        return subtypeStack.getComponents().toString();
                    }

                    @Override
                    public String getLegacyStringSubtypeInfo(ItemStack subtypeStack, UidContext context) {
                        return subtypeStack.getComponents().toString();
                    }
                });
            }
        }
    }

    @Override
    public void registerExtraIngredients(IExtraIngredientRegistration registration) {
        registration.addExtraItemStacks(loadGeneratedRecipeOutputs());
    }

    private static List<ItemStack> loadGeneratedRecipeOutputs() {
        try (InputStream manifestStream = MotetJeiPlugin.class.getResourceAsStream(JEI_MANIFEST)) {
            if (manifestStream == null) {
                throw new IllegalStateException("Generated JEI recipe manifest is missing: " + JEI_MANIFEST);
            }

            JsonObject manifest = JsonParser.parseReader(
                    new InputStreamReader(manifestStream, StandardCharsets.UTF_8)).getAsJsonObject();
            List<ItemStack> outputs = new ArrayList<>();
            for (JsonElement recipePath : manifest.getAsJsonArray("recipes")) {
                outputs.add(loadRecipeOutput(recipePath.getAsString()));
            }
            return outputs;
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to read generated JEI recipe manifest", exception);
        }
    }

    private static ItemStack loadRecipeOutput(String recipePath) {
        String resourcePath = "/" + recipePath;
        try (InputStream recipeStream = MotetJeiPlugin.class.getResourceAsStream(resourcePath)) {
            if (recipeStream == null) {
                throw new IllegalStateException("Generated recipe is missing: " + resourcePath);
            }

            JsonObject result = JsonParser.parseReader(
                    new InputStreamReader(recipeStream, StandardCharsets.UTF_8))
                    .getAsJsonObject().getAsJsonObject("result");
            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(result.get("id").getAsString()));
            ItemStack stack = new ItemStack(item, result.has("count") ? result.get("count").getAsInt() : 1);
            if (result.has("components")) {
                JsonObject components = result.getAsJsonObject("components");
                for (String componentName : components.keySet()) {
                    applyRegistryComponent(stack, componentName, components.get(componentName));
                }
            }
            return stack;
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to read generated recipe: " + resourcePath, exception);
        }
    }

    private static void applyRegistryComponent(ItemStack stack, String componentName, JsonElement value) {
        ResourceLocation componentId = ResourceLocation.parse(componentName);
        ResourceLocation registryId = switch (componentName) {
            case "totemic:wood_type" -> ResourceLocation.fromNamespaceAndPath("totemic", "b_wood_type");
            case "totemic:carving" -> ResourceLocation.fromNamespaceAndPath("totemic", "c_totem_carving");
            default -> null;
        };
        if (registryId == null) {
            return;
        }

        Registry<?> valueRegistry = BuiltInRegistries.REGISTRY.get(registryId);
        DataComponentType<?> componentType = BuiltInRegistries.DATA_COMPONENT_TYPE.get(componentId);
        if (valueRegistry == null || componentType == null) {
            throw new IllegalStateException("Totemic recipe component is unavailable: " + componentName);
        }

        Object componentValue = valueRegistry.get(ResourceLocation.parse(value.getAsString()));
        setComponent(stack, componentType, componentValue);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void setComponent(ItemStack stack, DataComponentType<?> type, Object value) {
        stack.set((DataComponentType) type, value);
    }
}
