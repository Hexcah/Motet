package hexca.motet;

import java.lang.reflect.InvocationTargetException;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.neoforge.registries.RegisterEvent;

public final class MotetTotems {
    private static final ResourceKey<Registry<Object>> TOTEM_CARVING_REGISTRY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("totemic", "c_totem_carving"));

    private MotetTotems() {
    }

    public static void register(RegisterEvent event) {
        event.register(TOTEM_CARVING_REGISTRY, helper -> {
            registerPotionTotem(helper, "turtle", MobEffects.HEALTH_BOOST);
        });
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void registerPotionTotem(RegisterEvent.RegisterHelper helper, String name, Holder<?> effect) {
        helper.register(ResourceLocation.fromNamespaceAndPath(Motet.MODID, name), createPotionCarving(effect));
    }

    private static Object createPotionCarving(Holder<?> effect) {
        try {
            Class<?> totemEffectClass = Class.forName("pokefenn.totemic.api.totem.TotemEffect");
            Class<?> potionEffectClass = Class.forName("pokefenn.totemic.api.totem.PotionTotemEffect");
            Object potionEffect = potionEffectClass
                    .getConstructor(Holder.class, boolean.class)
                    .newInstance(effect, false);
            Class<?> carvingClass = Class.forName("pokefenn.totemic.api.totem.TotemCarving");
            return carvingClass.getMethod("of", totemEffectClass).invoke(null, potionEffect);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                 | IllegalAccessException | InvocationTargetException exception) {
            throw new IllegalStateException("Totemic's carving API is unavailable", exception);
        }
    }
}
