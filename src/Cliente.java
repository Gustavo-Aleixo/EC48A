import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Cliente {

  private String host;
  private int port;
  private int numberOfThreads;

  public Cliente(String host, int port, int numberOfThreads) {
    this.host = host;
    this.port = port;
    this.numberOfThreads = numberOfThreads;
  }

  public void start() {
    long startTime = System.nanoTime();

    try (Socket socket = new Socket(host, port);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

      int[][] graph = (int[][]) in.readObject();
      int result = calculateShortestPathParallel(graph);
      out.writeInt(result);
      out.flush();

      long endTime = System.nanoTime();
      double elapsedTime = (endTime - startTime) / 1_000_000_000.0;
      System.out.println("Resultado enviado ao servidor: " + result);
      System.out.printf("Tempo de processamento (cliente): %.2f segundos\n", elapsedTime);
      System.out.printf("===================================================\n\n");

    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  private int calculateShortestPathParallel(int[][] graph) {
    AtomicInteger minPathCost = new AtomicInteger(Integer.MAX_VALUE);
    int n = graph.length;

    ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

    for (int i = 1; i < n; i++) {
      final int startNode = i;
      executor.submit(() -> {
        boolean[] visited = new boolean[n];
        visited[0] = true;
        visited[startNode] = true;
        backtrack(graph, graph[0][startNode], startNode, 2, minPathCost, visited);
      });
    }

    executor.shutdown();
    try {
      executor.awaitTermination(Long.MAX_VALUE, java.util.concurrent.TimeUnit.NANOSECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    return minPathCost.get();
  }

  private void backtrack(int[][] graph, int currentCost, int currentNode, int depth,AtomicInteger minPathCost, boolean[] visited) {

    int n = graph.length;
    if (depth == n) {
      currentCost += graph[currentNode][0];
      updateMinPathCost(currentCost, minPathCost);
      return;
    }

    for (int nextNode = 1; nextNode < n; nextNode++) {
      if (!visited[nextNode]) {
        visited[nextNode] = true;
        backtrack(graph, currentCost + graph[currentNode][nextNode], nextNode, depth + 1, minPathCost, visited);
        visited[nextNode] = false;
      }
    }
  }

  private void updateMinPathCost(int cost, AtomicInteger minPathCost) {
    int currentMin;
    do {
      currentMin = minPathCost.get();
    } while (cost < currentMin && !minPathCost.compareAndSet(currentMin, cost));
  }
}
