package com.nightdragon.create_portable_packaging.item;

import com.nightdragon.create_portable_packaging.CreatePortablePackaging;
import com.nightdragon.create_portable_packaging.ModDataComponents;
import com.nightdragon.create_portable_packaging.ModPackets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.content.logistics.AddressEditBox;
import com.simibubi.create.content.trains.station.NoShadowFontWrapper;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.utility.CreateLang;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import org.lwjgl.glfw.GLFW;

public class PortablePackagerScreen extends AbstractSimiContainerScreen<PortablePackagerMenu> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            CreatePortablePackaging.MODID, "textures/gui/portable_packager.png");

    public static final int GUI_WIDTH = 200;
    public static final int GUI_HEIGHT = 144;
    public static final int SLOT_SIZE = 18;

    public static final int PACKAGER_SLOT_X = 12;
    public static final int PACKAGER_SLOT_Y = 11;

    public static final int ADDRESS_X = 27;
    public static final int ADDRESS_Y = 36;
    public static final int ADDRESS_W = 88;
    public static final int ADDRESS_H = 10;

    public static final int BUTTON_X = 140;
    public static final int BUTTON_Y = 31;
    public static final int BUTTON_W = 60;
    public static final int BUTTON_H = 18;
    public static final int HOVER_X = 12;
    public static final int HOVER_Y = 150;

    public static final int SEND_TEXT_X = 158;
    public static final int SEND_TEXT_Y = 35;

    public static final int PLAYER_INVENTORY_X = 20;
    public static final int PLAYER_INVENTORY_Y = 66;
    public static final int PLAYER_HOTBAR_Y = 124;

    public static final int TEXT_COLOR = 0x714A40;

    private AddressEditBox addressBox;
    // private Button sendButton;

    public PortablePackagerScreen(PortablePackagerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = GUI_WIDTH;
        this.imageHeight = GUI_HEIGHT;
    }

    @Override
    protected void init() {
        setWindowSize(this.imageWidth, this.imageHeight);
        super.init();

        addressBox = new AddressEditBox(this, new NoShadowFontWrapper(font), leftPos + ADDRESS_X, topPos + ADDRESS_Y,
                ADDRESS_W, ADDRESS_H,
                true);
        addressBox.setTextColor(TEXT_COLOR);
        addressBox.setBordered(false);
        String lastAddress = menu.portablePackagerStack.get(ModDataComponents.LAST_USED_ADDRESS);
        if (lastAddress != null)
            addressBox.setValue(lastAddress);
        addRenderableWidget(addressBox);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        addressBox.tick();
    }

    private void sendPackage() {
        ModPackets.sendToServer(new PortablePackagerPacket(addressBox.getValue()));
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int x = getGuiLeft();
        int y = getGuiTop();

        pGuiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
        if (addressBox.getValue()
                .isBlank() && !addressBox.isFocused()) {
            pGuiGraphics.drawString(Minecraft.getInstance().font,
                    CreateLang.translate("gui.stock_keeper.package_adress")
                            .style(ChatFormatting.ITALIC)
                            .component(),
                    addressBox.getX(), addressBox.getY(), 0xff_CDBCA8, false);
        }
        if (isConfirmHovered(pMouseX, pMouseY)) {
            pGuiGraphics.blit(TEXTURE, x + BUTTON_X, y + BUTTON_Y,
                    HOVER_X, HOVER_Y, BUTTON_W, BUTTON_H);
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        boolean lmb = pButton == GLFW.GLFW_MOUSE_BUTTON_LEFT;
        // boolean rmb = pButton == GLFW.GLFW_MOUSE_BUTTON_RIGHT;

        if (lmb && isConfirmHovered((int) pMouseX, (int) pMouseY)) {
            sendPackage();
        }

        if (addressBox.isFocused()) {
            if (addressBox.isHovered())
                return addressBox.mouseClicked(pMouseX, pMouseY, pButton);
            addressBox.setFocused(false);
        }

        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
    }

    private boolean isConfirmHovered(int mouseX, int mouseY) {
        int confirmX = getGuiLeft() + BUTTON_X;
        int confirmY = getGuiTop() + BUTTON_Y;
        int confirmW = BUTTON_W;
        int confirmH = BUTTON_H;

        if (mouseX < confirmX || mouseX >= confirmX + confirmW)
            return false;
        return mouseY >= confirmY && mouseY < confirmY + confirmH;
    }
}
