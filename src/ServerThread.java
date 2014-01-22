/**
 * TEST COMMIT
 */

import java.net.*;
import java.io.*;

// Varje ServerThread är kopplad till en klient.
// Varje socket har en inputstream och en outputstream, således har
// ServerThreads en streamIn (saker som kommer från klienten)
// och en streamOut (saker som ska till klienten.)
public class ServerThread implements Runnable{
   private ChatServer       server    = null;
   private Socket           socket    = null;
   private int              ID        = -1;
   private DataInputStream  streamIn  = null;
   private DataOutputStream streamOut = null;

   // Konstruktor, får med sig ChatServer-instansen som ropade på den och en socket som
   // den kan posta eller ta emot saker på - kopplad till sin klient. 
   public ChatServerThread(ChatServer server, Socket socket) {  
      super();
      this.server = server;
      this.socket = socket;
      this.ID     = socket.getPort();
   }
	
	// När det kommer saker från klienten som den här ServerThreaden
	// är kopplad till på sin DataInputStream så vill vi skicka detta
	// till servern, så att den i sin tur kan dra iväg det till alla klienter.
	// (Det kommer alltså för just den här klienten komma tillbaka som ett
	// anrop till 'send()' som sedan drar tillbaks det meddelandet till
	// klienten som först skickade det. *Hihihi*).
   public void run() {
   	System.out.println(ID + " : Startar den eviga loopen!");
      while (true)
      {  
      	try {
      		server.handle(ID, streamIn.readUTF());
         } catch(IOException ioe) {  
            System.out.println(ID + " Fel vid tolkning: " + ioe.getMessage());
            // Här vill man nog ett anrop till ChatServer för att
            // den ska plocka bort den här tråden, för den är ju rätt broken nu efter ioe.
            // rimligtvis genom att passa med ID på något sätt.
            // (ID = porten den här är ansluten på).
            stop();
         }
      }
   }

  	// Skicka meddelande till den här tråden genom att anropa
  	// .send på den här tråden i ChatServer. Den skickar sedan iväg
  	// meddelandet på sin streamOut som är kopplad till klienten.
   public void send(String msg) {
      try {  
         streamOut.writeUTF(msg);
         streamOut.flush();
      } catch(IOException ioe) {
         System.out.println(ID + " Fel vid skickande: " + ioe.getMessage());
         // Här vill man göra ett anrop till ChatServer för att
         // den ska plocka bort den här tråden, för den är ju rätt broken nu efter ioe.
         // rimligtvis genom att passa med ID på något sätt.
         // (ID = porten den här är ansluten på). 	
         stop();
      }
   }   

   // Öppnas och startas från ChatServer, se 'addThread' i ChatServer
   public void open() throws IOException {
      streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
      streamOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
   }

   // Stängs även från ChatServer...
   public void close() throws IOException
   {  
      if (socket != null)    socket.close();
      if (streamIn != null)  streamIn.close();
      if (streamOut != null) streamOut.close();
   }

}


