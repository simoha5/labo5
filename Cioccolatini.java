import java.util.concurrent.Semaphore;
import java.util.Random;

public class Cioccolatini {
    private static final int NUM_MANGIATORI = 3;
    private static final int P = 5; // Dimensione massima della scatola
    
    private static int cioccolatiniInScatola = 0;
    
    // Semafori per la sincronizzazione
    private static Semaphore mutexScatola = new Semaphore(1); // Per proteggere l'accesso alla scatola
    private static Semaphore scatolaNonVuota = new Semaphore(0); // Inizialmente la scatola è vuota
    private static Semaphore scatolaNonPiena = new Semaphore(1); // La scatola può essere riempita
    
    public static void main(String[] args) {
        System.out.println("Simulazione cioccolatini iniziata.");
        System.out.println("Capacità della scatola: " + P + ", Mangiatori: " + NUM_MANGIATORI);
        
        // Creazione e avvio del thread pasticciere
        Thread pasticciere = new Thread(new Pasticciere());
        pasticciere.start();
        
        // Creazione e avvio dei thread mangiatori
        Thread[] mangiatori = new Thread[NUM_MANGIATORI];
        for (int i = 0; i < NUM_MANGIATORI; i++) {
            mangiatori[i] = new Thread(new Mangiatore(i + 1));
            mangiatori[i].start();
        }
        
        // La simulazione continua fino a quando non viene interrotta
        try {
            Thread.sleep(30000); // Eseguiamo la simulazione per 30 secondi
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Terminazione dei thread
        pasticciere.interrupt();
        for (int i = 0; i < NUM_MANGIATORI; i++) {
            mangiatori[i].interrupt();
        }
        
        System.out.println("Simulazione cioccolatini terminata.");
    }
    
    static class Pasticciere implements Runnable {
        private Random random = new Random();
        
        @Override
        public void run() {
            System.out.println("Pasticciere inizia a lavorare.");
            
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    // Preparazione dei cioccolatini (simulazione)
                    Thread.sleep(random.nextInt(2000) + 1000);
                    
                    // Aspetta che la scatola sia vuota
                    scatolaNonPiena.acquire();
                    
                    // Riempie la scatola
                    mutexScatola.acquire();
                    if (cioccolatiniInScatola == 0) {
                        cioccolatiniInScatola = P;
                        System.out.println("Pasticciere ha riempito la scatola con " + P + " cioccolatini.");
                        
                        // Rilascia il semaforo per i mangiatori (segnala che ci sono cioccolatini)
                        for (int i = 0; i < P; i++) {
                            scatolaNonVuota.release();
                        }
                    }
                    mutexScatola.release();
                }
            } catch (InterruptedException e) {
                System.out.println("Pasticciere ha terminato il lavoro.");
            }
        }
    }
    
    static class Mangiatore implements Runnable {
        private int id;
        private Random random = new Random();
        
        public Mangiatore(int id) {
            this.id = id;
        }
        
        @Override
        public void run() {
            System.out.println("Mangiatore " + id + " inizia a mangiare cioccolatini.");
            
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    // Aspetta che ci sia almeno un cioccolatino nella scatola
                    scatolaNonVuota.acquire();
                    
                    // Prende un cioccolatino
                    mutexScatola.acquire();
                    cioccolatiniInScatola--;
                    System.out.println("Mangiatore " + id + " ha preso un cioccolatino. Rimasti: " + cioccolatiniInScatola);
                    
                    // Se la scatola è vuota, segnala che può essere riempita
                    if (cioccolatiniInScatola == 0) {
                        System.out.println("La scatola è vuota! Il pasticciere può riempirla di nuovo.");
                        scatolaNonPiena.release();
                    }
                    mutexScatola.release();
                    
                    // Mangia il cioccolatino (simulazione)
                    System.out.println("Mangiatore " + id + " sta mangiando un cioccolatino...");
                    Thread.sleep(random.nextInt(3000) + 1000);
                }
            } catch (InterruptedException e) {
                System.out.println("Mangiatore " + id + " ha smesso di mangiare cioccolatini.");
            }
        }
    }
}