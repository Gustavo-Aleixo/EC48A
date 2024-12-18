import java.util.Random;

/**
 * Classe responsável pela geração de grafos com pesos aleatórios, de acordo com o nível de dificuldade.
 */
public class Grafos {

  /**
   * Gerador de grafos aleatorios.
   * @param size Tamanho do grafo (número de nós).
   * @param minWeight valor mínimo.
   * @param maxWeight valor máximo.
   * @return Matriz de adjacência representando o grafo.
   */
  private static int[][] generateRandomGraph(int size, int minWeight, int maxWeight) {
    Random random = new Random();
    int[][] graph = new int[size][size];
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        if (i == j) {
          graph[i][j] = 0;
        } else if (graph[i][j] == 0) {
          int weight = random.nextInt(maxWeight - minWeight + 1) + minWeight;
          graph[i][j] = weight;
          graph[j][i] = weight;
        }
      }
    }
    return graph;
  }


  /**
   * Retorna um grafo aleatório baseado no nível de dificuldade.
   * @param level Nível de dificuldade (1 = Fácil, 2 = Médio, 3 = Difícil).
   * @return Matriz de adjacência representando o grafo.
   */
  public static int[][] getGrafo(int level) {
    switch (level) {
      case 1: // FÁCIL
        return generateRandomGraph(11, 20, 50);
      case 2: // MÉDIO
        return generateRandomGraph(12, 30, 100);
      case 3: // DIFÍCIL
        return generateRandomGraph(13, 50, 150);
      default:
        throw new IllegalArgumentException("Nível inválido! Escolha entre 1, 2 ou 3.");
    }
  }
}
