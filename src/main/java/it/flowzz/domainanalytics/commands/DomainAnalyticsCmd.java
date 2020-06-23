package it.flowzz.domainanalytics.commands;

import it.flowzz.domainanalytics.DomainAnalytics;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class DomainAnalyticsCmd extends Command {

    private DomainAnalytics plugin;

    public DomainAnalyticsCmd(DomainAnalytics plugin) {
        super("domainanalytics", "domainanalytics.command.use", "da");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if(strings.length > 0){
            //Maybe i'll add some other commands in the future
            switch (strings[0].toLowerCase()){
                case "list":
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&m<-------------------------->"));
                    float total = plugin.getJoinCache().keySet().stream().mapToInt(domain -> plugin.getJoinCache().getOrDefault(domain, 0)).sum();
                    for(String domain : plugin.getJoinCache().keySet()){
                        float relative = plugin.getJoinCache().get(domain) / total;
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7» " +
                                domain.replace("_",".") + ": &e" + plugin.getJoinCache().get(domain)
                                + " &8(&e" + String.format("%.02f", relative * 100) + "%&8)"));
                    }
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
        commandSender.sendMessage(ChatColor.RED + "Wrong syntax. use /da <list>");
    }
}
