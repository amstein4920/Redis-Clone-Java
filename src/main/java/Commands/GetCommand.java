package Commands;

import DataStorage.DataStore;
import Utils.RESP;

public class GetCommand implements Command {

    private DataStore store;

    public GetCommand(DataStore store) {
        this.store = store;
    }

    @Override
    public String execute(String[] args) {
        String entity = store.getEntity(args[0]);

        if (entity == null) {
            return RESP.nullBulkString();
        }
        return RESP.bulkString(entity);
    }
}
