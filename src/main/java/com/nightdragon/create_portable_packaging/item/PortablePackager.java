package com.nightdragon.create_portable_packaging.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PortablePackager extends Item {
    public PortablePackager(Properties pProperties) {
        super(pProperties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack heldStack = pPlayer.getItemInHand(pUsedHand);

        if (pPlayer instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(new MenuProvider() {
                @NotNull
                @Override
                public Component getDisplayName() {
                    return heldStack.getHoverName();
                }

                @Nullable
                @Override
                public AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pPlayerInventory,
                        @NotNull Player pPlayer) {
                    return new PortablePackagerMenu(pContainerId, pPlayerInventory, heldStack);
                }
            });
        }

        return InteractionResultHolder.sidedSuccess(heldStack, pLevel.isClientSide());
    }

    public static ItemStack find(Inventory playerInventory) {
        ItemStack mainHand = playerInventory.player.getMainHandItem();
        if (mainHand.getItem() instanceof PortablePackager) {
            return mainHand;
        }
        ItemStack offHand = playerInventory.player.getOffhandItem();
        if (offHand.getItem() instanceof PortablePackager) {
            return offHand;
        }

        for (int i = 0; i < playerInventory.getContainerSize(); i++) {
            ItemStack stack = playerInventory.getItem(i);
            if (stack.getItem() instanceof PortablePackager) {
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }
}
