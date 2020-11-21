import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.io.InputStreamReader;

/**
 * La classe Client rappresenta un client con i propri metodi di interazione
 * con il server.
 */
public class Client {

    /**
     * Il campo port rappresenta la porta su cui è in ascolto il server.
     */
    private static final int port = 7777;
    /**
     * Il campo host rappresenta l'ip del server.
     */
    private static final String host = "2.236.189.9";
    /**
     * Il campo menu1 è la prima delle interfacce visualizzabili.
     */
    private static final String menu1 = "1) Esci\n2) Scrivi a...";

    /**
     * Il metodo firstInsertion si occupa del primo inserimento di uno
     * username.
     * @return String ottenuta in input.
     */
    private String firstInsertion() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Inserisci uno username:");
        return sc.nextLine();
    }
    /**
     * Il metodo userControl si occupa della comunicazione con il server per verificare che lo
     * username scelto non sia al momento già utilizzato sul server stesso.
     * @param user Username scelto.
     * @param socket Socket client di cui gestire input-stream e output-stream.
     * @param out
     * @param in
     */
    private void userControl(String user, Socket socket, PrintWriter out, BufferedReader in) {
        Scanner sc = new Scanner(System.in);
        //Appena invocato il metodo, viene comunicato al server lo username scelto.
        out.println(user);
        String control;
        try {
            //Con dei cicli sincronizzati tra client e un thread del server, viene ripetuto l'inserimento dello username sè il server lo ritiene necessario.
            while((control = in.readLine()).equals("true")) {
                System.out.println("Username gia' in uso, reinserisci:");
                user = sc.nextLine();
                out.println(user);
            }
        }
        catch(IOException e) {
            System.err.println("Errore durante la comunicazione con il server.");
        }
    }

    /**
     * Il metodo firstMenu viene invocato dopo tutti i controlli iniziali e rappresenta l'esecuzione vera e propria del programma lato client.
     * Viene visualizzato il primo menu di scelta tra uscita e invio di un messaggio.
     * @param socket Socket client di cui gestire input-stream e output-stream.
     * @param out
     * @param in
     */
    private void firstMenu(Socket socket, PrintWriter out, BufferedReader in) {
        Scanner sc = new Scanner(System.in);
        try {
            //Viene ricevuto un messaggio dal server e stampato a schermo nel client.
            String fromServer = in.readLine();
            System.out.println(fromServer);
        }
        catch(IOException e) {
            System.err.println("Errore durante la comunicazione con il server.");
        }
        //Stampa del primo menu.
        System.out.println(menu1);
        //L'utente può effettuare una scelta.
        int choice = sc.nextInt();
        try {
            switch(choice) {
                case 1 :
                    out.println(String.valueOf(choice));
                    socket.close();
                    break;
                case 2 :
                    out.println(String.valueOf(choice));
                    buddySelection(socket, out, in);
                    communicate(socket, out, in);
                    break;
                default :
                    System.out.println();
            }
        }
        catch(IOException e) {
            System.err.println("Errore durante la chiusura del client.");
        }
    }

    private void buddySelection(Socket socket, PrintWriter out, BufferedReader in) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Inserisci lo username dell'utente con cui vuoi parlare:");
        String buddy = sc.nextLine();
        out.println(buddy);
    }

    private void communicate(Socket socket, PrintWriter out, BufferedReader in) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Comunicazione avviata...");
                

        Thread rec = (new Thread(new ReceiveMessage(socket)));
        rec.start();

        String toSend;
        while(!(toSend = sc.nextLine()).equals("exit")) {
            out.println(toSend);
        }
        System.out.println("INTERROMPENDO...");
        rec.interrupt();

    }

    class ReceiveMessage implements Runnable {

        private Socket socket;

        public ReceiveMessage(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {

            try {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String received;

                while(!(received = in.readLine()).equals("exit")) {
                    System.out.println(received);
                }
            }
            catch(IOException e) {

            }
        }

    }

    public static void main(String[] args) {

        Client cl = new Client();

        String user = cl.firstInsertion();


        try {
            //Viene inizializzato un Socket con una connessione all'host tramita la porta port.
            Socket socket = new Socket(host, port);
            //Vengono inizializzati due oggetti per la gestione dell'input-stream ed output-stream del socket client.
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            cl.userControl(user, socket, out, in);

            cl.firstMenu(socket, out, in);

        }
        catch(Exception e) {

        }
    }

}