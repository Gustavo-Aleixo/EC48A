import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Servidor {

  private static final int PORT = 12345;
  private int[][] graph;
  private AtomicInteger minPathCost = new AtomicInteger(Integer.MAX_VALUE);

  public Servidor(int[][] graph) {
    this.graph = graph;
  }

  public void start() {
    try (ServerSocket serverSocket = new ServerSocket(PORT)) {
      Socket clientSocket = serverSocket.accept();
      processClient(clientSocket);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void processClient(Socket clientSocket) {
    new Thread(() -> {
      try (ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
          ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {

        out.writeObject(graph);
        out.flush();

        int clientResult = in.readInt();
        minPathCost.set(clientResult);
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          clientSocket.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }).start();
  }
}
