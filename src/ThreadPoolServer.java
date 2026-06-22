import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Version 3: Multi-threaded server with a fixed thread pool.
 * Threads are created once at startup and reused across requests,
 * eliminating per-request creation overhead. The pool size caps
 * concurrency, keeping resource usage predictable under any load.
 *
 * Pool size is set larger than core count because handlers spend most
 * of their time on blocking file I/O, not CPU work.
 */
public class ThreadPoolServer {
    static final int PORT = 8003;
    static final String DATA_FILE = "data.txt";
    static final int POOL_SIZE = 20;

    public static void main(String[] args) throws IOException {
        System.out.println("Thread-pool server (pool=" + POOL_SIZE + ") listening on port " + PORT);
        ExecutorService pool = Executors.newFixedThreadPool(POOL_SIZE);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket client = serverSocket.accept();
                pool.submit(new ClientHandler(client, DATA_FILE));
            }
        } finally {
            pool.shutdown();
        }
    }
}
