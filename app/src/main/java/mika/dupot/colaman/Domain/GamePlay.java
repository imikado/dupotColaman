package mika.dupot.colaman.Domain;

import android.graphics.Point;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import mika.dupot.colaman.Server.Bomb;
import mika.dupot.colaman.Server.Flame;
import mika.dupot.colaman.Server.Player;

/*
Message from User

user: red,blue,green,yellow, none

action: askConnect

action: askMove
x: N
y: N

action: askAddBomb
x: N
y: N

Message from server

status: OK,KO

user: red,blue,green,yellow

[ que le user]
action: setUser
user: red,blue,green,yellow
[ que le user]

[ autre users]
action: userConnected
user:red,blue,green,yellow
[ autre users]

action: move
x: N
y: N

action: addBomb
x: N
y: N

action: exploseBomb
x: N
y: N
length: N

 */

public class GamePlay {


    public static int max=11;
    private String[] listUser = {"red", "blue", "green", "yellow"};
    private int nbUserConnected = 0;

    private ArrayMap<String, Point> listCoordUser = new ArrayMap<String, Point>();

    private int[][] tMap;
    private ArrayList<Bomb> tBomb = new ArrayList<Bomb>();
    private ArrayList<Player> tPlayer = new ArrayList<Player>();
    private ArrayList<Flame> tFlame = new ArrayList<Flame>();

    private String userServer="";

    public static GamePlay oGamePlay;

    public static final String ACTION_STARTGAME = "startGame";
    public static final String ACTION_RESTARTGAME="restartGame";
    public static final String ACTION_ASKRESTARTGAME="askRestartGame";

    public static final String ACTION_ASK_CONNECT = "askConnect";
    public static final String ACTION_ASK_MOVE = "askMove";
    public static final String ACTION_ASK_ADDBOMB = "askAddBomb";

    public static final String ACTION_SETUSER = "setUser";
    public static final String ACTION_USERCONNECTED = "userConnected";
    public static final String ACTION_MOVEUSER = "moveUser";
    public static final String ACTION_ADDBOMB = "addBomb";
    public static final String ACTION_EXPLOSEBOMB = "exploseBomb";
    public static final String ACTION_ADDFLAME = "addFlame";
    public static final String ACTION_REMOVEFLAME = "removeFlame";
    public static final String ACTION_GAMEOVER = "gameover";
    public static final String ACTION_GAMEENDED ="gameended" ;

    public static final String ACTION_IAMSERVER="iamServer";

    public static final String ACTION_DRAWWALL = "drawWall";
    public static final String ACTION_DRAWWALLBREAKABLE = "drawWallBreakable";
    public static final String ACTION_DRAWUSER = "drawUser";
    public static final String ACTION_REMOVEWALLBREAKABLE = "removeWallBreakable";

    public static final String FIELD_STATUS = "status";
    public static final String FIELD_ACTION = "action";
    public static final String FIELD_X = "x";
    public static final String FIELD_Y = "y";
    public static final String FIELD_LENGTH = "length";
    public static final String FIELD_USER = "user";

    public static final String STATUS_OK = "ok";
    public static final String STATUS_KO = "ko";

    public static final String FIELD_MESSAGE = "message";

    public static final String FIELD_ANSWER_FOR_USER = "answerForUser";
    public static final String FIELD_ANSWER_FOR_OTHERUSER = "answerForOtherUser";
    public static final String FIELD_ANSWER_FOR_EVERYUSER = "answerForEveryUser";


    public GamePlay() {

        resetGame();
    }

    public String sendRestartGame(){


        return ACTION_ASKRESTARTGAME;

    }

    //send ASK

    public String sendAskAddBomb(String user_, int x_, int y_) {
        Hashtable<String, String> oMessage = new Hashtable<String, String>();
        oMessage.put(FIELD_ACTION, ACTION_ASK_ADDBOMB);
        oMessage.put(FIELD_USER, user_);
        oMessage.put(FIELD_X, Integer.toString(x_));
        oMessage.put(FIELD_Y, Integer.toString(y_));

        Log.i("GamePlay", "sendAskAddBomb x:" + Integer.toString(x_) + " y:" + Integer.toString(y_));

        return encodeMessage(oMessage);
    }

