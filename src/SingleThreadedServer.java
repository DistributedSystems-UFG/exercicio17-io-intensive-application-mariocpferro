import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Version 1: Single-threaded server.
 * Accepts and fully processes one connection before accepting the next.
 * All other clients block at accept() until the current request finishes.
 */
public class SingleThreadedServer {
    static final int PORT = 8001;
    static final String DATA_FILE = "data.txt";

    public static void main(String[] args) throws IOException {
        System.out.println("Single-threaded server listening on port " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket client = serverSocket.accept();
                // Handle the request inline — the next accept() only runs after this returns
                new ClientHandler(client, DATA_FILE).run();
            }
        }
    }
}
