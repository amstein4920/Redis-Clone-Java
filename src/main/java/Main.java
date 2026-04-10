import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        int port = 6379;
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
                        outputStream.write("+PONG\r\n".getBytes());
                    } else if ("*2".equals(firstInput)) {
                        // Eat the size input
                        inputStream.readLine();

                        // Don't need now and will probably end up as a Command object of some sort, but
                        // just saving as String until I know more of what that object looks like
                        String command = inputStream.readLine();

                        // Eat the size input
                        inputStream.readLine();

                        String echoInput = inputStream.readLine();
                        String echoOutput = String.format("$%d\r\n%s\r\n", echoInput.length(), echoInput);
                        outputStream.write(echoOutput.getBytes());

                    }
                } else {
                    break;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}