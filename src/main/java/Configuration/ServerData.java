package Configuration;

import java.util.HashMap;
import java.util.Map;

public class ServerData {

    private final Map<String, String> store;

    public ServerData() {
        this.store = new HashMap<>();
    }

    public synchronized void setEntity(String key, String value) {
        store.put(key, value);
    }

    public synchronized String getEntity(String key) {
        String value = store.get(key);
        return value;
    }
}
