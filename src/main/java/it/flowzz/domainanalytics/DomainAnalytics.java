package it.flowzz.domainanalytics;

import it.flowzz.domainanalytics.commands.DomainAnalyticsCmd;
import it.flowzz.domainanalytics.listeners.PlayerLoginEventListener;
import it.flowzz.domainanalytics.storage.Storage;
import it.flowzz.domainanalytics.storage.StorageType;
import it.flowzz.domainanalytics.storage.impl.StorageFlat;
import it.flowzz.domainanalytics.storage.impl.StorageMySql;
import it.flowzz.domainanalytics.utils.ConfigUtils;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

import java.util.HashMap;

@Getter
public class DomainAnalytics extends Plugin {

    private Storage storage;
    private Configuration config;
    private HashMap<String,Integer> joinCache;

    @Override
    public void onEnable() {
        joinCache = new HashMap<>();
        setupConfig();
        setupStorage();
        registerListeners();
        registerCommands();

    }

    @Override
    public void onDisable() {
        storage.save(joinCache);
        getProxy().getPluginManager().unregisterListeners(this);
        getProxy().getScheduler().cancel(this);
        joinCache.clear();
    }

    private void setupStorage() {
        StorageType type = StorageType.valueOf(config.getString("Storage"));
        switch(type){
            case FLAT:
                storage = new StorageFlat(this,"data.yml");
                break;
            case MYSQL:
                storage = new StorageMySql(this);
                break;
        }
        storage.load(joinCache);
    }

    private void registerListeners() {
        new PlayerLoginEventListener(this);
    }

    private void registerCommands() { getProxy().getPluginManager().registerCommand(this, new DomainAnalyticsCmd(this)); }

    private void setupConfig() {
            this.config = ConfigUtils.saveDefaultConfig(this,"config.yml");
    }
}
