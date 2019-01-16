package mika.dupot.colaman.Socket;

import android.util.Log;

import okhttp3.*;

public abstract class MyWebSocketClient extends WebSocketListener {
    private static final int NORMAL_CLOSURE_STATUS = 1000;

    public static MyWebSocketClient oMyWebSocketClient;

    public static okhttp3.WebSocket oInstance;

    protected WebSocket oWebSocket;

    public static void createWebSocketClient(String sAddress) {
        Request request = new Request.Builder().url("ws://" + sAddress).build();
        //MyWebSocketClient listener = new MyWebSocketClient();

        OkHttpClient client = new OkHttpClient();

        oInstance = client.newWebSocket(request, oMyWebSocketClient);
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        oWebSocket = webSocket;

    }

    /*
    @Override
    public void onMessage(WebSocket webSocket, String text) {

        output("Receiving : " + text);

        Hashtable<String,String> oMessage= GamePlay.getInstance().decodeMessage(text);

        GameSurface.executeMessageFromClient(oMessage);


    }*/

    private void output(String s) {

        System.out.println(s);

        Log.e("MyWebSocketClient", s);

    }


    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
        output("Closing : " + code + " / " + reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {

        output("Error : " + t.getMessage());


    }

    public abstract void sendMessageToUi(String message_);
}
