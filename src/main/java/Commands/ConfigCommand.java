package Commands;

import java.util.Arrays;

import Configuration.Config;

public class ConfigCommand implements Command {

    @Override
    public String execute(String[] args) {
        Config config = Config.getInstance();

        switch (args[0].toUpperCase()) {
            case "GET":
                return get(args, config);
        }

        return "*0\r\n";
    }

    private String get(String[] args, Config config) {
        String value;
        for (String arg : Arrays.copyOfRange(args, 1, args.length)) {
            switch (arg.toLowerCase()) {
                case "dir":
                    value = config.getDir().toString();
                    return String.format("*2\r\n$3\r\ndir\r\n$%d\r\n%s\r\n", value.length(), value);
                case "dbfilename":
                    value = config.getDbFileName().toString();
                    return String.format("*2\r\n$10\r\ndbfilename\r\n$%d\r\n%s\r\n", value.length(), value);
            }
        }
        return "*0\r\n";
    }
}
