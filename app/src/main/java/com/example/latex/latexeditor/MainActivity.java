package com.example.latex.latexeditor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private SocketIO socket;
    private Menu menu;
    String sessonid, email, user_email;
    EditText editTextLatex;
    public String valueFromServer, onlineUsersList="No user is logged in";
    Spinner btnSigns;
    Button btnSave, btnConvert, btnDollar, btnSquareBrackets, btnCurlyBrackets, btnSlash, btnLoggedinUsers;
    TextView txtOnlineUsers;
    private boolean mIsSpinnerFirstCall=true, mIsResettingValue = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            //makeConnection();
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
        if(SocketSingleton.getSocket() != null)
        {
            socket = SocketSingleton.getSocket();
        }
        else {
            SocketSingleton.get(this.getApplicationContext());
            socket = SocketSingleton.getSocket();
            socket.emit("room", "abc123");
        }
        Bundle extras = getIntent().getExtras();
        String id;
        if (extras != null) {

            email = extras.getString("email");

        }

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

    }

private void registerReceivers()
{
    IntentFilter sessionidFilter = new IntentFilter("getSessionid");
    IntentFilter onlineUsers = new IntentFilter("getNumOfUsersOnline");
    IntentFilter serverCharacter = new IntentFilter("getServerCharacter");
    IntentFilter serverUseronlineList = new IntentFilter("getServerUseronlineList");
    this.registerReceiver(new MessageReceiver(), sessionidFilter);
    this.registerReceiver(new MessageReceiver(), onlineUsers);
    this.registerReceiver(new MessageReceiver(), serverCharacter);
    this.registerReceiver(new MessageReceiver(), serverUseronlineList);
}
    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){

            CharSequence intentData;

            if(intent.getExtras().get("sessionid") != null) {
                intentData = (CharSequence) intent.getExtras().get("sessionid");
                Log.d("Session id", intentData.toString());

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

        }
    }

    private final TextWatcher latexWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
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
                startActivity(i);
            }

            return true;
        }
        else if(id == R.id.exit)
        {
            socket.disconnect();
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
            obj.put("owner", "Sidra");
            socket.emit("client_doc", obj, sessonid);
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
        }

        catch (JSONException e) {
            e.printStackTrace();
        }
    }

private void changeMenuOptionText(Menu menu)
{
    if(email != null)
        menu.getItem(0).setTitle("Logout");
}
}
