package info.lamatricexiste.network;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.provider.ContactsContract;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MyContact extends Activity {
    Button btnSend,btnReceive;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_contact);
        btnSend=(Button)findViewById(R.id.btnSend);
        btnReceive=(Button)findViewById(R.id.btnReceive);
        btnReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyContact.this,ReceiverActivity.class));
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyContact.this,ActivityMain.class));
            }
        });
        //configureWifi();

    }
    public void send(){

    }
    public void receive(){

    }


}
