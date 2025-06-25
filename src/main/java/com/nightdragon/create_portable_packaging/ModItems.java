package com.nightdragon.create_portable_packaging;

import com.nightdragon.create_portable_packaging.item.PortablePackager;
import com.tterrag.registrate.util.entry.ItemEntry;

public class ModItems {
    public static final ItemEntry<PortablePackager> PORTABLE_PACKAGER = CreatePortablePackaging.REGISTRATE
            .item("portable_packager", PortablePackager::new)
            .register();

    public static void register() {
    }
}
