package it.flowzz.domainanalytics.storage.impl;

import it.flowzz.domainanalytics.DomainAnalytics;
import it.flowzz.domainanalytics.storage.Storage;
import it.flowzz.domainanalytics.utils.ConfigUtils;
import net.md_5.bungee.config.Configuration;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class StorageFlat implements Storage {
    private DomainAnalytics plugin;
    private Configuration data;

    public StorageFlat(DomainAnalytics plugin, String fileName) {
        this.plugin = plugin;
        data = ConfigUtils.saveDefaultConfig(plugin,fileName);
    }

    @Override
    public void save(HashMap<String,Integer> cache) {
        for(String domain : cache.keySet()){
            data.set(domain, cache.getOrDefault(domain,0));
        }
        plugin.getProxy().getScheduler().schedule(plugin, () -> {
            ConfigUtils.saveAndReloadConfig(data,plugin,"data.yml");
        },5, TimeUnit.SECONDS);
    }

    @Override
    public void load(HashMap<String, Integer> cache) {
        for(String key : data.getKeys()){
            cache.put(key, data.getInt(key, 0));
        }
    }
}
