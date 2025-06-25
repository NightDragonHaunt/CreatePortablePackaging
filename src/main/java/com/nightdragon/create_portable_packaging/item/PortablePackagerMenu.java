package com.nightdragon.create_portable_packaging.item;

import com.nightdragon.create_portable_packaging.CreatePortablePackaging;

import com.nightdragon.create_portable_packaging.ModDataComponents;
import com.nightdragon.create_portable_packaging.ModMenuTypes;

import com.simibubi.create.content.logistics.box.PackageItem;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PortablePackagerMenu extends AbstractContainerMenu {

    public final Player player;
    public final ItemStack portablePackagerStack;
    private final IItemHandler itemHandler;

    public PortablePackagerMenu(int windowId, Inventory playerInventory) {
        this(windowId, playerInventory, PortablePackager.find(playerInventory));
    }

    public PortablePackagerMenu(int windowId, Inventory playerInventory, ItemStack packagerStack) {
        super(ModMenuTypes.PORTABLE_PACKAGER.get(), windowId);
        this.player = playerInventory.player;
        this.portablePackagerStack = Objects.requireNonNull(packagerStack, "Portable Packager stack cannot be null!");
        this.itemHandler = new ItemStackHandler(9);

        loadInventory();

        for (int i = 0; i < 9; ++i) {
            this.addSlot(new SlotItemHandler(itemHandler, i,
                    PortablePackagerScreen.PACKAGER_SLOT_X + i * (PortablePackagerScreen.SLOT_SIZE + 2),
                    PortablePackagerScreen.PACKAGER_SLOT_Y));
        }

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9,
                        PortablePackagerScreen.PLAYER_INVENTORY_X + j * 18,
                        PortablePackagerScreen.PLAYER_INVENTORY_Y + i * 18));
            }
        }

        // 快捷栏
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i,
                    PortablePackagerScreen.PLAYER_INVENTORY_X + i * 18,
                    PortablePackagerScreen.PLAYER_HOTBAR_Y));
        }
    }

    public void createAndGivePackage(String address) {
        /// 逻辑：遍历打包机的物品栏，将所有非空物品打包成一个包裹，并给予玩家。
        ItemStackHandler packageContentHandler = new ItemStackHandler(9);
        int itemsFound = 0;
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stackInSlot = itemHandler.getStackInSlot(i);
            if (!stackInSlot.isEmpty()) {
                packageContentHandler.setStackInSlot(itemsFound++, stackInSlot);
            }
        }

        if (itemsFound == 0) {
            return;
        }

        ItemStack packageStack = PackageItem.containing(packageContentHandler);
        if (address != null && !address.isEmpty()) {
            PackageItem.addAddress(packageStack, address);
            CreatePortablePackaging.LOGGER.info("PP>>>>>>> Creating package with address: " + address);
        } else {
            CreatePortablePackaging.LOGGER.warn("PP>>>>>>> Creating package without address");
        }
        for (int i = 0; i < packageContentHandler.getSlots(); i++) {
            ItemStack stack = packageContentHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                CreatePortablePackaging.LOGGER.info("PP>>>>>>> Item in package slot " + i + ": " + stack);
            }
        }

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ((ItemStackHandler) itemHandler).setStackInSlot(i, ItemStack.EMPTY);
        }

        ItemHandlerHelper.giveItemToPlayer(player, packageStack);

        saveInventory();
        portablePackagerStack.set(ModDataComponents.LAST_USED_ADDRESS, address);
    }

    @Override
    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        if (!portablePackagerStack.isEmpty()) {
            // Log all items in the packager's inventory
            for (int i = 0; i < itemHandler.getSlots(); i++) {
                ItemStack stack = itemHandler.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    CreatePortablePackaging.LOGGER.info("PP>>>>>>> Item in slot " + i + ": " + stack);
                }
            }
            saveInventory();
        }
    }

    private void saveInventory() {
        List<ItemStack> items = new ArrayList<>(9);
        for (int i = 0; i < 9; i++) {
            items.add(itemHandler.getStackInSlot(i));
        }
        portablePackagerStack.set(ModDataComponents.PORTABLE_PACKAGER_INVENTORY, items);
    }

    private void loadInventory() {
        if (portablePackagerStack.isEmpty())
            return;
        List<ItemStack> items = portablePackagerStack.get(ModDataComponents.PORTABLE_PACKAGER_INVENTORY);
        if (items != null) {
            for (int i = 0; i < Math.min(items.size(), 9); i++) {
                ((ItemStackHandler) itemHandler).setStackInSlot(i, items.get(i));
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack quickMovedStack = ItemStack.EMPTY;
        Slot quickMovedSlot = this.slots.get(pIndex);

        if (quickMovedSlot != null && quickMovedSlot.hasItem()) {
            ItemStack rawStack = quickMovedSlot.getItem();
            quickMovedStack = rawStack.copy();

            if (pIndex < 9) {
                if (!this.moveItemStackTo(rawStack, 9, 45, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (pIndex >= 9 && pIndex < 45) {
                if (!this.moveItemStackTo(rawStack, 0, 9, false)) {
                    if (pIndex < 36) {
                        if (!this.moveItemStackTo(rawStack, 36, 45, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.moveItemStackTo(rawStack, 9, 36, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else {
                return ItemStack.EMPTY;
            }

            if (rawStack.isEmpty()) {
                quickMovedSlot.set(ItemStack.EMPTY);
            } else {
                quickMovedSlot.setChanged();
            }
            if (rawStack.getCount() == quickMovedStack.getCount()) {
                return ItemStack.EMPTY;
            }
            quickMovedSlot.onTake(player, rawStack);
        }

        return quickMovedStack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return pPlayer.getMainHandItem() == portablePackagerStack || pPlayer.getOffhandItem() == portablePackagerStack;
    }
}
