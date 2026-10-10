package hexca.motet;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

public final class MotetTotems {
    private static final ResourceKey<Registry<Object>> TOTEM_CARVING_REGISTRY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("totemic", "c_totem_carving"));

    private MotetTotems() {
    }

    public static void register(RegisterEvent event) {
        event.register(TOTEM_CARVING_REGISTRY, helper -> {
            registerPotionTotem(helper, "turtle", true, MobEffects.MOVEMENT_SLOWDOWN, MobEffects.DAMAGE_RESISTANCE);
            registerPotionTotem(helper, "ghast", false, ModEffects.FLIGHT_EFFECT);
            registerPotionTotem(helper, "glow_squid", false, MobEffects.GLOWING);
        });
    }

    public static void modifyCowTotem(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> modifyPotionTotem("cow", false, MobEffects.SATURATION));
    }

    private static void modifyPotionTotem(String name, boolean scaleAmplifier, Holder<?> effect) {
        try {
            Class<?> apiClass = Class.forName("pokefenn.totemic.api.TotemicAPI");
            Object api = apiClass.getMethod("get").invoke(null);
            Object registryApi = apiClass.getMethod("registry").invoke(api);
            Object carvings = registryApi.getClass().getMethod("totemCarvings").invoke(registryApi);
            ResourceLocation carvingId = ResourceLocation.fromNamespaceAndPath("totemic", name);
            Object carving = Registry.class.getMethod("get", ResourceLocation.class).invoke(carvings, carvingId);
            if (carving == null) {
                throw new IllegalStateException("Totemic carving is unavailable: " + carvingId);
            }

            Class<?> potionEffectClass = Class.forName("pokefenn.totemic.api.totem.PotionTotemEffect");
            Object potionEffect = potionEffectClass
                    .getConstructor(Holder.class, boolean.class)
                    .newInstance(effect, scaleAmplifier);
            Class<?> totemCarvingClass = Class.forName("pokefenn.totemic.api.totem.TotemCarving");
            totemCarvingClass.getMethod("setEffects", List.class)
                    .invoke(carving, List.of(potionEffect));
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                 | IllegalAccessException | InvocationTargetException exception) {
            throw new IllegalStateException("Totemic's carving API is unavailable", exception);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void registerPotionTotem(
            RegisterEvent.RegisterHelper helper, String name, boolean scaleAmplifier, Holder<?>... effects) {
        helper.register(ResourceLocation.fromNamespaceAndPath(Motet.MODID, name),
                createPotionCarving(scaleAmplifier, effects));
    }

    private static Object createPotionCarving(boolean scaleAmplifier, Holder<?>... effects) {
        try {
            Class<?> totemEffectClass = Class.forName("pokefenn.totemic.api.totem.TotemEffect");
            Class<?> potionEffectClass = Class.forName("pokefenn.totemic.api.totem.PotionTotemEffect");
            List<Object> potionEffects = new java.util.ArrayList<>(effects.length);
            for (Holder<?> effect : effects) {
                potionEffects.add(potionEffectClass
                        .getConstructor(Holder.class, boolean.class)
                        .newInstance(effect, scaleAmplifier));
            }
            Class<?> carvingClass = Class.forName("pokefenn.totemic.api.totem.TotemCarving");
            return carvingClass.getMethod("of", List.class).invoke(null, potionEffects);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                 | IllegalAccessException | InvocationTargetException exception) {
            throw new IllegalStateException("Totemic's carving API is unavailable", exception);
        }
    }
}
