package com.xw.saytime;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SaySomethingService extends Service {
    private final String TAG = SaySomethingService.class.getSimpleName();
    private TextToSpeech textToSpeech;
    private BroadcastReceiver broadcastReceiver;

    public  SaySomethingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i == TextToSpeech.SUCCESS) {
                    int suppported = textToSpeech.setLanguage(Locale.US);
                    if(suppported != TextToSpeech.LANG_AVAILABLE
                            && suppported != TextToSpeech.LANG_COUNTRY_AVAILABLE) {
                        Log.e(TAG, "tts not support");
                        Toast.makeText(SaySomethingService.this, "tts not support",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                if(Intent.ACTION_SCREEN_ON.equals(action)) {
                    sayTime();
                }
            }
        };

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            String text = intent.getStringExtra("text");
            if(text.length() > 0) {
                saySomething(text);
            }
        }
        return 0;
    }

    @Override
    public void onDestroy() {
        if(broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
        if(textToSpeech != null) {
            textToSpeech.shutdown();
        }
    }


    private void sayTime() {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        String stime = df.format(new Date());
        saySomething(stime);
    }

    private void saySomething(String something) {
        if(textToSpeech != null) {
            textToSpeech.speak(something, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
}