    public String sendAskMove(String user_, int x_, int y_) {
        Hashtable<String, String> oMessage = new Hashtable<String, String>();
        oMessage.put(FIELD_ACTION, ACTION_ASK_MOVE);
        oMessage.put(FIELD_USER, user_);
        oMessage.put(FIELD_X, Integer.toString(x_));
        oMessage.put(FIELD_Y, Integer.toString(y_));

        Log.i("GamePlay", "sendAskMove x:" + Integer.toString(x_) + " y:" + Integer.toString(y_));

        return encodeMessage(oMessage);
    }

    //encode/decode message
    public Hashtable<String, String> decodeMessage(String msg_) {

        Hashtable<String, String> hashMessage = new Hashtable<String, String>();

        String[] listKeyVal = msg_.split("##");
        for (int i = 0; i < listKeyVal.length; i++) {
            String[] keyVal = listKeyVal[i].split("=");

            hashMessage.put(keyVal[0].toString(), keyVal[1].toString());
        }

        return hashMessage;
    }

    public String encodeMessage(Hashtable<String, String> hashMessage) {

        ArrayList<String> listKeyVal = new ArrayList<String>();

        Set<Map.Entry<String, String>> setHm = hashMessage.entrySet();
        Iterator<Map.Entry<String, String>> it = setHm.iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> e = it.next();


            listKeyVal.add(e.getKey() + "=" + e.getValue());
        }

