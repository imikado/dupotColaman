package mika.dupot.colaman;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.java_websocket.WebSocketImpl;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Hashtable;

import mika.dupot.colaman.Domain.GamePlay;
import mika.dupot.colaman.Socket.MyWebSocketClient;
import mika.dupot.colaman.Socket.MyWebSocketServer;
import mika.dupot.colaman.Surface.GameSurface;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class ServerActivity extends AppCompatActivity {


    protected Button oBtnStartServer;
    protected Button oBtnConnnect;
    protected Button oBtnStartGame;
    protected EditText oLogText;

    protected String user;

    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.server);

        final TextInputEditText inputIP = findViewById(R.id.inputIP);

        inputIP.setText(getIPAddress());

        this.oLogText = findViewById(R.id.logText);

        this.oBtnConnnect = findViewById(R.id.btnConnect);

        this.oBtnStartGame = findViewById(R.id.btnStartGame);

        this.oLogText.setText("");


        final int port = 8080;
        WebSocketImpl.DEBUG = true;

        MyWebSocketServer.oInstance = new MyWebSocketServer(new InetSocketAddress(port)) {

            @Override
            public void sendMessageToUi(final String message_) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        oLogText.setText(oLogText.getText() + "\n" + message_);
                    }
                });
            }

            @Override
            public void hideStartServerButton() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        oBtnStartServer.setEnabled(false);
                    }
                });

            }


            @Override
            public void showConnectBtn() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        oBtnConnnect.setVisibility(View.VISIBLE);

                        EditText inputIP = (EditText)findViewById(R.id.inputIP);
                        inputIP.setEnabled(false);

                    }
                });
            }


        };



        oBtnStartServer = (Button) findViewById(R.id.button10);
        oBtnStartServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startServer();
            }
        });

        oBtnConnnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getIPAddress()
                String sAddress = inputIP.getText().toString();
                sAddress = "127.0.0.1:8080";

                MyWebSocketClient.oMyWebSocketClient = new MyWebSocketClient() {
                    @Override
                    public void onMessage(okhttp3.WebSocket webSocket, String text) {

                        //output("Receiving : " + text);

                        Log.i("SERVER CLIENT",text);

                        final Hashtable<String, String> oMessage = GamePlay.getInstance().decodeMessage(text);

                        if (oMessage.get(GamePlay.FIELD_ACTION).equals(GamePlay.ACTION_STARTGAME)) {

                            GameSurface.startGame();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    launchGame();

                                }
                            });


                        } else if(oMessage.get(GamePlay.FIELD_ACTION).equals(GamePlay.ACTION_SETUSER)){
                            user=oMessage.get(GamePlay.FIELD_USER);

                            Log.i("SERVER","our user is:"+user);

                            GameSurface.executeMessageFromClient(oMessage);
                        } else {

                            GameSurface.executeMessageFromClient(oMessage);

                        }


                    }

                    @Override
                    public void onFailure(okhttp3.WebSocket webSocket, Throwable t, Response response) {

                        Log.e("SERVER",t.getMessage());
                        sendMessageToUi(t.getMessage());


                    }

                    @Override
                    public void sendMessageToUi(final String message_) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //EditText oLogText= findViewById(R.id.logText);

                                oLogText.append( message_);
                            }
                        });
                    }
                };
                MyWebSocketClient.createWebSocketClient(sAddress);
                MyWebSocketClient.oInstance.send( GamePlay.FIELD_ACTION+"="+GamePlay.ACTION_ASK_CONNECT);

                //oBtnConnnect.setVisibility(View.INVISIBLE);
                //oBtnConnnect.setClickable(false);
                oBtnConnnect.setEnabled(false);

                oBtnStartGame.setVisibility(View.VISIBLE);

                /*
                Request request = new Request.Builder().url("ws://" + sAddress).build();
                MyWebSocketClient listener = new MyWebSocketClient();

                client = new OkHttpClient();

                okhttp3.WebSocket ws = client.newWebSocket(request, listener);
                ws.send("action=askConnect");
                */
                //listener.askConnect();
            }
        });

        oBtnStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //MyWebSocketClient.oInstance.send("action=startGame");

                //oBtnStartGame.setVisibility(View.INVISIBLE);
                oBtnStartGame.setEnabled(false);

                Hashtable<String,String> oMessage=new Hashtable<String,String>();
                oMessage.put(GamePlay.FIELD_ACTION,GamePlay.ACTION_IAMSERVER);
                oMessage.put(GamePlay.FIELD_USER,user);

                MyWebSocketClient.oInstance.send( GamePlay.getInstance().encodeMessage(oMessage));



                MyWebSocketClient.oInstance.send(GamePlay.ACTION_STARTGAME);

            }
        });


    }

    public void launchGame() {
        oLogText.append("Lancement du jeu");

        Intent oIntent = new Intent(this, GameActivity.class);
        startActivity(oIntent);


    }

    public void startServer() {

        MyWebSocketServer.oInstance.start();

    }

/*
    @Override
    protected void onStop() {
        super.onStop();

        try {
            MyWebSocketServer.oInstance.stop();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
*/


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
