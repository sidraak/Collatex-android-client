package com.example.latex.latexeditor;

/**
 * Created by SSc on 4/16/2016.
 */
import android.content.Context;
import android.content.Intent;
import android.net.ParseException;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

/**
 * Created by SSc on 4/16/2016.
 */
public class SocketSingleton {

    private static SocketSingleton instance;
    private static SocketIO socket;
    private Context context;

    public static SocketSingleton get(Context context) {
        if (instance == null) {
            instance = getSync(context);
        }
        instance.context = context;
        return instance;
    }

    public static void makeInstanceNull() {
        instance = null;
    }

    public static void disconnectSocket()
    {
        socket.disconnect();
    }
    public static synchronized SocketSingleton getSync(Context context){
        if (instance == null) {
            instance = new SocketSingleton(context);
        }
        return instance;
    }

    public static SocketIO getSocket(){
        return socket;
    }

    private SocketSingleton(Context context){
        this.context = context;
        this.socket = getChatServerSocket();
    }

    private SocketIO getChatServerSocket(){
        try {
            Log.d("Connection", "I will connect");
            SocketIO socket = new SocketIO();
            socket.connect(Constants.CHAT_SERVER_URL, new IOCallback() {
                @Override
                public void onDisconnect() {

                }

                @Override
                public void onConnect() {
                    Log.d("Connection", "Connected");
                }

                @Override
                public void onMessage(String s, IOAcknowledge ioAcknowledge) {

                }

                @Override
                public void onMessage(JSONObject jsonObject, IOAcknowledge ioAcknowledge) {

                }

                @Override
                public void on(String event, IOAcknowledge ioAcknowledge, Object... objects) {

                    Log.d("Event_name",event);
                    if (event.equals("sessionid")) {
                        Intent intent = new Intent();
                        intent.setAction("getSessionid");
                        intent.putExtra("sessionid", objects[0].toString());
                        context.sendBroadcast(intent);

                    } else if (event.equals("user_online")) {
                        Intent intent = new Intent();
                        intent.setAction("getNumOfUsersOnline");
                        intent.putExtra("onlineUsers", objects[0].toString());
                        context.sendBroadcast(intent);
                    }
                    else if(event.equals("server_character"))
                    {
                        Intent intent = new Intent();
                        intent.setAction("getServerCharacter");
                        intent.putExtra("serverCharacter", objects[0].toString());
                        context.sendBroadcast(intent);
                    }
                    else if(event.equals("server_login"))
                    {
                        Intent intent = new Intent();
                        intent.setAction("getServerLogin");
                        intent.putExtra("serverLogin", objects[0].toString());
                        context.sendBroadcast(intent);
                    }
                    else if(event.equals("server_registration"))
                    {
                        Intent intent = new Intent();
                        intent.setAction("getServerRegisteration");
                        intent.putExtra("serverRegisteration", objects[0].toString());
                        context.sendBroadcast(intent);
                    }
                    else if(event.equals("server_useronlinelist"))
                    {
                        String users="";
                        try {
                            JSONObject jObject = new JSONObject(objects[0].toString());
                            Iterator<?> keys = jObject.keys();

                            while (keys.hasNext()) {
                                String key = (String) keys.next();

                                users =  users + jObject.getString(key)+ "\n";
                            }
                            Log.d("Current users", users);
                        }
                        catch (Exception ex)
                        {

                        }
                        Intent intent = new Intent();
                        intent.setAction("getServerUseronlineList");
                        intent.putExtra("serverUseronlineList", users);
                        context.sendBroadcast(intent);
                        Log.d("serverUseronlineList", users);
                    }
                }
                @Override
                public void onError(SocketIOException e) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);

                    Intent intent = new Intent();
                    intent.setAction("errorInConnection");
                    intent.putExtra("connectionError", "Could not connect to the server");
                    context.sendBroadcast(intent);
                    Log.d("Connection error: ", sw.toString());
                }
            });
            Log.d("Connection", "I am connected");
            return socket;

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

}

