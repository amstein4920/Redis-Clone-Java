package DataStorage;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DataStore {
    private final Map<String, ValueEntry> store;

    public DataStore() {
        this.store = new HashMap<>();
    }

    public DataStore(int size) {
        int actualCapacity = (int) ((size / 0.75) + 1);
        this.store = new HashMap<>(actualCapacity);
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
        return store.keySet();
    }
}