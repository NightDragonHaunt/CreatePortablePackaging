// com/nightdragon/create_portable_packaging/index/ModPackets.java

package com.nightdragon.create_portable_packaging;

import com.nightdragon.create_portable_packaging.item.PortablePackagerPacket;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.CatnipPacketRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Locale;

public enum ModPackets implements BasePacketPayload.PacketTypeProvider {

    // C2S (Client to Server)
    PORTABLE_PACKAGER_SEND(PortablePackagerPacket.class, PortablePackagerPacket.STREAM_CODEC),
    ;

    private final CatnipPacketRegistry.PacketType<?> type;

    <T extends BasePacketPayload> ModPackets(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        String packetName = this.name().toLowerCase(Locale.ROOT);
        this.type = new CatnipPacketRegistry.PacketType<>(
                new CustomPacketPayload.Type<>(CreatePortablePackaging.asResource(packetName)),
                clazz,
                codec);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends CustomPacketPayload> CustomPacketPayload.Type<T> getType() {
        return (CustomPacketPayload.Type<T>) this.type.type();
    }

    public static void register() {
        CatnipPacketRegistry packetRegistry = new CatnipPacketRegistry(CreatePortablePackaging.MODID, 1);

        for (ModPackets packet : ModPackets.values()) {
            packetRegistry.registerPacket(packet.type);
        }

        packetRegistry.registerAllPackets();
    }

    public static void sendToServer(BasePacketPayload packet) {
        PacketDistributor.sendToServer(packet);
    }
}
