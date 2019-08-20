package com.example.gmapsapi;

import android.app.Dialog;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity {
private static final String TAG = "MainActivity";
private static final int ERORE_DIALOG_REQUEST = 100;
private Button map_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(isServiceOk()){
            init();
            Toast.makeText(this, "dfgdsfgsd", Toast.LENGTH_SHORT).show();
        }
    }

    private void init() {
        map_btn = (Button)findViewById(R.id.map_activity_btn);
        map_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,MapActivity.class);
                startActivity(intent);
            }
        });
    }

    public boolean isServiceOk(){
        Log.d(TAG,"checking google services version");
        int availble = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if (availble == ConnectionResult.SUCCESS){
            //everything is find and user can make map requests...
            Log.d(TAG,"google play services is working correctly");
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(availble)){
            //an error occour but we cant resolve it...
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this,availble,ERORE_DIALOG_REQUEST);
            dialog.show();
        }else {
            Toast.makeText(MainActivity.this,"you can't make map request",Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
