package asm.hangman;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import asm.hangman.net.ClientConnection;
import asm.hangman.view.OutputViewer;

public class MainActivity extends AppCompatActivity implements OutputViewer{
    private ClientConnection clientToServerConnection;
    private Button playAgainBtn;
    private Button submitBtn;
    private LinearLayout startupLayout;
    private LinearLayout playLayout;
    private EditText wordGuess;
    private String ipAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUIOnStartup();
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    //Call at startup game
    private void setupUIOnStartup(){
        final Button connectBtn=(Button)findViewById(R.id.btnConnect);
        final Button quitBtn = (Button)findViewById(R.id.btnQuit);
        playAgainBtn = (Button)findViewById(R.id.btnPlayAgain);
        submitBtn = (Button)findViewById(R.id.btnSubmit);
        startupLayout = (LinearLayout) findViewById(R.id.startupLayout);
        playLayout = (LinearLayout) findViewById(R.id.playLayout);

        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText serverNameTxt = (EditText) findViewById(R.id.txtServerName);

                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(v.getContext().CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if(networkInfo != null && networkInfo.isConnected()){
                    ipAddress = ((EditText)findViewById(R.id.txtServerName)).getText().toString();
                    new Thread(new ConnectToServer()).start();

                    //Change layout
                    startupLayout.setVisibility(View.INVISIBLE);
                    playLayout.setVisibility(View.VISIBLE);

                    new Thread(clientToServerConnection).start();//Show first challenge
                    setupUIOnGame();//Setup Game UI
                }
                else{
                    Toast.makeText(getApplicationContext(), "No connection: You cannot connect to the server.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        quitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });


    }
    //Call after the game is started
    private void setupUIOnGame(){
        final Button submitBtn = (Button)findViewById(R.id.btnSubmit);
        final Button playAgainBtn = (Button)findViewById(R.id.btnPlayAgain);
        final Button quitBtn = (Button)findViewById(R.id.btnQuit2);
        wordGuess = (EditText)  findViewById(R.id.txtGuessedWord);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(v.getContext().CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if(networkInfo != null && networkInfo.isConnected()){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            clientToServerConnection.DataOut(wordGuess.getText().toString());
                            clientToServerConnection.dataIn();
                        }
                    }).start();
                }
                else{
                    Toast.makeText(getApplicationContext(), "No connection: You cannot connect to the server.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        playAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText serverNameTxt = (EditText) findViewById(R.id.txtServerName);

                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(v.getContext().CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if(networkInfo != null && networkInfo.isConnected()){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            clientToServerConnection.dataIn();
                        }
                    }).start();
                    playAgainBtn.setVisibility(View.INVISIBLE);
                    submitBtn.setEnabled(true);
                }
                else{
                    Toast.makeText(getApplicationContext(), "No connection: You cannot connect to the server.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        quitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        clientToServerConnection.DataOut("0");
                    }
                }).start();
                finish();
                System.exit(0);
            }
        });
    }

    //Show result outputs
    @Override
    public void showOutput(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String[] msgs = message.split(":");
                showWord(msgs[0]);
                showScore(msgs[1]);
                showRemainingAttempts(msgs[2]);
                if(message.contains("Success!")) showStatus(":) Success!");
                else if(message.contains("Game over!")) showStatus(":( Game over!");
                else  showStatus("You're playing...");

                resetWordGuess();
                if(message.contains("Success!") || message.contains("Game over!")) {
                    playAgainBtn.setVisibility(View.VISIBLE);
                    submitBtn.setEnabled(false);
                }else{
                    playAgainBtn.setVisibility(View.INVISIBLE);
                    submitBtn.setEnabled(true);
                }
            }
        });

    }
    public void showWord(String output){
        TextView textView = (TextView) findViewById(R.id.lblWord);
        textView.setText("\n  " + output);
    }
    public void showScore(String output){
        TextView textView = (TextView) findViewById(R.id.lblScore);
        textView.setText("\n  " + output);
    }
    public void showRemainingAttempts(String output){
        TextView textView = (TextView) findViewById(R.id.lblRemainingAttempts);
        textView.setText("\n  " + output);
    }
    public void showStatus(String output){
        TextView textView = (TextView) findViewById(R.id.lblSuccess);
        textView.setText("\n" + output + "\n");
    }
    private void resetWordGuess(){
        wordGuess = (EditText)  findViewById(R.id.txtGuessedWord);
        wordGuess.setText("");
    }

    //---------------------ConnectToServer Threaded class
    private class ConnectToServer  implements Runnable {
        @Override
        public void run() {
            MainActivity.this.clientToServerConnection = new ClientConnection(MainActivity.this, MainActivity.this.ipAddress);
            MainActivity.this.clientToServerConnection.connectToServer();
        }
    }
}


