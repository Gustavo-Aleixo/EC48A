import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class Main {

  private static int threads = Runtime.getRuntime().availableProcessors();
  private static int[][] grafoFacil = Grafos.getGrafo(1);
  private static int[][] grafoMedio = Grafos.getGrafo(2);
  private static int[][] grafoDificil = Grafos.getGrafo(3);

  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);

    boolean running = true;
    while (running) {
      showMenu();
      int option = scanner.nextInt();

      if (option == 4) {
        onClose();
        running = false;
        break;
      }

      showDifficulty();
      int level = scanner.nextInt();

      int[][] graph = getGrafo(level);

      if (graph == null) {
        System.out.println("Nível de dificuldade inválido!");
        continue;
      }

      execute(option, graph);
    }

    scanner.close();
  }

  private static void showMenu() {
    System.out.println("\n\n===================================================");
    System.out.println("Escolha o tipo de execução:");
    System.out.println("1. Sequencial");
    System.out.println("2. Paralela");
    System.out.println("3. Distribuída");
    System.out.println("4. Sair");
    System.out.printf("Opção: ");
  }

  private static void showDifficulty() {
    System.out.println("\nEscolha o nível de dificuldade:");
    System.out.println("1. Fácil");
    System.out.println("2. Médio");
    System.out.println("3. Difícil");
    System.out.printf("Opção: ");
  }

  private static int[][] getGrafo(int level) {
    switch (level) {
      case 1:
        return grafoFacil;
      case 2:
        return grafoMedio;
      case 3:
        return grafoDificil;
      default:
        return null;
    }
  }

  private static void onClose() {
    System.out.println("Encerrando a aplicação...");
  }

  private static void execute(int option, int[][] graph) {
    try {
      switch (option) {
        case 1:
          executeSequencial(graph);
          break;
        case 2:
          executarParalelo(graph);
          break;
        case 3:
          executeDistribuida(graph);
          break;
        default:
          System.out.println("Opção inválida!");
      }
    } catch (IllegalArgumentException e) {
      System.out.println("Erro: " + e.getMessage());
    }
  }

  private static void executeSequencial(int[][] graph) {
    System.out.println("\nExecutando solução SEQUENCIAL...");
    Sequencial sequencial = new Sequencial(graph);
    sequencial.solve();
  }

  private static void executarParalelo(int[][] graph) {
    System.out.println("\nExecutando solução PARALELA...");
    Paralelo paralelo = new Paralelo(graph, threads);
    paralelo.solve();
  }

  private static void executeDistribuida(int[][] graph) {
    System.out.println("\nExecutando solução DISTRIBUÍDA...");
    CountDownLatch latch = new CountDownLatch(1);

    new Thread(() -> {
      Servidor servidor = new Servidor(graph, threads);
      servidor.start(latch);
    }).start();

    for (int i = 1; i <= threads - 1; i++) {
      int startNode = i % (graph.length - 1) + 1;
      Cliente cliente = new Cliente("localhost", 12345, startNode);
      new Thread(cliente::start).start();
    }

    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
