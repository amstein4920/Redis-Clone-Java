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
                if (firstInput != null) {
                    if ("*1".equals(firstInput)) {
                        // This is just a PING. We don't need to do anything other than clear the buffer
                        // with reads and respond with our PONG
                        inputStream.readLine();
                        inputStream.readLine();

                        Command command = registry.get("PING");

                        outputStream.write(command.execute(null).getBytes());
                    } else if ("*2".equals(firstInput)) {
                        // Eat the size input
                        inputStream.readLine();

                        Command command = registry.get(inputStream.readLine());
                        String[] args = new String[1];

                        switch (command) {
                            case EchoCommand _:
                            case GetCommand _:
                                // Eat the size input then read argument
                                inputStream.readLine();
                                args[0] = inputStream.readLine();
                                break;
                            default:
                        }

                        outputStream.write(command.execute(args).getBytes());

                    } else if ("*3".equals(firstInput)) {
                        // Eat the size input
                        inputStream.readLine();

                        Command command = registry.get(inputStream.readLine());
                        String[] args = new String[2];

                        switch (command) {
                            case SetCommand _:
                                // Eat the size input
                                inputStream.readLine();
                                args[0] = inputStream.readLine();
                                inputStream.readLine();
                                args[1] = inputStream.readLine();

                                break;
                            default:
                        }

                        outputStream.write(command.execute(args).getBytes());
                    } else {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}