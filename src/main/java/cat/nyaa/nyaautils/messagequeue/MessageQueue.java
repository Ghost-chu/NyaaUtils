package cat.nyaa.nyaautils.messagequeue;

import cat.nyaa.nyaacore.Message;
import cat.nyaa.nyaacore.component.IMessageQueue;
import cat.nyaa.nyaautils.I18n;
import cat.nyaa.nyaautils.NyaaUtils;
//import net.ess3.api.events.AfkStatusChangeEvent;
import com.Zrips.CMI.events.CMIAfkEnterEvent;
import com.Zrips.CMI.events.CMIAfkLeaveEvent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.text.DateFormat;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class MessageQueue implements IMessageQueue, Listener {

    final private NyaaUtils plugin;
    private Map<UUID, String> messages;

    public MessageQueue(NyaaUtils pl) {
        plugin = pl;
        plugin.getServer().getPluginManager().registerEvents(this, pl);
        messages = new HashMap<>();
    }

    @Override
    public void send(OfflinePlayer player, Message message, long timestamp) {
        if (!plugin.cfg.message_queue_enable) return;
        UUID uuid = player.getUniqueId();
        String msg = messages.get(uuid);
        String current = timestamp + ":" + message.toString() + "\n";
        msg = msg == null ? current : msg + current;
        messages.put(uuid, msg);
    }

    @Override
    public void send(OfflinePlayer player, Message message) {
        send(player, message, System.currentTimeMillis());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!plugin.cfg.message_queue_enable) return;
        Player player = event.getPlayer();
        sendQueue(player);
    }

    @EventHandler
    public void onPlayerAfkBack(CMIAfkLeaveEvent event) {
        if (!plugin.cfg.message_queue_enable) return;
        Player player = event.getPlayer();
        sendQueue(player);
    }

    private void sendQueue(Player player) {
        UUID uniqueId = player.getUniqueId();
        String msg = messages.remove(uniqueId);
        if (msg == null) return;
        Map<Long, List<String>> map =
                Arrays.stream(msg.split("\n")).map(s -> s.split(":", 2)).collect(Collectors.groupingBy(
                        s -> Long.parseLong(s[0]),
                        Collectors.mapping(s -> s[1], Collectors.toList())
                ));
        map.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey)).forEach(
                p -> {
                    Date time = Date.from(Instant.ofEpochMilli(p.getKey()));
                    p.getValue().forEach(msgJson -> {
                                Message message = new Message("").append(I18n.format("user.mq.deliver", DateFormat.getDateTimeInstance().format(time)), Collections.singletonMap("{message}", ComponentSerializer.parse(msgJson)[0]));
                                message.send(player);
                            }
                    );
                }
        );
    }
}
