package Commands;

import java.util.HashMap;
import java.util.Map;

public class DataStore {
    private final Map<String, String> store = new HashMap<>();

    public synchronized void setEntity(String key, String value) {
        store.put(key, value);
    }

    public synchronized String getEntity(String key) {
        return store.get(key);
    }
}
