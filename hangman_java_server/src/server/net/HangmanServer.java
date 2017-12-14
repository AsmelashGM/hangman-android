package server.net;
import java.net.*;

import server.controller.HangmanController;
import server.view.View;

import java.io.*;
public class HangmanServer  implements Runnable{
	private String message;
	private String userInput;
	private ServerSocket ss;
	private Socket s;
	private static int port = 8989;
	private DataInputStream dataIn;
	private DataOutputStream dataOut;
	private HangmanController game;
	private View view;
	HangmanServer(Socket s){this.s = s;}
	public HangmanServer() {}
	
	public void run() {
		try {
			//Declarations of data in/out streams
			dataIn = new DataInputStream(s.getInputStream());
			dataOut = new DataOutputStream(s.getOutputStream());
			game = new HangmanController();
			view = new View();
			
			gameCaller();
			
			dataOut.close();
			ss.close();
			view.println("Server shutdown!");
		}
		catch(Exception ex) {
			view.errln(ex.getMessage());
		}
	}
	private void gameCaller() throws Exception{
		message = game.instantiateTheGame();		
		while(true) {
			dataOut.writeUTF(message);
			dataOut.flush();
			if(message.contains("Success!") || message.contains("Game over!")) {
				gameCaller();
				break;
			}
			userInput = dataIn.readUTF();
			if(userInput.equals("0")) break;
			
			message = game.onGame(userInput);
		}
		view.println("Player opted-out!");
	}	
	public void gameHandler() {
		view = new View();
		view.println("Hangman server is running...");
		
		try {
			//Create a server socket, listen for incoming connections @localhost:8989
			ss = new ServerSocket(port);
			while(true) {
				s = ss.accept();
				//Show who is playing and create its own thread!
				String ipPort = s.getRemoteSocketAddress().toString().substring(1);
				String[] addr = ipPort.split(":");
				view.println("A player from "+addr[0]+" on port "+addr[1]+" is playing");
				
				new Thread(new HangmanServer(s)).start();
			}
		}
		catch(Exception ex) {
			view.errln(ex.getMessage());
		}
	}
}