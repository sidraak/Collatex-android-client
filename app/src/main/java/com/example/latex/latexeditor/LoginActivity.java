package com.example.latex.latexeditor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import io.socket.SocketIO;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private SocketIO socket;
    Button btnLoginAndRegistered, btnRegister;
    TextView txtEmail, txtPassword;
    private String sessionid, valueFromServer;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
         toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initializeComponent();
        registerReceivers();
    }

    private void initializeComponent()
    {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            sessionid = extras.getString("sessionid");
        }
        socket = SocketSingleton.getSocket();
        btnLoginAndRegistered = (Button) findViewById(R.id.btnLoginAndRegistered);
        btnLoginAndRegistered.setOnClickListener(this);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);
        txtEmail =(TextView) findViewById(R.id.txtEmail);
        txtPassword =(TextView) findViewById(R.id.txtPassword);
    }

    private void registerReceivers()
    {
        IntentFilter login = new IntentFilter("getServerLogin");
        this.registerReceiver(new MessageReceiver(), login);
        IntentFilter registeration = new IntentFilter("getServerRegisteration");
        this.registerReceiver(new MessageReceiver(), registeration);
    }
    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){

            CharSequence intentData;

            if(intent.getExtras().get("serverLogin") != null) {
                intentData = (CharSequence) intent.getExtras().get("serverLogin");
                valueFromServer = intentData.toString();
                Log.d("Server Login data", intentData.toString());
                if (valueFromServer.equals("Error occured"))
                {
                    Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_LONG).show();
                }
                else if(valueFromServer.equals("no match found"))
                {
                    Toast.makeText(getApplicationContext(), "No match found", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), valueFromServer, Toast.LENGTH_LONG).show();
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    i.putExtra("userId",valueFromServer);
                    i.putExtra("email", txtEmail.getText().toString());
                    startActivity(i);
                }

            }
            else if(intent.getExtras().get("serverRegisteration") != null) {
                intentData = (CharSequence) intent.getExtras().get("serverRegisteration");
                valueFromServer = intentData.toString();
                Log.d("Server Registeration", intentData.toString());
                if(valueFromServer.equals("no match found"))
                {
                    Toast.makeText(getApplicationContext(), "No match found", Toast.LENGTH_LONG).show();
                }
                else
                {
                    //Bundle dataBundle = new Bundle();
                    //dataBundle.putString("userId",valueFromServer);
                    //dataBundle.putString("emailId", txtEmail.getText().toString());
                    Toast.makeText(getApplicationContext(), valueFromServer, Toast.LENGTH_LONG).show();
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    i.putExtra("userId", valueFromServer);
                    i.putExtra("email", txtEmail.getText().toString());
                    startActivity(i);
                }

            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }
    }


    @Override
    public void onClick(View v) {
        if(v == btnLoginAndRegistered)
        {
            String email = txtEmail.getText().toString();
            String password = txtPassword.getText().toString();


                if(email.length() > 0  && password.length() > 0 ) {
                    if (btnLoginAndRegistered.getText().equals("Login")) {
                        try {
                            JSONObject obj = new JSONObject();
                            obj.put("userEmail", email);
                            obj.put("userPassword", password);
                            socket.emit("client_login", obj, sessionid);
                        } catch (Exception ex) {
                            Log.d("Login Error", ex.getMessage());
                        }

                    }
                    else
                    {
                        try {
                            JSONObject obj = new JSONObject();
                            obj.put("userEmail", email);
                            obj.put("userPassword", password);
                            socket.emit("client_register", obj, sessionid);
                        } catch (Exception ex) {
                            Log.d("Login Error", ex.getMessage());
                        }

                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Enter Email and Password", Toast.LENGTH_LONG).show();
                }

        }

        else if(v == btnRegister)
        {
            btnRegister.setVisibility(View.INVISIBLE);
            btnLoginAndRegistered.setText("Register");
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Register");
        }
    }
}
