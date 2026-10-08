package hexca.motet;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, Motet.MODID);

    public static final DeferredHolder<MobEffect, MobEffect> FLIGHT_EFFECT =
            MOB_EFFECTS.register("flight", FlightMobEffect::new);

    private static final ResourceLocation FLIGHT_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(Motet.MODID, "potion_flight");

    private ModEffects() {
    }

    public static void register(IEventBus modEventBus) {
        MOB_EFFECTS.register(modEventBus);
        modEventBus.addListener(ModEffects::addFlightAttribute);
    }

    private static void addFlightAttribute(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, NeoForgeMod.CREATIVE_FLIGHT);
    }

    private static final class FlightMobEffect extends MobEffect {
        private FlightMobEffect() {
            super(MobEffectCategory.BENEFICIAL, 0xDDDDDD);
            addAttributeModifier(
                    NeoForgeMod.CREATIVE_FLIGHT,
                    FLIGHT_MODIFIER_ID,
                    1.0,
                    AttributeModifier.Operation.ADD_VALUE);
        }
    }
}
