package com.nightdragon.create_portable_packaging;

import com.nightdragon.create_portable_packaging.item.PortablePackagerMenu;
import com.nightdragon.create_portable_packaging.item.PortablePackagerScreen;
import com.tterrag.registrate.util.entry.MenuEntry;

public class ModMenuTypes {
    public static final MenuEntry<PortablePackagerMenu> PORTABLE_PACKAGER = CreatePortablePackaging.REGISTRATE.menu(
            "portable_packager",
            (type, containerId, playerInventory) -> new PortablePackagerMenu(containerId, playerInventory),
            () -> PortablePackagerScreen::new).register();

    public static void register() {
    }
}
