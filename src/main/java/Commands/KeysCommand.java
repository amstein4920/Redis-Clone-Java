package Commands;

import DataStorage.DataStore;
import Utils.RESP;

public class KeysCommand implements Command {
    private DataStore store;

    public KeysCommand(DataStore store) {
        this.store = store;
    }

    @Override
    public String execute(String[] args) {
        return RESP.array(store.getKeys().toArray(String[]::new));
    }

}
