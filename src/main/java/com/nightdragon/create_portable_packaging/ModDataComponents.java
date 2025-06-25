package com.nightdragon.create_portable_packaging;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.UnaryOperator;

public class ModDataComponents {
        public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister
                        .create(Registries.DATA_COMPONENT_TYPE, CreatePortablePackaging.MODID);

        public static final DataComponentType<List<ItemStack>> PORTABLE_PACKAGER_INVENTORY = register(
                        "portable_packager_inventory",
                        builder -> builder.persistent(ItemStack.OPTIONAL_CODEC.listOf())
                                        .networkSynchronized(ItemStack.OPTIONAL_LIST_STREAM_CODEC));

        public static final DataComponentType<String> LAST_USED_ADDRESS = register(
                        "last_used_address",
                        builder -> builder.persistent(Codec.STRING));

        private static <T> DataComponentType<T> register(String name,
                        UnaryOperator<DataComponentType.Builder<T>> builder) {
                DataComponentType<T> type = builder.apply(DataComponentType.builder()).build();
                DATA_COMPONENTS.register(name, () -> type);
                return type;
        }

        public static void register(IEventBus eventBus) {
                DATA_COMPONENTS.register(eventBus);
        }
}
