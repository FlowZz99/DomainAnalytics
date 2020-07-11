package it.flowzz.domainanalytics.listeners;

import it.flowzz.domainanalytics.DomainAnalytics;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.TimeUnit;

public class PlayerLoginEventListener implements Listener {

    private DomainAnalytics plugin;

    public PlayerLoginEventListener(DomainAnalytics domainAnalytics) {
        plugin = domainAnalytics;
        plugin.getProxy().getPluginManager().registerListener(domainAnalytics,this);
        plugin.getProxy().getScheduler().schedule(plugin, () ->{
            plugin.getStorage().save(plugin.getJoinCache());
        }, 15, plugin.getConfig().getLong("save-interval"), TimeUnit.SECONDS);

    }

    @EventHandler
    public void onPreLogin(PreLoginEvent event){
        if(plugin.getConfig().getStringList("blacklisted-hostnames").contains(event.getConnection().getVirtualHost().getHostName().toLowerCase())){
            event.setCancelReason(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',plugin.getLang().getString("deny-access"))));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onLoginEvent(PostLoginEvent event){
        if(event.getPlayer().getPendingConnection().getVirtualHost().getHostName() == null) return;
        String hostNoDots = event.getPlayer().getPendingConnection().getVirtualHost().getHostName().toLowerCase().replace(".", "_");
        plugin.getJoinCache().put(hostNoDots, plugin.getJoinCache().getOrDefault(hostNoDots,0) + 1);
    }

}
