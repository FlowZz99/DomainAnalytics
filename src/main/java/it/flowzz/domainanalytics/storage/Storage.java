package it.flowzz.domainanalytics.storage;

import java.util.HashMap;

public interface Storage {

    void save(HashMap<String,Integer> cache);
    void load(HashMap<String,Integer> cache);
}
