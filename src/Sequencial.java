public class Sequencial {

  private int[][] graph;
  private int n;
  private int minPathCost = Integer.MAX_VALUE;

  public Sequencial(int[][] graph) {
    this.graph = graph;
    this.n = graph.length;
  }

  public void solve() {
    long startTime = System.nanoTime();
    boolean[] visited = new boolean[n];
    visited[0] = true;

    backtrack(0, 0, 1, visited);

    long endTime = System.nanoTime();
    double elapsedTime = (endTime - startTime) / 1_000_000_000.0;

    System.out.println("Menor custo do caminho (sequencial): " + minPathCost);
    System.out.printf("Tempo de processamento (sequencial): %.2f segundos\n", elapsedTime);
    System.out.printf("===================================================\n\n");
  }

  private void backtrack(int currentCost, int currentNode, int depth, boolean[] visited) {
    if (depth == n) {
      currentCost += graph[currentNode][0];
      minPathCost = Math.min(minPathCost, currentCost);
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
}
