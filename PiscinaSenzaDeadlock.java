import java.util.concurrent.Semaphore;
import java.util.Random;

public class PiscinaSenzaDeadlock {
    private static final int NUM_CLIENTI = 10;
    private static final int NUM_SPOGLIATOI = 3;
    private static final int NUM_ARMADIETTI = 4;
    
    // Semafori per gestire la disponibilità di spogliatoi e armadietti
    private static Semaphore semaforoSpogliatoi = new Semaphore(NUM_SPOGLIATOI, true);
    private static Semaphore semaforoArmadietti = new Semaphore(NUM_ARMADIETTI, true);
    
    public static void main(String[] args) {
        System.out.println("Simulazione piscina modificata iniziata.");
        System.out.println("Spogliatoi: " + NUM_SPOGLIATOI + ", Armadietti: " + NUM_ARMADIETTI + ", Clienti: " + NUM_CLIENTI);
        
        // Creazione e avvio dei thread clienti
        Thread[] clienti = new Thread[NUM_CLIENTI];
        for (int i = 0; i < NUM_CLIENTI; i++) {
            clienti[i] = new Thread(new Cliente(i + 1));
            clienti[i].start();
        }
        
        // Attesa della terminazione di tutti i thread
        for (int i = 0; i < NUM_CLIENTI; i++) {
            try {
                clienti[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        System.out.println("Simulazione piscina modificata terminata.");
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
                // Entrare in piscina
                System.out.println("Cliente " + id + " è arrivato alla piscina.");
                
                // MODIFICA: Prendiamo entrambe le risorse insieme o nessuna
                // Implementazione per evitare il deadlock
                boolean risorseAcquisite = false;
                while (!risorseAcquisite) {
                    // Verifica che sia lo spogliatoio sia l'armadietto siano disponibili
                    if (semaforoSpogliatoi.tryAcquire()) {
                        System.out.println("Cliente " + id + " ha preso uno spogliatoio e cerca un armadietto.");
                        if (semaforoArmadietti.tryAcquire()) {
                            // Ha ottenuto entrambe le risorse
                            System.out.println("Cliente " + id + " ha preso anche un armadietto.");
                            risorseAcquisite = true;
                        } else {
                            // Non ha ottenuto l'armadietto, rilascia lo spogliatoio
                            semaforoSpogliatoi.release();
                            System.out.println("Cliente " + id + " non ha trovato armadietti liberi, rilascia lo spogliatoio.");
                            Thread.sleep(random.nextInt(500) + 100);
                        }
                    } else {
                        // Attesa prima di riprovare
                        System.out.println("Cliente " + id + " attende risorse disponibili...");
                        Thread.sleep(random.nextInt(500) + 100);
                    }
                }
                
                // (c) Si cambia nello spogliatoio
                System.out.println("Cliente " + id + " si sta cambiando nello spogliatoio...");
                Thread.sleep(random.nextInt(1000) + 500);
                
                // (d) Libera lo spogliatoio
                System.out.println("Cliente " + id + " ha finito di cambiarsi.");
                semaforoSpogliatoi.release();
                
                // (e) Mette i suoi vestiti nell'armadietto
                System.out.println("Cliente " + id + " mette i vestiti nell'armadietto.");
                Thread.sleep(random.nextInt(500) + 200);
                
                // (g) Nuota (tenendosi la chiave dell'armadietto)
                System.out.println("Cliente " + id + " sta nuotando...");
                Thread.sleep(random.nextInt(3000) + 1000);
                
                // (h) Prende la chiave di un spogliatoio
                System.out.println("Cliente " + id + " ha finito di nuotare e cerca uno spogliatoio...");
                semaforoSpogliatoi.acquire();
                System.out.println("Cliente " + id + " ha preso uno spogliatoio per cambiarsi.");
                
                // (i) Ricupera i suoi vestiti nell'armadietto
                System.out.println("Cliente " + id + " recupera i vestiti dall'armadietto.");
                Thread.sleep(random.nextInt(500) + 200);
                
                // (j) Si riveste nello spogliatoio
                System.out.println("Cliente " + id + " si sta rivestendo...");
                Thread.sleep(random.nextInt(1000) + 500);
                
                // (k) Libera lo spogliatoio
                System.out.println("Cliente " + id + " ha finito di rivestirsi.");
                semaforoSpogliatoi.release();
                
                // (l) Rida le chiavi dello spogliatoio (già fatto) e dell'armadietto
                System.out.println("Cliente " + id + " restituisce la chiave dell'armadietto.");
                semaforoArmadietti.release();
                
                System.out.println("Cliente " + id + " ha lasciato la piscina.");
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}