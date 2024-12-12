import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Cliente {
  private String host;
  private int port;
  private int startNode;
  private int[][] graph;

  public Cliente(String host, int port, int startNode) {
    this.host = host;
    this.port = port;
    this.startNode = startNode;
  }

  public void start() {
    try (Socket socket = new Socket(host, port);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

      graph = (int[][]) in.readObject();
      int result = calculateShortestPathFromNode(graph, startNode);
      out.writeInt(result);
      out.flush();
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  private int calculateShortestPathFromNode(int[][] graph, int startNode) {
    AtomicInteger minPathCost = new AtomicInteger(Integer.MAX_VALUE);
    boolean[] visited = new boolean[graph.length];
    visited[0] = true;
    visited[startNode] = true;
    backtrack(graph, graph[0][startNode], startNode, 2, minPathCost, visited);
    return minPathCost.get();
  }

  private void backtrack(int[][] graph, int currentCost, int currentNode, int depth, AtomicInteger minPathCost,
      boolean[] visited) {
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
