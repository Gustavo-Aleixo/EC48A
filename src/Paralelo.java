import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Paralelo {
  private int[][] graph;
  private int n;
  private AtomicInteger minPathCost;
  private int numberOfThreads;

  public Paralelo(int[][] graph, int numberOfThreads) {
    this.graph = graph;
    this.n = graph.length;
    this.numberOfThreads = numberOfThreads;
    this.minPathCost = new AtomicInteger(Integer.MAX_VALUE);
  }

  public void solve() {
    long startTime = System.nanoTime();

    ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

    for (int i = 1; i < n; i++) {
      final int startNode = i;
      executor.submit(() -> {
        boolean[] visited = new boolean[n];
        visited[0] = true;
        visited[startNode] = true;
        backtrack(0, startNode, 1, visited);
      });
    }

    executor.shutdown();
    try {
      executor.awaitTermination(Long.MAX_VALUE, java.util.concurrent.TimeUnit.NANOSECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    long endTime = System.nanoTime();
    double elapsedTime = (endTime - startTime) / 1_000_000_000.0;
    System.out.println("Menor custo do caminho (paralelo): " + minPathCost.get());
    System.out.printf("Tempo de processamento (paralelo): %.2f segundos\n", elapsedTime);
    System.out.printf("===================================================\n\n");
  }

  private void backtrack(int currentCost, int currentNode, int depth, boolean[] visited) {
    if (depth == n - 1) {
      currentCost += graph[currentNode][0];
      updateMinPathCost(currentCost);
      return;
    }

    for (int nextNode = 1; nextNode < n; nextNode++) {
      if (!visited[nextNode]) {
        visited[nextNode] = true;
        backtrack(currentCost + graph[currentNode][nextNode], nextNode, depth + 1, visited);
        visited[nextNode] = false;
      }
    }
  }

  private void updateMinPathCost(int cost) {
    int currentMin;
    do {
      currentMin = minPathCost.get();
    } while (cost < currentMin && !minPathCost.compareAndSet(currentMin, cost));
  }
}
