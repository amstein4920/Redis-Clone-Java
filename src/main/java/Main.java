import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import Commands.*;

public class Main {

    private static HashMap<String, Command> registry = new HashMap<>();

    public static void main(String[] args) {
        int port = 6379;
        DataStore store = new DataStore();

        // Populate command registry before doing anything else
        registry.put("PING", new PingCommand());
        registry.put("ECHO", new EchoCommand());
        registry.put("SET", new SetCommand(store));
        registry.put("GET", new GetCommand(store));

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
                int argsCount = Integer.parseInt(firstInput.substring(1)) - 1;
                final String[] args = new String[argsCount];

                // Eat the size input
                inputStream.readLine();

                Command command = registry.get(inputStream.readLine());

                for (int i = 0; i < args.length; i++) {
                    // Eat each size input
                    inputStream.readLine();
                    args[i] = inputStream.readLine();
                }

                outputStream.write(command.execute(args).getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}