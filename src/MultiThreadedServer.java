import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Version 2: Multi-threaded server — one new thread per request.
 * Each accepted connection spawns a fresh Thread. The main loop returns
 * to accept() immediately, so connections are handled concurrently.
 * Downside: thread creation overhead on every request; no upper bound
 * on simultaneous threads under high load.
 */
public class MultiThreadedServer {
    static final int PORT = 8002;
    static final String DATA_FILE = "data.txt";

    public static void main(String[] args) throws IOException {
        System.out.println("Multi-threaded (thread-per-request) server listening on port " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket client = serverSocket.accept();
                new Thread(new ClientHandler(client, DATA_FILE)).start();
            }
        }
    }
}
