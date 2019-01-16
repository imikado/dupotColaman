package mika.dupot.colaman;



import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Hashtable;

import mika.dupot.colaman.Domain.GamePlay;
import mika.dupot.colaman.Socket.MyWebSocketClient;
import mika.dupot.colaman.Surface.GameSurface;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.WebSocket;

public class ClientActivity extends AppCompatActivity {

    private OkHttpClient client;
    private Button start;

    TextInputEditText inputIP;
    EditText oLogText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.client);

        inputIP = findViewById(R.id.inputIP);

        inputIP.setText(getIPAddress());

        oLogText = findViewById(R.id.logText);

        client = new OkHttpClient();

        start = (Button) findViewById(R.id.btnConnect);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start();
            }
        });
    }

    public void start() {

        final EditText oLogText = findViewById(R.id.logText);

        String sAddress = inputIP.getText().toString() + ":8080";

        message("Test de connexion sur " + sAddress + "...");

        MyWebSocketClient.oMyWebSocketClient = new MyWebSocketClient() {

            @Override
            public void onMessage(WebSocket webSocket, String text) {

                //output("Receiving : " + text);


                Hashtable<String, String> oMessage = GamePlay.getInstance().decodeMessage(text);

                if (oMessage.get(GamePlay.FIELD_ACTION).equals(GamePlay.ACTION_STARTGAME)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            launchGame();


                        }
                    });
                } else if (oMessage.get(GamePlay.FIELD_ACTION).equals(GamePlay.ACTION_SETUSER)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            waitingForGame();
                        }
                    });
                    GameSurface.executeMessageFromClient(oMessage);
                } else {
                    GameSurface.executeMessageFromClient(oMessage);

                }


            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {

                sendMessageToUi("Error:" + t.getMessage());


            }

            @Override
            public void sendMessageToUi(final String message_) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //EditText oLogText= findViewById(R.id.logText);

                        oLogText.append(message_ + "\n");
                    }
                });
            }
        };
        MyWebSocketClient.createWebSocketClient(sAddress);
        MyWebSocketClient.oInstance.send("action=askConnect");



        /*

        Request request = new Request.Builder().url("ws://"+sAddress).build();
        ClientActivity.ClientWebSocketListener listener = new ClientActivity.ClientWebSocketListener();

        WebSocket ws = client.newWebSocket(request, listener);

        client.dispatcher().executorService().shutdown();
        */

    }

    public void waitingForGame() {
        start.setEnabled(false);

        inputIP.setEnabled(false);

        oLogText.setText("Vous etes connecte, en attente du lancement de la partie...");

    }

    public void launchGame() {
        Intent oIntent = new Intent(this, GameActivity.class);
        startActivity(oIntent);

    }

    public void message(String text_) {
        EditText oLogText = findViewById(R.id.logText);
        oLogText.append(text_ + "\n");
    }


    public String getIPAddress() {


        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();
                    if (inetAddress.isSiteLocalAddress() && inetAddress.isLoopbackAddress() == false) {
                        ip = inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {

            e.printStackTrace();

        }
        return ip;
    }
}

