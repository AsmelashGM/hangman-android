package server.startup;

import server.net.HangmanServer;

public class Main {
	public static void main(String[] args) {
		
		new Thread(new Runnable() {
			public void run() {
				new HangmanServer().gameHandler();
			}
		}).start();
	}
}