import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

public class Servidor {
  private static final int PORT = 12345;
  private int[][] graph;
  private AtomicInteger minPathCost = new AtomicInteger(Integer.MAX_VALUE);
  private ConcurrentLinkedQueue<Integer> resultsQueue = new ConcurrentLinkedQueue<>();
  private volatile boolean running = true;
  private ServerSocket serverSocket;
  private int threads;

  public Servidor(int[][] graph, int threads) {
    this.graph = graph;
    this.threads = threads;
  }

  public void start(CountDownLatch latch) {
    long startTime = System.nanoTime();
    try {
      serverSocket = new ServerSocket(PORT);
      while (running) {
        Socket clientSocket = serverSocket.accept();
        processClient(clientSocket, startTime, latch);
      }
    } catch (IOException e) {
    }
  }

  public void stop() {
    running = false;
    try {
      if (serverSocket != null && !serverSocket.isClosed()) {
        serverSocket.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void processClient(Socket clientSocket, long startTime, CountDownLatch latch) {
    new Thread(() -> {
      try (ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
          ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {

        out.writeObject(graph);
        out.flush();
        int clientResult = in.readInt();
        resultsQueue.add(clientResult);

        if (resultsQueue.size() == threads - 1) {
          combineResults(startTime, latch);
        }
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

  private void combineResults(long startTime, CountDownLatch latch) {
    for (int result : resultsQueue) {
      if (result < minPathCost.get()) {
        minPathCost.set(result);
      }
    }
    long endTime = System.nanoTime();
    double elapsedTime = (endTime - startTime) / 1_000_000_000.0;
    System.out.println("Menor custo do caminho (distribuido): " + minPathCost.get());
    System.out.printf("Tempo de processamento (distribuido): %.2f segundos\n", elapsedTime);
    System.out.printf("===================================================\n\n");
    latch.countDown();
    stop();
  }

}
