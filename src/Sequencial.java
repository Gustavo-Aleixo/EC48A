public class Sequencial {

  private int[][] graph;
  private int n;
  private int minPathCost = Integer.MAX_VALUE;

  public Sequencial(int[][] graph) {
    this.graph = graph;
    this.n = graph.length;
  }

  /**
  * Método que inicia a resolução do problema de forma sequencial. 
  * Mede o tempo de execução, encontra o menor custo do caminho e exibe os resultados.
  */
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


  /**
  * Método recursivo que utiliza backtracking para explorar todos os caminhos possíveis no grafo.
  * @param currentCost Custo acumulado do caminho atual.
  * @param currentNode Nó atual no grafo.
  * @param depth Número de nós visitados até o momento.
  * @param visited Vetor que rastreia os nós visitados para evitar ciclos.
  */
  private void backtrack(int currentCost, int currentNode, int depth, boolean[] visited) {

    // Caso base: se todos os nós foram visitados, calcula o custo de retornar ao nó inicial.
    if (depth == n) {
      currentCost += graph[currentNode][0];
      minPathCost = Math.min(minPathCost, currentCost);
      return;
    }

    // Explora os nós vizinhos do nó atual.
    for (int nextNode = 1; nextNode < n; nextNode++) {
      if (!visited[nextNode]) {
        visited[nextNode] = true;

        // Chamada recursiva: Avança para o próximo nó, acumulando o custo e aumentando a profundidade.
        backtrack(currentCost + graph[currentNode][nextNode], nextNode, depth + 1, visited);
        visited[nextNode] = false;
      }
    }
  }
}
