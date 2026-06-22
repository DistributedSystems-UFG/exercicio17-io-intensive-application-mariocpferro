import java.io.*;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * Benchmark client: fires NUM_REQUESTS READ requests with up to
 * CONCURRENT_CLIENTS in flight simultaneously and prints throughput.
 *
 * Usage: java -cp out BenchmarkClient <host> <port>
 * Example: java -cp out BenchmarkClient localhost 8001
 */
public class BenchmarkClient {
    static final int NUM_REQUESTS = 500;
    static final int CONCURRENT_CLIENTS = 20;

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: java -cp out BenchmarkClient <host> <port>");
            System.exit(1);
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]);

        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger failed = new AtomicInteger(0);
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_CLIENTS);
        CountDownLatch latch = new CountDownLatch(NUM_REQUESTS);

        long start = System.currentTimeMillis();

        for (int i = 0; i < NUM_REQUESTS; i++) {
            executor.submit(() -> {
                try {
                    sendReadRequest(host, port);
                    success.incrementAndGet();
                } catch (Exception e) {
                    failed.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        long elapsed = System.currentTimeMillis() - start;
        executor.shutdown();

        System.out.printf("Host: %s  Port: %d%n", host, port);
        System.out.printf("Requests: %d  |  Success: %d  |  Failed: %d%n",
                NUM_REQUESTS, success.get(), failed.get());
        System.out.printf("Elapsed: %d ms%n", elapsed);
        System.out.printf("Throughput: %.2f req/s%n", (success.get() * 1000.0) / elapsed);
    }

    private static void sendReadRequest(String host, int port) throws IOException {
        try (
            Socket socket = new Socket(host, port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            out.println("READ");
            String line;
            while ((line = in.readLine()) != null) {
                if ("END".equals(line)) break;
            }
        }
    }
}
