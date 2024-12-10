import java.util.Scanner;

public class Main {

  private static int threads = 12; // NÚMERO DE THREADS QUE DESEJA USAR
  private static int[][] grafoFacil = Grafos.getGrafo(1);
  private static int[][] grafoMedio = Grafos.getGrafo(2);
  private static int[][] grafoDificil = Grafos.getGrafo(3);

  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);

    boolean running = true;
    while (running) {
      System.out.println("\n\n===================================================");
      System.out.println("Escolha o tipo de execução:");
      System.out.println("1. Sequencial");
      System.out.println("2. Paralela");
      System.out.println("3. Distribuída");
      System.out.println("4. Sair");
      System.out.printf("Opção: ");
      int option = scanner.nextInt();

      if (option == 4) {
        System.out.println("Encerrando a aplicação...");
        running = false;
        break;
      }

      System.out.println("\nEscolha o nível de dificuldade:");
      System.out.println("1. Fácil");
      System.out.println("2. Médio");
      System.out.println("3. Difícil");
      System.out.printf("Opção: ");
      int level = scanner.nextInt();

      int[][] graph;
      switch (level) {
        case 1:
          graph = grafoFacil;
          break;
        case 2:
          graph = grafoMedio;
          break;
        case 3:
          graph = grafoDificil;
          break;
        default:
          System.out.println("Nível de dificuldade inválido!");
          continue;
      }

      try {
        switch (option) {
          case 1:
            System.out.println("\nExecutando solução SEQUENCIAL...");
            Sequencial sequencial = new Sequencial(graph);
            sequencial.solve();
            break;
          case 2:
            System.out.println("\nExecutando solução PARALELA...");
            Paralelo paralelo = new Paralelo(graph, threads);
            paralelo.solve();
            break;
          case 3:
            System.out.println("\nExecutando solução DISTRIBUÍDA...");
            new Thread(() -> {
              Servidor servidor = new Servidor(graph);
              servidor.start();
            }).start();
            Cliente cliente = new Cliente("localhost", 12345, threads);
            cliente.start();
            break;
          default:
            System.out.println("Opção inválida!");
        }
      } catch (IllegalArgumentException e) {
        System.out.println("Erro: " + e.getMessage());
      }
    }

    scanner.close();
  }
}
