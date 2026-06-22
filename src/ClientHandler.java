import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final String dataFilePath;

    public ClientHandler(Socket socket, String dataFilePath) {
        this.socket = socket;
        this.dataFilePath = dataFilePath;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String request = in.readLine();
            if ("READ".equals(request)) {
                try (BufferedReader fileReader = new BufferedReader(new FileReader(dataFilePath))) {
                    String line;
                    while ((line = fileReader.readLine()) != null) {
                        out.println(line);
                    }
                } catch (IOException e) {
                    out.println("ERROR: " + e.getMessage());
                }
                out.println("END");
                out.flush();
            }
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }
}
