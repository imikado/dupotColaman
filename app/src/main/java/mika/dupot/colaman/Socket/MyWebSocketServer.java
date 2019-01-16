package mika.dupot.colaman.Socket;


import android.util.Log;

import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Hashtable;

import mika.dupot.colaman.Domain.GamePlay;
import mika.dupot.colaman.threads.ServerGameThread;


public abstract class MyWebSocketServer extends WebSocketServer {

    public static MyWebSocketServer oInstance;

    private ServerGameThread oThread = null;

    private ArrayList<org.java_websocket.WebSocket> listConnections;


    public MyWebSocketServer(InetSocketAddress address) {
        super(address);

    }

    @Override
    public void start() {
        super.start();

        listConnections = new ArrayList<org.java_websocket.WebSocket>();
    }


    @Override
    public void onClose(org.java_websocket.WebSocket conn, int code, String reason, boolean remote) {
        Log.d("SERVER", "onClose()");

    }

    @Override
    public void onError(org.java_websocket.WebSocket conn, final Exception ex) {
        Log.d("SERVER", "onError()", ex);


        sendMessageToUi("errror:" + ex.toString());


    }


    @Override
    public void onStart() {

        Log.d("SERVER", "start()");

        sendMessageToUi("Le serveur du jeu est démarré sur votre appareil :)");

        hideStartServerButton();
        showConnectBtn();


    }

    @Override
    public void onMessage(org.java_websocket.WebSocket conn, String message) {

        try {

            Log.i("SERVER","onMessage");

            Log.i("SERVER", message);

            if (message.equals(GamePlay.ACTION_STARTGAME)) {


                this.startGame(true);

                return;
            } else if (message.equals(GamePlay.ACTION_ASKRESTARTGAME)) {

                Log.i("SERVER", "ACTION_ASKRESTARTGAME");

                GamePlay.getInstance().resetGame();

                Log.i("SERVER", "call GamePlay.resetGame");

                this.restartGame();

                Log.i("SERVER", "restartGame");

                //this.startGame(true);

                Log.i("SERVER", "startGame");
                return;
            }

            String messageFromGame = GamePlay.getInstance().getInstructionForMessage(message);

            String[] listMessageFor = messageFromGame.split("__");

            if (listMessageFor.length == 2) {

                String messageForUser = listMessageFor[0];
                String messageForEveryUser = listMessageFor[1];

                Log.i("SERVER", "messageFromGame:" + messageFromGame);

                if (messageForUser.isEmpty() == false) {
                    Log.i("SERVER", "messageForUser:" + messageForUser);
                    conn.send(messageForUser);

                    Hashtable<String, String> oMessage = GamePlay.getInstance().decodeMessage(messageForUser);

                    if (oMessage.get(GamePlay.FIELD_ACTION).equals(GamePlay.ACTION_SETUSER)) {
                        Log.i("SERVER", "setUser , add connexion");

                        sendMessageToUi("Nouvel utilisateur connecté, couleur: " + oMessage.get(GamePlay.FIELD_USER));
                        //listConnections.add(conn);
                    } else {
                        Log.i("SERVER", "Pas setUser, action:" + oMessage.get(GamePlay.FIELD_ACTION));
                    }


                }
                if (messageForEveryUser.isEmpty() == false) {
                    for (int i = 0; i < listConnections.size(); i++) {
                        listConnections.get(i).send(messageForEveryUser);
                    }
                }

            }

        }catch (Exception e){
            Log.e("SERVER","ERROR:"+e.getMessage());
        }


    }

    public void restartGame(){
        ArrayList<String> listMessageFromGame = GamePlay.getInstance().getListInstructionToReStart();
        broadcastMessage(listMessageFromGame);
    }

    public void startGame(boolean cycle_) {
        Log.i("SERVER", "startGame() called");
        ArrayList<String> listMessageFromGame = GamePlay.getInstance().getListInstructionToStart();


        broadcastMessage(listMessageFromGame);

        if(cycle_) {
            cycle();
        }
    }

    public void broadcastMessage(ArrayList<String> tMessage_) {
        try {
            if (tMessage_.size() > 0) {
                for (int i = 0; i < tMessage_.size(); i++) {

                    Log.i("SERVER bcast", "loop tMessage, i:" + Integer.toString(i));

                    for (int iUser = 0; iUser < listConnections.size(); iUser++) {

                        if (listConnections.get(iUser).isOpen()) {
                            listConnections.get(iUser).send(tMessage_.get(i));
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("SERVEUR bcast", e.getMessage());
        }
    }

    public void cycle() {

        try {

            oThread = null;

            Log.i("Websocket SERVER", "cycle");
            Log.i("Websocket SERVER", "cycle,ask GamePlay.cycle");
            ArrayList<String> tMessage = GamePlay.getInstance().cycle();

            Log.i("Websocket SERVER", "cycle,get GamePlay.cycle, tMessage.size:" + Integer.toString(tMessage.size()));

            broadcastMessage(tMessage);

            oThread = new ServerGameThread(this);

            oThread.start();

        } catch (Exception e) {

            Log.e("SERVER", "Error: " + e.getMessage());
            //ServerGameThread oThread = new ServerGameThread(this);
        }
        //oThread.start();
    }

    @Override
    public void onOpen(org.java_websocket.WebSocket conn, ClientHandshake handshake) {

        Log.d("SERVER", "onOpen()");

        listConnections.add(conn);

    }

    public abstract void sendMessageToUi(String message_);

    public abstract void hideStartServerButton();

    public abstract void showConnectBtn();
};