package mika.dupot.colaman.threads;

import mika.dupot.colaman.Socket.MyWebSocketServer;

public class ServerGameThread extends Thread {

    private MyWebSocketServer oWebsocketServer;

    public ServerGameThread(MyWebSocketServer oWebsocketServer_){
        oWebsocketServer=oWebsocketServer_;
    }

    @Override
    public void run()  {

        try{
            this.sleep(1000);
        } catch(InterruptedException e)  {

        }

        oWebsocketServer.cycle();
    }


}
