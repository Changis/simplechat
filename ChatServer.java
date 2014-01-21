import java.net.*;
import java.io.*;

// Conrad: Bra om Chatserver implementerar Runnable... 
public class ChatServer implements Runnable {
   // Conrad: 2 st serverthreads, en för varje klient.
   private ChatServerThread client_one_server_thread = null;
   private ChatServerThread client_two_server_thread = null;
   private ServerSocket server_socket = null;

   // Kan lika gärna tråda servern också.
   private Thread       thread = null;
	
   // ChatServer-konstruktor
   public ChatServer(int port){
      // Försök binda upp serversocket.
      try {
         server = new ServerSocket(port);  
         start();
      } catch(IOException ioe) {  
         System.out.println("Kan inte binda till port: " + port + ": " + ioe.getMessage() + " .. den var hal."); 
      }
   }

   public void run() {  
      while (thread != null) {
         try {
            System.out.println("Sitter och vill att det ska komma en klient ..."); 
            addThread(server.accept());
         }
         catch(IOException ioe) {
            System.out.println("Server accept-fel: " + ioe); stop(); 
         }
      }
   }

   // Start har egentligen bara till uppgift att dra igång den här servertråden.
   public void start() {
      if (thread == null) {
         thread = new Thread(this); 
         thread.start();
      }
   }

   // D'oh.... 
   public void stop() {  
      if (thread != null) {  thread.stop(); 
         thread = null;
      }
   }

   // Vi ropar på addThread om vi fått en ny klient som försökt ansluta.
   // Bara att hoppas att inte alla slots är tagna!
   private void addThread(Socket socket) {
      // Kolla om vi har en första klient ansluten...
      if (client_one_server_thread == null) {
         client_one_server_thread = new ChatServerThread(this, socket);
      } else if (client_two_server_thread == null) {
         client_two_server_thread = new ChatServerThread(this, socket);
      } else {
         System.out.println("Eh... jaha, det var ju inte bra.");
      }
   }

   // Handle ropar vi på från trådarna när vi ska dra iväg ett meddelande.
   public synchronized void handle(int ID, String input){
      // Det är lättare att skicka till båda, så behöver man inte hålla koll på vem det kom från.
      // men om man vill det så kan man ju alltid skriva till en funktion som matchar IDn och skickar
      // till alla utom 'id'.
      client_one_server_thread.send(ID + ": " + input);
      client_two_server_thread.send(ID + ": " + input);
   }

   // Conrad: rekommenderar att main bara tar hand om första kommandot och att ni
   // i main anropar en konstruktor med args som startar serversocket.
	public static void main(String[] args) throws Exception{
      // I main, skapa ny instans av klassen vi är i.
      ChatServer server = null;

      // Försök initialisera den. (kanske inte går :P)
      if (args.length != 1) {
         System.out.println("Prova: java ChatServer port");  
      } else {
         server = new ChatServer(Integer.parseInt(args[0]));         
      }
	}
}