        return TextUtils.join("##", listKeyVal);
    }
    //----

    public static GamePlay getInstance() {
        if (oGamePlay == null) {
            oGamePlay = new GamePlay();
        }
        return oGamePlay;
    }

    public boolean playerIsAlive(Hashtable<String, String> oMessage_) {
        Player oPlayer;

        try {
            oPlayer = getPlayerByColor(oMessage_.get(FIELD_USER));
            return oPlayer.isAlive();
        } catch (Exception e) {
            return false;
        }
    }

    public String getInstructionForMessage(String message_) {

        Hashtable<String, String> oAnswer = new Hashtable<String, String>();

        try {
            Log.i("GamePlay","getInstructionForMessage step 1");
            Hashtable<String, String> oMessage = this.decodeMessage(message_);

            Log.i("GamePlay","getInstructionForMessage step 2");
            String askAction = oMessage.get(FIELD_ACTION);

            Log.i("GamePlay","getInstructionForMessage step 3");

            oAnswer.put(FIELD_ANSWER_FOR_USER, "");
            oAnswer.put(FIELD_ANSWER_FOR_EVERYUSER, "");

            Log.i("GamePlay","getInstructionForMessage step 4");
            if (askAction.equals(ACTION_ASK_CONNECT)) {
                Log.i("GamePlay","getInstructionForMessage step 5");
                oAnswer = getAskConnect(oMessage);
                Log.i("GamePlay","getInstructionForMessage step 6");
            } else if (askAction.equals(ACTION_ASK_ADDBOMB)) {
                if (playerIsAlive(oMessage)) {
                    oAnswer = getAskAddBomb(oMessage);
                }
            } else if (askAction.equals(ACTION_ASK_MOVE)) {
                if (playerIsAlive(oMessage)) {
                    oAnswer = getAskMove(oMessage);
                }
            } else if (askAction.equals(ACTION_IAMSERVER)) {
                Log.i("GamePlay", "action iamserver:");
                userServer = oMessage.get(FIELD_USER);
                Log.i("GamePlay", "userServer" + userServer);
            }else  if (askAction.equals(ACTION_ASKRESTARTGAME)) {
                Log.i("GamePlay","getInstructionForMessage ACTION_RESTARTGAME");
                oAnswer = getRestartGame( );
            } else {
                oAnswer.put(FIELD_ANSWER_FOR_USER, "Error: askAction not known: '" + askAction + "' not in '" + ACTION_ASK_CONNECT + "'");
            }

            return oAnswer.get(FIELD_ANSWER_FOR_USER) + "__" + oAnswer.get(FIELD_ANSWER_FOR_EVERYUSER);

        }catch(Exception e){
            Log.e("GamePlay","getInstructionForMessage "+e.getMessage());

            return oAnswer.get(FIELD_ANSWER_FOR_USER) + "__" + oAnswer.get(FIELD_ANSWER_FOR_EVERYUSER);
        }
    }

    public Hashtable<String, String>  getRestartGame(){
        Hashtable<String, String> oAnswer = new Hashtable<String, String>();

        Hashtable<String, String> oAnswerUser = new Hashtable<String, String>();
        Hashtable<String, String> oAnswerEveryUser = new Hashtable<String, String>();

        oAnswerEveryUser.put(FIELD_ACTION,ACTION_RESTARTGAME);

        oAnswer.put(FIELD_ANSWER_FOR_EVERYUSER,encodeMessage(oAnswerEveryUser));
        oAnswer.put(FIELD_ANSWER_FOR_USER,"");

        return oAnswer;
    }

    public Hashtable<String, String> getAskConnect(Hashtable<String, String> oMessage_) {


        Hashtable<String, String> oAnswer = new Hashtable<String, String>();

        Hashtable<String, String> oAnswerUser = new Hashtable<String, String>();
        Hashtable<String, String> oAnswerEveryUser = new Hashtable<String, String>();

        try {

            Log.i("GamePlay","getAskConnect step 1");

            String currentUserConnected = "";

            if (nbUserConnected < listUser.length) {

                Log.i("GamePlay","getAskConnect step 2");

                currentUserConnected = listUser[nbUserConnected];

                Log.i("GamePlay","getAskConnect step 3");

                Point oPointUser = listCoordUser.get(currentUserConnected);


                Log.i("GamePlay","getAskConnect step 4");
                Player oPlayer = new Player(currentUserConnected, oPointUser.x, oPointUser.y);

                Log.i("GamePlay","getAskConnect step 5");
                tPlayer.add(oPlayer);

                Log.i("GamePlay","getAskConnect step 6");

                oAnswerUser.put(FIELD_STATUS, STATUS_OK);
                oAnswerUser.put(FIELD_ACTION, ACTION_SETUSER);

                Log.i("GamePlay","getAskConnect step 7");
                oAnswerUser.put(FIELD_USER, oPlayer.getUser());

                Log.i("GamePlay","getAskConnect step 8");

                oAnswerEveryUser.put(FIELD_STATUS, STATUS_OK);
                oAnswerEveryUser.put(FIELD_ACTION, ACTION_USERCONNECTED);
                oAnswerEveryUser.put(FIELD_USER, oPlayer.getUser());

                nbUserConnected += 1;
            } else {
                oAnswerUser.put(FIELD_STATUS, STATUS_KO);
                oAnswerUser.put(FIELD_MESSAGE, "Max user connected");
            }

            oAnswer.put(FIELD_ANSWER_FOR_USER, encodeMessage(oAnswerUser));
            oAnswer.put(FIELD_ANSWER_FOR_EVERYUSER, encodeMessage(oAnswerEveryUser));

        }catch(Exception e){

            Log.e("GamePlay","getAskConnect, "+e.getMessage());

            oAnswer.put(FIELD_ANSWER_FOR_USER, encodeMessage(oAnswerUser));
            oAnswer.put(FIELD_ANSWER_FOR_EVERYUSER, encodeMessage(oAnswerEveryUser));
        }

        return oAnswer;
    }

    public Player getPlayerByColor(String user_) throws Exception {
        for (int i = 0; i < tPlayer.size(); i++) {
            if (tPlayer.get(i).getUser().equals(user_)) {
                return tPlayer.get(i);
            }
        }
        throw new Exception("Player not found for user:" + user_);
    }

    public Hashtable<String, String> getAskMove(Hashtable<String, String> oMessage_) {

        Hashtable<String, String> oAnswer = new Hashtable<String, String>();

        Hashtable<String, String> oAnswerUser = new Hashtable<String, String>();
        Hashtable<String, String> oAnswerEveryUser = new Hashtable<String, String>();

        String xAskedString = oMessage_.get(FIELD_X);
        String yAskedString = oMessage_.get(FIELD_Y);
        int xAskedInt = Integer.parseInt(xAskedString);
        int yAskedInt = Integer.parseInt(yAskedString);

        String userWhoAsked = oMessage_.get(FIELD_USER);

        if (this.isWalkable(xAskedInt, yAskedInt)) {

            Player oPlayer;
            try {
                oPlayer = getPlayerByColor(userWhoAsked);

                oPlayer.setCoord(xAskedInt, yAskedInt);

                oAnswerEveryUser = oPlayer.getAnswerDrawUser();
            } catch (Exception e) {

            }

        }

        oAnswer.put(FIELD_ANSWER_FOR_USER, encodeMessage(oAnswerUser));
        oAnswer.put(FIELD_ANSWER_FOR_EVERYUSER, encodeMessage(oAnswerEveryUser));

        return oAnswer;
    }

    public Hashtable<String, String> getAskAddBomb(Hashtable<String, String> oMessage_) {

        Hashtable<String, String> oAnswer = new Hashtable<String, String>();

        Hashtable<String, String> oAnswerUser = new Hashtable<String, String>();
        Hashtable<String, String> oAnswerEveryUser = new Hashtable<String, String>();

        String xAsked = oMessage_.get(FIELD_X);
        String yAsked = oMessage_.get(FIELD_Y);

        String user = oMessage_.get(FIELD_USER);

        Player oCurrentPlayer;
        try {
            oCurrentPlayer = getPlayerByColor(user);

        } catch (Exception e) {
            oAnswer.put(FIELD_ANSWER_FOR_USER, encodeMessage(oAnswerUser));
            oAnswer.put(FIELD_ANSWER_FOR_EVERYUSER, encodeMessage(oAnswerEveryUser));

            return oAnswer;
        }

        if (oCurrentPlayer.canAddBomb() && this.isWalkable(Integer.parseInt(xAsked), Integer.parseInt(yAsked))) {

            Bomb oBomb = new Bomb(user, Integer.parseInt(xAsked), Integer.parseInt(yAsked));
            tBomb.add(oBomb);

            oCurrentPlayer.denyAddBomb();

            oAnswerEveryUser = oCurrentPlayer.getAnswerDrawAddBomb();

        }

        oAnswer.put(FIELD_ANSWER_FOR_USER, encodeMessage(oAnswerUser));
        oAnswer.put(FIELD_ANSWER_FOR_EVERYUSER, encodeMessage(oAnswerEveryUser));

        return oAnswer;
    }

    public void resetGame(){


        listCoordUser.clear();

        listCoordUser.put("red", new Point(1, 1));
        listCoordUser.put("blue", new Point(9, 1));
        listCoordUser.put("green", new Point(1, 12));
        listCoordUser.put("yellow", new Point(9, 12));

        for (int i = 0; i < tPlayer.size(); i++) {

            tPlayer.get(i).relive();

            Point oPlayerPoint=listCoordUser.get(tPlayer.get(i).getUser() );

            tPlayer.get(i).setCoord(oPlayerPoint.x,oPlayerPoint.y);
        }



        this.tMap=null;
        this.tMap = new int[][]{
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 0, 0, 0, 0, 2, 0, 2, 0, 0, 1, 0, 1},
                {1, 0, 1, 2, 1, 2, 1, 0, 1, 0, 1, 0, 1},
                {1, 2, 2, 0, 0, 0, 2, 2, 0, 2, 1, 0, 1},
                {1, 2, 1, 2, 1, 0, 1, 0, 1, 0, 1, 2, 1},
                {1, 0, 0, 0, 2, 0, 2, 0, 0, 0, 1, 0, 1},
                {1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1},
                {1, 0, 0, 2, 0, 0, 0, 0, 0, 2, 1, 2, 1},
                {1, 2, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1},
                {1, 0, 2, 0, 2, 0, 2, 0, 2, 2, 1, 0, 1},
                {1, 0, 1, 2, 1, 0, 1, 0, 1, 0, 1, 2, 1},
                {1, 0, 2, 2, 0, 0, 0, 2, 0, 0, 1, 2, 1},
                {1, 2, 1, 0, 1, 0, 1, 0, 1, 2, 1, 2, 1},
                {1, 0, 0, 2, 0, 2, 0, 2, 2, 0, 1, 0, 1},
                {1, 0, 1, 0, 1, 0, 1, 2, 1, 2, 1, 0, 1},
                {1, 0, 0, 2, 0, 0, 2, 0, 0, 2, 1, 0, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},

        };

        this.tFlame.clear();
        this.tBomb.clear();

    }

    public ArrayList<String> getListInstructionToStart() {
        ArrayList<String> listInstruction = new ArrayList<String>();

        Hashtable<String, String> oStartInstruction = new Hashtable<String, String>();
        oStartInstruction.put(FIELD_ACTION, ACTION_STARTGAME);

        listInstruction.add(encodeMessage(oStartInstruction));

        for (int y = 0; y < this.tMap.length; y++) {
            for (int x = 0; x < this.tMap[0].length; x++) {
                if (this.tMap[y][x] == 1) {
                    Hashtable<String, String> oInstruction = new Hashtable<String, String>();
                    oInstruction.put(FIELD_ACTION, ACTION_DRAWWALL);
                    oInstruction.put(FIELD_X, Integer.toString(x));
                    oInstruction.put(FIELD_Y, Integer.toString(y));

                    listInstruction.add(encodeMessage(oInstruction));
                } else if (this.tMap[y][x] == 2) {
                    Hashtable<String, String> oInstruction = new Hashtable<String, String>();
                    oInstruction.put(FIELD_ACTION, ACTION_DRAWWALLBREAKABLE);
                    oInstruction.put(FIELD_X, Integer.toString(x));
                    oInstruction.put(FIELD_Y, Integer.toString(y));

                    listInstruction.add(encodeMessage(oInstruction));
                }
            }
        }

        //users
        for (int i = 0; i < tPlayer.size(); i++) {

            Player oPlayer = tPlayer.get(i);

            listInstruction.add(encodeMessage(oPlayer.getAnswerDrawUser()));

        }

        return listInstruction;
    }


    public boolean isWalkable(int x_, int y_) {
        if (isFree(x_, y_)) {
            return true;
        }
        return false;
    }

    public ArrayList<String> cycle() {

        ArrayList<String> tMessage = new ArrayList<String>();

        try {



            for (int i = 0; i < tBomb.size(); i++) {


                tBomb.get(i).nextCycle();


                if (tBomb.get(i).willExplose()) {

                    Bomb oBombExplose = tBomb.get(i);
                    String userBombExplose = tBomb.get(i).getUser();
                    tBomb.remove(i);

                    tMessage.add(encodeMessage(oBombExplose.getAnswerExploseBomb()));

                    try {
                        allowUserToAddBomb(userBombExplose);

                    } catch (Exception e) {
                        Log.e("GamePlay", "Error:" + e.getMessage());
                        continue;
                    }

                    ArrayList<Point> tPoint = oBombExplose.getListFlames();
                    for (int iFlame = 0; iFlame < tPoint.size(); iFlame++) {
                        Point flamePoint = tPoint.get(iFlame);
                        Flame oFlame = new Flame(oBombExplose.getUser(), flamePoint.x, flamePoint.y);

                        tFlame.add(oFlame);

                        tMessage.add(encodeMessage(oFlame.getAnswerAddFlame()));
                    }


                }
            }


            for (int i = 0; i < tFlame.size(); i++) {

                tFlame.get(i).nextCycle();

                String userFindHere = getPlayerByCoord(tFlame.get(i).getX(), tFlame.get(i).getY());

                Player oPlayerKilled = null;
                try {
                    oPlayerKilled = getPlayerByColor(userFindHere);

                } catch (Exception e) {

                }


                if (oPlayerKilled != null && oPlayerKilled.isAlive()) {
                    Log.i("GamePlay","player killed");
                    playerKillPlayer(tFlame.get(i).getUser(), userFindHere);

                    tMessage.add(encodeMessage(oPlayerKilled.getAnswerKilled()));

                    if(gameIsOver()){
                        Log.i("GamePlay","gameIsOver:before add message");
                        Hashtable<String,String> oAnswerGameEnded=getAnswerGameEnded();
                        Log.i("GamePlay","gameIsOver:after get Answer");
                        tMessage.add(encodeMessage(oAnswerGameEnded));
                        Log.i("GamePlay","tMessage gameIsOverAdded");
                    }else{
                        Log.i("GamePlay","gameIsOver:nok");

                    }

                }

                if (isWallBreakable(tFlame.get(i).getX(), tFlame.get(i).getY())) {
                    removeWallBreakable(tFlame.get(i).getX(), tFlame.get(i).getY());

                    tMessage.add(encodeMessage(tFlame.get(i).getAnswerRemoveWallBreakable()));
                }

                if (tFlame.get(i).willDisapear()) {
                    tMessage.add(encodeMessage(tFlame.get(i).getAnswerRemoveFlame()));
                 }

            }

            for (int i=tFlame.size()-1; i >=0; i--) {
                if (tFlame.get(i).willDisapear()) {
                    tFlame.remove(i);
                }
            }

            return tMessage;

        }catch (Exception e){
            Log.e("GamePlay ERROR",e.toString());

            return tMessage;
        }

    }

    private Hashtable<String,String> getAnswerGameEnded() {
        Hashtable<String,String> oAnswer=new Hashtable<String,String>();
        oAnswer.put(FIELD_STATUS,STATUS_OK);
        oAnswer.put(FIELD_ACTION,ACTION_GAMEENDED);

        if(userServer==null){

            Log.e("GamePlay","getAnswerGameEnded, userServer is null");

            return oAnswer;
        }

        oAnswer.put(FIELD_USER,userServer);



        return oAnswer;
    }

    public boolean gameIsOver(){
        int nbAlivePlayer=0;
        for(int i=0;i<tPlayer.size();i++){
            if(tPlayer.get(i).isAlive()){
                nbAlivePlayer++;
            }
        }

        if(nbAlivePlayer <=1){
            return true;
        }
        return false;
    }

    private String getPlayerByCoord(int x_, int y_) {
        for (int i = 0; i < tPlayer.size(); i++) {
            if (tPlayer.get(i).getX() == x_ && tPlayer.get(i).getY() == y_) {
                return tPlayer.get(i).getUser();
            }
        }
        return "";
    }

    private void playerKillPlayer(String player_, String playerVictim_) {
        for (int i = 0; i < tPlayer.size(); i++) {
            if (tPlayer.get(i).getUser().equals(playerVictim_)) {
                tPlayer.get(i).kill();
            }
        }
    }

    private void allowUserToAddBomb(String user_) {

        Log.i("GamePlay", "allowUserToAddBomb, user:'" + user_ + "'");
        for (int i = 0; i < tPlayer.size(); i++) {
            if (tPlayer.get(i).getUser().equals(user_)) {
                tPlayer.get(i).allowAddBomb();
            }
        }
    }

    private void removeWallBreakable(int x, int y) {

        tMap[y][x] = 0;
    }


    public boolean canAddFlameOn(int x_, int y_) {
        if (isFree(x_, y_) || isWallBreakable(x_, y_)) {
            return true;
        }
        return false;

    }

    public boolean isFree(int x_, int y_) {

        if (isNotInMap(x_, y_)) {
            return false;
        }

        if (this.tMap[y_][x_] == 0) {
            return true;
        }
        return false;
    }


    public boolean isWallBreakable(int x_, int y_) {
        if (isNotInMap(x_, y_)) {
            return false;
        }

        if (this.tMap[y_][x_] == 2) {
            return true;
        }
        return false;
    }

    private boolean isNotInMap(int x_, int y_) {
        if (y_ < 0 || y_ > tMap.length) {
            return true;
        } else if (x_ < 0 || x_ > tMap[0].length) {
            return true;
        }
        return false;
    }

    public ArrayList<String> getListInstructionToReStart() {

        ArrayList<String> listInstruction = new ArrayList<String>();

        Hashtable<String, String> oStartInstruction = new Hashtable<String, String>();
        oStartInstruction.put(FIELD_ACTION, ACTION_RESTARTGAME);

        listInstruction.add(encodeMessage(oStartInstruction));

        for (int y = 0; y < this.tMap.length; y++) {
            for (int x = 0; x < this.tMap[0].length; x++) {
                if (this.tMap[y][x] == 1) {
                    Hashtable<String, String> oInstruction = new Hashtable<String, String>();
                    oInstruction.put(FIELD_ACTION, ACTION_DRAWWALL);
                    oInstruction.put(FIELD_X, Integer.toString(x));
                    oInstruction.put(FIELD_Y, Integer.toString(y));

                    listInstruction.add(encodeMessage(oInstruction));
                } else if (this.tMap[y][x] == 2) {
                    Hashtable<String, String> oInstruction = new Hashtable<String, String>();
                    oInstruction.put(FIELD_ACTION, ACTION_DRAWWALLBREAKABLE);
                    oInstruction.put(FIELD_X, Integer.toString(x));
                    oInstruction.put(FIELD_Y, Integer.toString(y));

                    listInstruction.add(encodeMessage(oInstruction));
                }
            }
        }

        //users
        for (int i = 0; i < tPlayer.size(); i++) {

            Player oPlayer = tPlayer.get(i);

            listInstruction.add(encodeMessage(oPlayer.getAnswerDrawUser()));

        }


        return listInstruction;
    }
}
