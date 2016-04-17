package com.example.latex.latexeditor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import io.socket.SocketIO;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.AlertDialog.Builder;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private SocketIO socket;
    private Menu menu;
    String sessonid, email, valueFromServer, onlineUsersList="No user is logged in",saveTextFromEditor;
    EditText editTextLatex;
    Spinner btnSigns;
    Button btnSave, btnConvert, btnDollar, btnSquareBrackets, btnCurlyBrackets, btnSlash, btnLoggedinUsers,btnReconnect;
    TextView txtOnlineUsers;
    private boolean mIsSpinnerFirstCall=true, doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            initializeComponents();
            registerReceivers();

        }
        catch(Exception ex)
        {
            Log.w("", ex.getMessage());
        }

    }

    private void initializeComponents()
    {
        Bundle extras = getIntent().getExtras();

            SocketSingleton.get(this.getApplicationContext());
            socket = SocketSingleton.getSocket();
            socket.emit("room", "abc123");

        btnReconnect = (Button) findViewById(R.id.btnReconnect);
        btnReconnect.setOnClickListener(this);
        btnLoggedinUsers = (Button) findViewById(R.id.btnLoggedinUsers);
        btnLoggedinUsers.setOnClickListener(this);
        editTextLatex = (EditText) findViewById(R.id.editTextLatex);
        editTextLatex.addTextChangedListener(latexWatcher);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        btnConvert = (Button) findViewById(R.id.btnConvert);
        btnConvert.setOnClickListener(this);
        btnSigns = (Spinner) findViewById(R.id.btnSigns);
        //btnSigns.setOnClickListener(this);
        btnDollar = (Button) findViewById(R.id.btnDollar);
        btnDollar.setOnClickListener(this);
        btnSquareBrackets = (Button) findViewById(R.id.btnSquareBrackets);
        btnSquareBrackets.setOnClickListener(this);
        btnCurlyBrackets = (Button) findViewById(R.id.btnCurlyBrackets);
        btnCurlyBrackets.setOnClickListener(this);
        btnSlash = (Button) findViewById(R.id.btnSlash);
        btnSlash.setOnClickListener(this);
        txtOnlineUsers = (TextView) findViewById(R.id.txtOnlineUsers);
        btnSigns.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (!mIsSpinnerFirstCall) {
                    editTextLatex.getText().insert(editTextLatex.getSelectionStart(), parentView.getItemAtPosition(position).toString());
                }
                mIsSpinnerFirstCall = false;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        if (extras != null) {

            email = extras.getString("email");
            editTextLatex.setText(extras.getString("editorText"));
        }
    }

    private void registerReceivers()
    {
        IntentFilter sessionidFilter = new IntentFilter("getSessionid");
        IntentFilter onlineUsers = new IntentFilter("getNumOfUsersOnline");
        IntentFilter serverCharacter = new IntentFilter("getServerCharacter");
        IntentFilter serverUseronlineList = new IntentFilter("getServerUseronlineList");
        IntentFilter connectionError = new IntentFilter("errorInConnection");
        this.registerReceiver(new MessageReceiver(), sessionidFilter);
        this.registerReceiver(new MessageReceiver(), onlineUsers);
        this.registerReceiver(new MessageReceiver(), serverCharacter);
        this.registerReceiver(new MessageReceiver(), serverUseronlineList);
        this.registerReceiver(new MessageReceiver(), connectionError);
    }
    public class MessageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent){

            CharSequence intentData;

            if(intent.getExtras().get("sessionid") != null) {
                intentData = (CharSequence) intent.getExtras().get("sessionid");
                sessonid =  intentData.toString();
                Log.d("Session id", sessonid);

            }
            if(intent.getExtras().get("onlineUsers") != null)
            {
                intentData = (CharSequence) intent.getExtras().get("onlineUsers");
                valueFromServer = intentData.toString();
                Log.d("Online users", valueFromServer);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtOnlineUsers.setText(valueFromServer);
                    }
                });
            }
            if(intent.getExtras().get("serverCharacter") != null)
            {
                intentData = (CharSequence) intent.getExtras().get("serverCharacter");
                valueFromServer = intentData.toString();
                saveTextFromEditor = valueFromServer;
                Log.d("Character from server", intentData.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        editTextLatex.setText(valueFromServer);

                    }
                });
            }
            if(intent.getExtras().get("serverUseronlineList") != null)
            {
                intentData = (CharSequence) intent.getExtras().get("serverUseronlineList");
                onlineUsersList = intentData.toString();
                Log.d("Loggedin users", intentData.toString());

            }
        if(intent.getExtras().get("errorInConnection") != null)
        {
            intentData = (CharSequence) intent.getExtras().get("errorInConnection");
            Toast.makeText(getApplicationContext(), intentData, Toast.LENGTH_LONG).show();
            Log.d("Error in connection", intentData.toString());

        }
        }
    }

    private final TextWatcher latexWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                saveTextFromEditor = editTextLatex.getText().toString();
                JSONObject obj = new JSONObject();

                obj.put("buffer", editTextLatex.getText().toString());
            socket.emit("client_character", obj, sessonid);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public void afterTextChanged(Editable s) {
            if (s.length() == 0) {

            } else{

            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        changeMenuOptionText(menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.login) {
            if(email == null) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                i.putExtra("sessionid", sessonid);
                i.putExtra("editorText",saveTextFromEditor);
                startActivity(i);
            }
            else
            {
                showMessage("Confirm","Are you sure, you want to disconnect from server?");
            }

            return true;
        }
        else if(id == R.id.exit)
        {
            SocketSingleton.makeInstanceNull();
            socket.disconnect();
            SocketSingleton.disconnectSocket();
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        try {
        if(v == btnSave)
        {
            JSONObject obj = new JSONObject();
            obj.put("name", "MyLatexDoc");
            obj.put("content", editTextLatex.getText().toString());
            if(email != null)
            {
                obj.put("owner", email);
            }
            else
            {
                obj.put("owner", "Sidra");
            }

            socket.emit("client_doc", obj, sessonid);
            btnConvert.setEnabled(true);
            }
        else if(v == btnConvert)
        {
            String docName = "MyLatexDoc.tex";
            socket.emit("client_convert", docName, sessonid);
        }
        else if(v == btnLoggedinUsers)
        {
            Intent i = new Intent(getApplicationContext(), OnlineUsersList.class);
            i.putExtra("loggedinUsersList",onlineUsersList);
            startActivity(i);
        }
        else if(v == btnSquareBrackets)
        {
            editTextLatex.getText().insert(editTextLatex.getSelectionStart(), "[]");
        }
        else if(v == btnCurlyBrackets)
        {
            editTextLatex.getText().insert(editTextLatex.getSelectionStart(), "{}");
        }
        else if(v == btnDollar)
        {
            editTextLatex.getText().insert(editTextLatex.getSelectionStart(), "$");
        }
        else if(v == btnSlash)
        {
            editTextLatex.getText().insert(editTextLatex.getSelectionStart(), "\\");
        }
        else if(v ==btnReconnect)
        {
            SocketSingleton.get(this.getApplicationContext());
            socket = SocketSingleton.getSocket();
            socket.emit("room", "abc123");
            btnSave.setEnabled(true);
            btnConvert.setEnabled(true);
            editTextLatex.setEnabled(true);
            btnLoggedinUsers.setEnabled(true);
            btnReconnect.setVisibility(View.INVISIBLE);
        }

        }

        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showMessage(String title, String message) {
        Builder builder = new Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        socket.emit("client_logout", email, sessonid);
                        btnSave.setEnabled(false);
                        btnConvert.setEnabled(false);
                        editTextLatex.setEnabled(false);
                        btnLoggedinUsers.setEnabled(false);
                        btnReconnect.setVisibility(View.VISIBLE);
                        email = null;
                        changeMenuOptionText(menu);
                        SocketSingleton.makeInstanceNull();
                        socket = null;
                    }
                });
        builder.show();
    }

private void changeMenuOptionText(Menu menu)
{
    if(email != null)
        menu.getItem(0).setTitle("Logout");
    else
        menu.getItem(0).setTitle("Login");
}

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            SocketSingleton.makeInstanceNull();
            socket.disconnect();
            SocketSingleton.disconnectSocket();
            socket = null;
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
