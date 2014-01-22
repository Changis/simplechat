/**
 * TEST COMMIT
 */

import java.net.*;
import java.io.*;

public class ChatClient implements Runnable {
   private Socket socket              = null;
   private Thread thread              = null;
   private DataInputStream  console   = null;
   private DataOutputStream streamOut = null;
   private ChatClientThread client    = null;


   public ChatClient(String serverName, int serverPort) {  
		System.out.println("Etablerar anslutning, var god sitt still...");
		try {
			// Sätt upp socketen...
			socket = new Socket(serverName, serverPort);
			System.out.println("Ansluten: " + socket);
			start();
		} catch(UnknownHostException uhe) {
			System.out.println("Vet inte vart du ville att jag skulle ansluta: " + uhe.getMessage());
		} catch(IOException ioe) {
			System.out.println("Va? Det verkar som att vi fick ett fel: " + ioe.getMessage());
		}
   }

   // Run i ChatClient ligger och kollar om vi matat in någonting (och avslutat med enter -
   // eftersom vi försöker läsa en hel rad). I så fall skriver den ut det på streamOut, som
   // är bunden till socketen, och alltså kommer dyka upp på streamIn på i ServerThread.
   public void run() {
      while (thread != null) {  
         try {
            streamOut.writeUTF(console.readLine());
            streamOut.flush();
         } catch(IOException ioe) {  System.out.println("Fel vid postande: " + ioe.getMessage());
            stop();
         }
      }
   }

   // Handle ropar vi på från ChatClient-tråden. Den tar meddelandet som kommer från
   // den här klientens motsvarande serverthread och skriver ut det så att användaren
   // kan se det. Om det råkar
   public void handle(String msg) {  
      System.out.println(msg);         
   }

   // Vi har 2 st DataInputStreams här. Vi behöver lyssna både på vad
   // man själv skriver på tangentbordet - det är vad System.in gör -
   // och på vad som kommer på socketen man är ansluten
   // till och det gör vi i tråden.
   // det är typ därför man vill tråda chat-klienten.
   public void start() throws IOException {
      console   = new DataInputStream(System.in);
      streamOut = new DataOutputStream(socket.getOutputStream());

      if (thread == null) {
         client = new ChatClientThread(this, socket);
         thread = new Thread(this);                   
         thread.start();
      }
   }

   public void stop() {
      if (thread != null) {
         thread.stop();  
         thread = null;
      }
      try{
         if (console   != null) { console.close(); }
         if (streamOut != null) { streamOut.close(); }
         if (socket    != null) { socket.close(); }
      } catch(IOException ioe) {
         System.out.println("Fel vid stängning :( ...");
      }
      client.close();  
      client.stop();
   }

   // Börja med att starta en chatklient.. skicka in lite argument typ.
   public static void main(String args[])
   {
      ChatClient client = null;
      if (args.length != 2) {
         System.out.println("Prova: java ChatClient host port");         
      }
      else {
         client = new ChatClient(args[0], Integer.parseInt(args[1]));         
      }
   }

}