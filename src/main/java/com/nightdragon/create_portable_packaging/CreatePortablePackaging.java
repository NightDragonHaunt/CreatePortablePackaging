package com.nightdragon.create_portable_packaging;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import com.simibubi.create.foundation.data.CreateRegistrate;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(CreatePortablePackaging.MODID)
public class CreatePortablePackaging {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "create_portable_packaging";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID)
            .defaultCreativeTab("create_portable_packaging_tab",
                    t -> {
                        t.icon(() -> ModItems.PORTABLE_PACKAGER.get().getDefaultInstance());
                        t.title(Component.translatable("itemGroup.create_portable_packaging"));
                    })
            .build();

    public CreatePortablePackaging(IEventBus modEventBus, ModContainer modContainer) {
        REGISTRATE.registerEventListeners(modEventBus);

        ModItems.register();
        ModMenuTypes.register();
        ModPackets.register();
        ModDataComponents.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        // modEventBus.addListener(DataGenerators::gatherData);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Create: Portable Packaging Common Setup Initialized");
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
