import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
* Método que inicia a resolução do problema de forma paralela.
* Divide o trabalho entre múltiplas threads para explorar diferentes caminhos simultaneamente e determinar o menor custo.
*/
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

  /**
  * Método que inicia a resolução do problema de forma paralela.
  * Divide o trabalho inicial entre múltiplas threads, cada uma começando de um nó diferente.
  * Mede o tempo de execução, encontra o menor custo do caminho e exibe os resultados.
  */
  public void solve() {
    long startTime = System.nanoTime();
    ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

    for (int i = 1; i < n; i++) {
      final int startNode = i;

      executor.submit(() -> {
        boolean[] visited = new boolean[n];
        visited[0] = true;
        visited[startNode] = true;
        backtrack(graph[0][startNode], startNode, 2, visited);
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
      updateMinPathCost(currentCost);
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

  private void updateMinPathCost(int cost) {
    int currentMin;
    do {
      currentMin = minPathCost.get();
    } while (cost < currentMin && !minPathCost.compareAndSet(currentMin, cost));
  }
}
