package me.athlaeos.lapi.network;

import me.athlaeos.lapi.LoreAPIPlugin;
import org.bukkit.entity.Player;

public class NetworkHandlerImpl implements NetworkHandler {
    @Override
    public PacketStatus writeBefore(Player player, Object packet) {
        LoreAPIPlugin.getNms().readItemMetaPacket(player, packet);
        return PacketStatus.ALLOW;
    }

    @Override
    public void writeAfter(Player player, Object packet) {
        LoreAPIPlugin.getNms().readItemMetaPacket(player, packet);
    }
}
