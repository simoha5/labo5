import java.util.concurrent.Semaphore;
import java.util.Random;


public class piscinaSenzaDeadlock {
    public static final int NUM_CLIENTI = 10;
    public static final int NUM_SPOGLIATOI  = 3;        
    public static final int NUM_ARMADIETTI = 4;

    private static Semaphore semaforoSpogliatori = new Semaphore(NUM_SPOGLIATOI, true);
    private static Semaphore semaforoArmadietti = new Semaphore(NUM_ARMADIETTI, true);


    public static void main(String[] args) {
        System.out.println("Benvenuto nella piscina!");
        System.out.println("Ci sono " + NUM_SPOGLIATOI + " spogliatoi e " + NUM_ARMADIETTI + " armadietti disponibili, e abbiamo " + NUM_CLIENTI + " clienti per oggi.");
        
        Thread[] clienti = new Thread[NUM_CLIENTI];
        for (int i = 0; i < NUM_CLIENTI; i++) {
            clienti[i] = new Thread(new Cliente(i+1));
            clienti[i].start();
        }

        for (int i = 0; i < NUM_CLIENTI; i++) {
            try {
                clienti[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Tutti i clienti hanno terminato.");
    }

    static class Cliente implements Runnable {
        private int id;
        private Random random = new Random();

        public Cliente(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                //  entra nella piscina
                System.out.println("Cliente " + id + " è entrato nella piscina.");

                // per tgliere il deadlock, si invertono le operazioni di acquisto dei semafori

                // b) prende la chiave dell'armadietto
                System.out.println("Cliente " + id + " sta cercando un armadietto libero...");
                semaforoArmadietti.acquire();
                System.out.println("Cliente " + id + " ha trovato un armadietto libero.");

                // a) prende la chiave dello spogliatoio
                System.out.println("Cliente " + id + " sta cercando uno spogliatoio libero...");
                semaforoSpogliatori.acquire();
                System.out.println("Cliente " + id + " ha trovato uno spogliatoio libero.");
                
                // c) si cambia nello spogliatoio
                System.out.println("Cliente " + id + " si sta cambiando nello spogliatoio...");
                Thread.sleep(random.nextInt(2000) + 1000); // Simula il tempo di cambio
                System.out.println("Cliente " + id + " ha finito di cambiarsi nello spogliatoio.");
                
                // d) libera lo spogliatoio
                System.out.println("Cliente " + id + " ha finito di cambiarsi.");
                semaforoSpogliatori.release();
                
                // e) mette i vestiti nell'armadietto
                System.out.println("Cliente " + id + " sta mettendo i vestiti nell'armadietto...");
                Thread.sleep(random.nextInt(2000) + 1000); // Simula il tempo di messa dei vestiti
                System.out.println("Cliente " + id + " ha messo i vestiti nell'armadietto.");
                
                // f) rida la la chiave dello spogliatoio
                System.out.println("Cliente " + id + " sta restituendo la chiave dello spogliatoio...");

                // g) nuota(tenendo la chiave dell'armadietto)
                System.out.println("Cliente " + id + " sta nuotando...");
                Thread.sleep(random.nextInt(5000) + 2000); // Simula il tempo di nuoto
                System.out.println("Cliente " + id + " ha finito di nuotare.");

                // h) prende la chiave dello spogliatoio
                System.out.println("Cliente " + id + " sta cercando uno spogliatoio libero...");
                semaforoSpogliatori.acquire();
                System.out.println("Cliente " + id + " ha trovato uno spogliatoio libero.");

                // i) ricupera i suoi vestiti
                System.out.println("Cliente " + id + " sta recuperando i vestiti dall'armadietto...");
                Thread.sleep(random.nextInt(2000) + 1000); // Simula il tempo di recupero dei vestiti
                System.out.println("Cliente " + id + " ha recuperato i vestiti dall'armadietto.");

                // j) si riveste nello spogliatoio
                System.out.println("Cliente " + id + " si sta rivestendo nello spogliatoio...");
                Thread.sleep(random.nextInt(2000) + 1000); // Simula il tempo di rivestimento
                System.out.println("Cliente " + id + " ha finito di rivestirsi nello spogliatoio.");

                // k) libera lo spogliatoio
                System.out.println("Cliente " + id + " ha finito di rivestirsi.");
                semaforoSpogliatori.release();

                //rida la chiave dell'armadietto
                System.out.println("Cliente " + id + " sta restituendo la chiave dell'armadietto...");
                semaforoArmadietti.release();
                System.out.println("Cliente " + id + " ha restituito la chiave dell'armadietto,e ha lasciato la piscina.");
            } catch (InterruptedException e) {
                System.out.println("Cliente " + id + " è stato interrotto.");
            }

        }
    }
    


}