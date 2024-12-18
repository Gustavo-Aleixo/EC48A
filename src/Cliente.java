import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
* Classe Cliente que implementa parte a solução distribuída.
* O cliente se conecta ao servidor para receber o grafo e calcular o caminho mais curto a partir de um nó inicial.
*/
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


  /**
  * Inicia a comunicação com o servidor.
  * O cliente se conecta ao servidor, recebe o grafo e envia o resultado do cálculo do caminho mais curto.
  */
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


  /**
  * Calcula o caminho mais curto a partir de um nó inicial utilizando a técnica de backtracking.
  * @param graph     O grafo representado como uma matriz de adjacência.
  * @param startNode O nó inicial a partir do qual o cálculo será realizado.
  * @return O custo do menor caminho encontrado.
  */
  private int calculateShortestPathFromNode(int[][] graph, int startNode) {
    AtomicInteger minPathCost = new AtomicInteger(Integer.MAX_VALUE);
    boolean[] visited = new boolean[graph.length];
    visited[0] = true;
    visited[startNode] = true;
    backtrack(graph, graph[0][startNode], startNode, 2, minPathCost, visited);
    return minPathCost.get();
  }


  /**
  * Realiza o backtracking para calcular o caminho mais curto.
  * @param graph        O grafo representado como uma matriz de adjacência.
  * @param currentCost O custo acumulado até o nó atual.
  * @param currentNode O nó atual sendo processado.
  * @param depth       A profundidade atual da busca.
  * @param minPathCost O menor custo encontrado até o momento.
  * @param visited      Marca os nós visitados.
  */
  private void backtrack(int[][] graph, int currentCost, int currentNode, int depth, AtomicInteger minPathCost, boolean[] visited) {
    
    int n = graph.length;

    // Caso base: quando todos os nós foram visitados, calcula o custo total retornando ao nó inicial
    if (depth == n) {
      currentCost += graph[currentNode][0];
      updateMinPathCost(currentCost, minPathCost);
      return;
    }

    // Percorre os nós restantes para explorar os caminhos possíveis
    for (int nextNode = 1; nextNode < n; nextNode++) {
      if (!visited[nextNode]) {
        visited[nextNode] = true;

        // Recursivamente tenta todos os caminhos possíveis
        backtrack(graph, currentCost + graph[currentNode][nextNode], nextNode, depth + 1, minPathCost, visited);
        visited[nextNode] = false;
      }
    }
  }


  /**
  * Atualiza o menor custo encontrado utilizando uma operação atômica.
  * @param cost        O custo atual a ser comparado.
  * @param minPathCost O menor custo encontrado até o momento.
  */
  private void updateMinPathCost(int cost, AtomicInteger minPathCost) {
    int currentMin;
    do {
      currentMin = minPathCost.get();
    } while (cost < currentMin && !minPathCost.compareAndSet(currentMin, cost));
  }
}
