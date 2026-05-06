package Commands;

import java.time.Instant;

import DataStorage.DataStore;
import DataStorage.ValueEntry;
import Utils.RESP;

public class SetCommand implements Command {
    private DataStore store;

    public SetCommand(DataStore store) {
        this.store = store;
    }

    @Override
    public String execute(String[] args) {
        String key = args[0];
        ValueEntry.Builder entryBuilder = ValueEntry.builder(args[1]);

        for (int i = 2; i < args.length - 1; i += 2) {
            String flag = args[i].toUpperCase();

            // Currently only PX flag is available
            switch (flag) {
                case "PX":
                    entryBuilder.expiry(Instant.now().plusMillis(Long.parseLong(args[i + 1])));
                    break;
            }
        }

        store.setEntity(key, entryBuilder.build());
        return RESP.simpleString("OK");
    }

}
