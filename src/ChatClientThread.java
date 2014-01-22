/**
 * TEST COMMIT
 */

import java.net.*;
import java.io.*;

public class ChatClientThread extends Thread {
   private Socket           socket   = null;
   private ChatClient       client   = null;
   private DataInputStream  streamIn = null;

   // Sätt en konstruktor som vi kan ropa på med en ChatClient och en socket.
   public ChatClientThread(ChatClient client, Socket socket) {  
      this.client   = client;
      this.socket   = socket;

      // Öppna upp och starta.
      open();  
      try {
		client.start();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
   }

   // streamIn är alltså bunden till den socketen som vi har
   // gemensam med motsvarande ServerThread. streamIn tar emot meddelanden
   // som den servertråden skickar på sin streamOut.
   public void open() {  
      try {
         streamIn  = new DataInputStream(socket.getInputStream());
      } catch(IOException ioe) {
         System.out.println("Error getting input stream: " + ioe);
         client.stop();
      }
   }

   public void close() {
      try {
         if (streamIn != null) {
            streamIn.close();
         }
      } catch(IOException ioe) {
         System.out.println("Error closing input stream: " + ioe);
      }
   }

   // Här ligger vi och snurrar och kollar om vi har fått någonting från
   // server-tråden vi har vår socket gemensam med. om vi har det så
   // ropar vi på handle..
   public void run() {  
      while (true) { 
         try {
            client.handle(streamIn.readUTF());
         } catch(IOException ioe) {
            System.out.println("Listening error: " + ioe.getMessage());
            client.stop();
         }
      }
   }
}