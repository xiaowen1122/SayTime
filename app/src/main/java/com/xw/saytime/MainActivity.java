package com.xw.saytime;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private final String TAG = MainActivity.class.getSimpleName();
    private EditText editText;
    private SwitchCompat switchCompat;
    private Boolean bStopped;

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch(compoundButton.getId()) {
            case R.id.serviceSwitch:
                Log.i(TAG, "SwitchCompat is " + b);
                bStopped = b;
                SharedPreferences sp = getSharedPreferences("ServiceSetting", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("StopService", bStopped);
                editor.apply();
                if(!b) {
                    Intent intent = new Intent(MainActivity.this, SaySomethingService.class);
                    stopService(intent);
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sp = getSharedPreferences("ServiceSetting", Context.MODE_PRIVATE);
        bStopped = sp.getBoolean("StopService",false);

        switchCompat = (SwitchCompat)findViewById(R.id.serviceSwitch);
        switchCompat.setChecked(bStopped);
        switchCompat.setOnCheckedChangeListener(this);

        editText = (EditText)findViewById(R.id.somethingToSay);

        Button button = (Button)findViewById(R.id.btnSay);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!bStopped) {
                    String str = editText.getText().toString();
                    if (str.length() > 0) {
                        Intent intent = new Intent(MainActivity.this, SaySomethingService.class);
                        intent.putExtra("text", str);
                        startService(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "Text is empty", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "switch is off", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Intent intent = new Intent(MainActivity.this, SaySomethingService.class);
        intent.putExtra("text","");
        startService(intent);
    }
}
