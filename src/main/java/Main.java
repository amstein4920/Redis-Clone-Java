import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.util.HashMap;

import Commands.*;
import Configuration.Config;
import Configuration.Config.Builder;
import DataStorage.DataStore;
import DataStorage.RdbLoader;
import Utils.RESP;

public class Main {

    private static final HashMap<String, Command> registry = new HashMap<>();

    public static void main(String[] args) {
        // Default port
        int port = 6379;

        Config.Builder builder = new Builder();
        for (int i = 0; i < args.length; i++) {
            switch (args[i].toLowerCase()) {
                case "--dir":
                    i++;
                    builder.setDir(args[i]);
                    break;
                case "--dbfilename":
                    i++;
                    builder.setDbFileName(args[i]);
                    break;
                case "--port":
                    i++;
                    port = Integer.parseInt(args[i]);
                    break;
            }
        }
        Config.initialize(builder);
        Config config = Config.getInstance();

        DataStore store = new DataStore();

        if (config.getDir() != null && config.getDbFileName() != null) {
            RdbLoader loader = new RdbLoader();
            Path path = config.getDir().resolve(config.getDbFileName());
            try (PushbackInputStream stream = new PushbackInputStream(new FileInputStream(path.toFile()))) {
                loader.load(stream, store);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Populate command registry before doing anything else
        registry.put("PING", new PingCommand());
        registry.put("ECHO", new EchoCommand());
        registry.put("SET", new SetCommand(store));
        registry.put("GET", new GetCommand(store));
        registry.put("CONFIG", new ConfigCommand());
        registry.put("KEYS", new KeysCommand(store));

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            // Since the tester restarts the program quite often, setting ReuseAddress
            // ensures that we don't run into 'Address already in use' errors
            serverSocket.setReuseAddress(true);
            // Wait for connection from client.
            while (true) {
                Socket clientSocket = serverSocket.accept();

                new Thread(() -> handleConnection(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    private static void handleConnection(Socket clientSocket) {
        try (Socket socket = clientSocket) {
            OutputStream outputStream = socket.getOutputStream();
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {

                String firstInput = inputStream.readLine();
                if (firstInput == null) {
                    // No more inputs, end loop
                    break;
                }

                try {
                    if (!firstInput.startsWith("*")) {
                        outputStream.write(RESP.simpleErrorString("ERR invalid request format").getBytes());
                        outputStream.flush();
                        continue;
                    }

                    int argsCount = Integer.parseInt(firstInput.substring(1)) - 1;
                    final String[] args = new String[argsCount];

                    // Eat the size input
                    inputStream.readLine();

                    Command command = registry.get(inputStream.readLine().toUpperCase());
                    if (command == null) {
                        outputStream.write(RESP.simpleErrorString("ERR unknown command").getBytes());
                        outputStream.flush();
                        continue;
                    }

                    for (int i = 0; i < args.length; i++) {
                        // Eat each size input
                        inputStream.readLine();
                        args[i] = inputStream.readLine();
                    }

                    outputStream.write(command.execute(args).getBytes());
                    outputStream.flush();
                } catch (Exception e) {
                    outputStream.write(RESP.simpleErrorString("ERR server error").getBytes());
                    outputStream.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}