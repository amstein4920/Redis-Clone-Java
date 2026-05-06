package DataStorage;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DataStore {
    private final Map<String, ValueEntry> store;

    public DataStore() {
        this.store = new HashMap<>();
    }

    public synchronized void setEntity(String key, ValueEntry entry) {
        store.put(key, entry);
    }

    public synchronized String getEntity(String key) {
        ValueEntry entry = store.get(key);
        if (entry == null) {
            return null;
        }

        if (entry.getExpiry() != null && entry.getExpiry().compareTo(Instant.now()) <= 0) {
            store.remove(key);
            return null;
        }

        return entry.getValue();
    }

    public synchronized Set<String> getKeys() {
        return new HashSet<>(store.keySet());
    }
}