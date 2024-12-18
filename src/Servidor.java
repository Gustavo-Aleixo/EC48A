import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

/**
* Classe Servidor que implementa parte a solução distribuída.
* Essa classe cria um servidor que gerencia conexões de clientes para distribuir tarefas e processar resultados.
*/
public class Servidor {

  private static final int PORT = 12345;
  private int[][] graph;
  private AtomicInteger minPathCost = new AtomicInteger(Integer.MAX_VALUE);
  private ConcurrentLinkedQueue<Integer> resultsQueue = new ConcurrentLinkedQueue<>();
  private volatile boolean running = true;
  private ServerSocket serverSocket;
  private int threads;

  public Servidor(int[][] graph, int threads) {
    this.graph = graph;
    this.threads = threads;
  }

  /**
  * Inicia o servidor e aguarda conexões de clientes.
  * Para cada cliente conectado, delega o processamento à função processClient.
  * @param latch Sincronizador para gerenciar a conclusão das tarefas distribuídas.
  */
  public void start(CountDownLatch latch) {
    long startTime = System.nanoTime();
    try {
      serverSocket = new ServerSocket(PORT);
      while (running) {

        // Aguarda conexões de clientes
        Socket clientSocket = serverSocket.accept();
        processClient(clientSocket, startTime, latch);
      }
    } catch (IOException e) {
    }
  }


  /**
  * Finaliza o servidor, fechando o socket principal.
  */
  public void stop() {
    running = false;
    try {
      if (serverSocket != null && !serverSocket.isClosed()) {
        serverSocket.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  /**
  * Processa a conexão com o cliente, enviando o grafo e aguardando o resultado parcial.
  * @param clientSocket Socket do cliente conectado.
  * @param startTime    Tempo inicial do processamento para cálculo do tempo total.
  * @param latch        Sincronizador para sinalizar a conclusão das tarefas.
  */
  private void processClient(Socket clientSocket, long startTime, CountDownLatch latch) {
    new Thread(() -> {
      try (ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
          ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {

        out.writeObject(graph);
        out.flush();
        int clientResult = in.readInt();
        resultsQueue.add(clientResult);

        if (resultsQueue.size() == threads - 1) {
          combineResults(startTime, latch);
        }
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          clientSocket.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }).start();
  }


  /**
  * Combina os resultados recebidos de todos os clientes para encontrar o menor custo do caminho.
  * @param startTime Tempo inicial do processamento.
  * @param latch     Sincronizador para sinalizar a conclusão das tarefas.
  */
  private void combineResults(long startTime, CountDownLatch latch) {

    // Percorre os resultados na fila e atualiza o menor custo
    for (int result : resultsQueue) {
      if (result < minPathCost.get()) {
        minPathCost.set(result);
      }
    }

    long endTime = System.nanoTime();
    double elapsedTime = (endTime - startTime) / 1_000_000_000.0;
    System.out.println("Menor custo do caminho (distribuido): " + minPathCost.get());
    System.out.printf("Tempo de processamento (distribuido): %.2f segundos\n", elapsedTime);
    System.out.printf("===================================================\n\n");
    latch.countDown();
    stop();
  }
}