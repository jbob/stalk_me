package ch.markusko.stalkme;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private static final String[] LOCATION_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private ToggleButton button;
    private Button share;
    private EditText textfield;
    private String share_url;
    LocationService myService;

    private UrlBroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (ToggleButton) findViewById(R.id.button);
        share = (Button) findViewById(R.id.share);

        button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Intent startIntent = new Intent(MainActivity.this, LocationService.class);
                    startIntent.setAction("ch.markusko.stalkme.action.startforeground");
                    requestPermissions(LOCATION_PERMS, 123456789);
                    startService(startIntent);
                } else {
                    Intent stopIntent = new Intent(MainActivity.this, LocationService.class);
                    stopIntent.setAction("ch.markusko.stalkme.action.stopforeground");
                    startService(stopIntent);
                    updateClient("");
                }
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent share_intent = new Intent(android.content.Intent.ACTION_SEND);
                share_intent.setType("text/plain");
                share_intent.putExtra(Intent.EXTRA_TEXT, share_url);
                startActivity(Intent.createChooser(share_intent, "Share link!"));
            }
        });


        broadcastReceiver = new UrlBroadcastReceiver();
        registerReceiver(broadcastReceiver, new IntentFilter("set_url"));
    }

    public void updateClient(String data) {
        textfield = (EditText) findViewById(R.id.editText);
        textfield.setText(data);
        share_url = data;
        if (data.equals("")) {
            share.setVisibility(View.INVISIBLE);
        } else {
            share.setVisibility(View.VISIBLE);
        }

    }


    private class UrlBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String url = intent.getStringExtra("url");
            updateClient(url);
        }
    }

}
