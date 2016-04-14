package com.example.latex.latexeditor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.MalformedURLException;


public class MainActivity extends AppCompatActivity implements IOCallback, View.OnClickListener{
    private SocketIO socket;
    String sessonid;
    EditText editTextLatex;
    String valueFromServer;
    Button btnSave, btnConvert, btnSigns, btnDollar, btnSquareBrackets, btnCurlyBrackets, btnSlash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       //lo bas
        try {
            Log.d("before","connecting");
           // mSocket = IO.socket(Constants.CHAT_SERVER_URL);// serce/
            makeConnection();
            initializeComponents();
            Log.d("after", "connected");// disconnect hata k
        }
        catch(Exception ex)
        {
            Log.w("", ex.getMessage());
        }

    }

    private void initializeComponents()
    {
        editTextLatex = (EditText) findViewById(R.id.editTextLatex);
        editTextLatex.addTextChangedListener(latexWatcher);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        btnConvert = (Button) findViewById(R.id.btnConvert);
        btnConvert.setOnClickListener(this);
        //btnSigns = (Button) findViewById(R.id.btnSigns);
        //btnSigns.setOnClickListener(this);
        btnDollar = (Button) findViewById(R.id.btnDollar);
        btnDollar.setOnClickListener(this);
        btnSquareBrackets = (Button) findViewById(R.id.btnSquareBrackets);
        btnSquareBrackets.setOnClickListener(this);
        btnCurlyBrackets = (Button) findViewById(R.id.btnCurlyBrackets);
        btnCurlyBrackets.setOnClickListener(this);
        btnSlash = (Button) findViewById(R.id.btnSlash);
        btnSlash.setOnClickListener(this);
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void makeConnection() throws MalformedURLException {
        try {

            socket = new SocketIO();
            socket.connect("http://192.168.137.1:3000", this);

        }
        catch(Exception ex)
        {
            Log.d("ConnectionError",ex.getMessage());
        }
    }

    @Override
    public void onDisconnect() {

    }

    @Override
    public void onConnect() {
        socket.emit("room", "abc123");
    }

    @Override
    public void onMessage(String s, IOAcknowledge ioAcknowledge) {
        String test= "";
    }

    @Override
    public void onMessage(JSONObject jsonObject, IOAcknowledge ioAcknowledge) {
        String test= "";
    }

    @Override
    public void on(String s, IOAcknowledge ioAcknowledge, Object... objects) {
        try {
            valueFromServer = objects[0].toString();
            if (s.equals("sessionid")) {
                sessonid = objects[0].toString();
            } else if (s.equals("server_character")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        editTextLatex.setText(valueFromServer);}
                });

            }
            Log.d("Event name", s);
        }
        catch (Exception ex)
        {
            Log.d("Object Error: " , ex.getMessage());
        }
    }

    @Override
    public void onError(SocketIOException e) {

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
        else if(v == btnSigns)
        {

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
}
