package asm.hangman.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.LinkedBlockingQueue;

import asm.hangman.view.OutputViewer;

public class ClientConnection implements Runnable {

    //Attribute declarations
    private Socket socket;
    private DataInputStream din;
    private DataOutputStream dout;

    private static String SERVER_ADDRESS = "192.168.2.5";
    private final static int SERVER_PORT = 8989;
    private final OutputViewer outputViewer;

    public ClientConnection(final OutputViewer outputViewer, String ipAddress){
        this.outputViewer = outputViewer;
        this.SERVER_ADDRESS = ipAddress;
    }

    public void run(){
        dataIn();
    }
    public void connectToServer(){
        try
        {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            din = new DataInputStream(socket.getInputStream());
            dout = new DataOutputStream(socket.getOutputStream());
        } catch (UnknownHostException e) {
            System.out.println("Unknown host @" + SERVER_ADDRESS + ":" + SERVER_PORT);
            System.exit(1);
        } catch (IOException e) {
            System.out.println("I/O Error: " + e.getMessage());
            System.exit(1);
        }
    }
    public void dataIn(){
        try {
            String message = din.readUTF();
            outputViewer.showOutput(message);
        } catch (IOException e) {
            System.out.println("Exception: " + e.getMessage());
            System.exit(1);
        }
    }
    public void DataOut(String userInput) {
        try {
            dout.writeUTF(userInput);
            dout.flush();
        } catch (IOException e) {
            System.out.println("Exception: " + e.getMessage());
            e.getStackTrace();
            System.exit(1);
        }
    }
}
