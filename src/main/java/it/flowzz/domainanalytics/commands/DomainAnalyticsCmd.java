package it.flowzz.domainanalytics.commands;

import it.flowzz.domainanalytics.DomainAnalytics;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.HashMap;

public class DomainAnalyticsCmd extends Command {

    private DomainAnalytics plugin;

    public DomainAnalyticsCmd(DomainAnalytics plugin) {
        super("domainanalytics", "domainanalytics.command.use", "da");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if(strings.length == 2){
            if(!strings[0].equalsIgnoreCase("list")){
                sendHelp(commandSender);
                return;
            }
            //Maybe i'll add some other commands in the future
            switch (strings[1].toLowerCase()){
                case "total":
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&m<-------------------------->"));
                    float total = plugin.getJoinCache().keySet().stream().mapToInt(domain -> plugin.getJoinCache().getOrDefault(domain, 0)).sum();
                    plugin.getJoinCache().keySet().forEach(domain -> {
                        float relative = plugin.getJoinCache().get(domain) / total;
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7» " +
                                domain.replace("_", ".") + ": &e" + plugin.getJoinCache().get(domain)
                                + " &8(&e" + String.format("%.02f", relative * 100) + "%&8)"));
                    });
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7» Total connections: &e" + Math.round(total)));
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&m<-------------------------->"));
                    break;
                case "now":
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&m<-------------------------->"));
                    HashMap<String,Integer> joinCacheNow = new HashMap<>();
                    for(ProxiedPlayer player : plugin.getProxy().getPlayers()){
                        String hostName = player.getPendingConnection().getVirtualHost().getHostName().toLowerCase();
                        joinCacheNow.put(hostName, joinCacheNow.getOrDefault(hostName,0) + 1);
                    }
                    joinCacheNow.keySet().forEach(domain -> {
                        float relative = joinCacheNow.get(domain) / (float)plugin.getProxy().getPlayers().size();
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7» " +
                                domain + ": &e" + joinCacheNow.get(domain)
                                + " &8(&e" + String.format("%.02f", relative * 100) + "%&8)"));
                    });
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7» Total online: &e" + plugin.getProxy().getPlayers().size()));
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&m<-------------------------->"));
                    break;
                default:
                    sendHelp(commandSender);
                    break;
            }
        }else{
            sendHelp(commandSender);
        }

    }

    private void sendHelp(CommandSender commandSender) {
        commandSender.sendMessage(ChatColor.RED + "Wrong syntax. use /da list <total|now>");
    }
}
