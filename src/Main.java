import java.util.Scanner;

public class Main {

  private static int threads = 12; // NUMERO DE THREADS QUE DESEJA USAR

  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    System.out.println("\n\n===================================================");
    System.out.println("Escolha o tipo de execução:");
    System.out.println("1. Sequencial");
    System.out.println("2. Paralela");
    System.out.printf("Opção: ");
    int option = scanner.nextInt();

    System.out.println("\nEscolha o nível de dificuldade:");
    System.out.println("1. Fácil");
    System.out.println("2. Médio");
    System.out.println("3. Difícil");
    System.out.printf("Opção: ");
    int level = scanner.nextInt();

    try {
      int[][] graph = Grafos.getGrafo(level);

      if (option == 1) {
        System.out.println("\nExecutando solução SEQUENCIAL...");
        Sequencial sequencial = new Sequencial(graph);
        sequencial.solve();
      } else if (option == 2) {
        System.out.println("\nExecutando solução PARALELA...");
        Paralelo paralelo = new Paralelo(graph, threads);
        paralelo.solve();
      } else {
        System.out.println("Opção inválida!");
      }
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }

    scanner.close();
  }
}
