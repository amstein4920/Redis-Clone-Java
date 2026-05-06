package Commands;

import java.util.Arrays;

import Configuration.Config;
import Utils.RESP;

public class ConfigCommand implements Command {

    @Override
    public String execute(String[] args) {
        Config config = Config.getInstance();

        switch (args[0].toUpperCase()) {
            case "GET":
                return get(args, config);
        }

        return RESP.bulkString("");
    }

    private String get(String[] args, Config config) {
        String value;
        for (String arg : Arrays.copyOfRange(args, 1, args.length)) {
            switch (arg.toLowerCase()) {
                case "dir":
                    try {
                        value = config.getDir().toString();
                        return RESP.array("dir", value);
                    } catch (IllegalStateException e) {
                        // Value not configured, return empty
                    }
                case "dbfilename":
                    try {
                        value = config.getDbFileName().toString();
                        return RESP.array("dbfilename", value);
                    } catch (IllegalStateException e) {
                        // Value not configured, return empty
                    }
            }
        }
        return RESP.bulkString("");
    }
}
