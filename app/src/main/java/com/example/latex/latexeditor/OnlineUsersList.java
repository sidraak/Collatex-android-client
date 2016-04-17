package com.example.latex.latexeditor;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class OnlineUsersList extends Activity {
    TextView txtOnlineUsersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.activity_online_users_list);
            initializeComponent();
        }
        catch (Exception ex)
        {
            Log.d("Error loggedinusers", ex.getMessage());
        }

    }

    private void initializeComponent()
    {
        try {
            String list="";
            txtOnlineUsersList = (TextView) findViewById(R.id.txtOnlineUsersList);
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                list = extras.getString("loggedinUsersList");
            }
            txtOnlineUsersList.setText(list);
        }
        catch (Exception ex)
        {

        }

    }
}
