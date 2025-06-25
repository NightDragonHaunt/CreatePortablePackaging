package com.nightdragon.create_portable_packaging.item;

import com.nightdragon.create_portable_packaging.ModPackets;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class PortablePackagerPacket implements ServerboundPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, PortablePackagerPacket> STREAM_CODEC = StreamCodec
            .composite(
                    ByteBufCodecs.STRING_UTF8, PortablePackagerPacket::getAddress,
                    PortablePackagerPacket::new);

    private final String address;

    public PortablePackagerPacket(String address) {
        this.address = address;
    }

    public String getAddress() {
        return this.address;
    }

    @NotNull
    @Override
    public PacketTypeProvider getTypeProvider() {
        return ModPackets.PORTABLE_PACKAGER_SEND;
    }

    @Override
    public void handle(ServerPlayer player) {
        player.server.execute(() -> {
            if (player.containerMenu instanceof PortablePackagerMenu menu) {
                menu.createAndGivePackage(this.address);
            }
        });
    }
}
