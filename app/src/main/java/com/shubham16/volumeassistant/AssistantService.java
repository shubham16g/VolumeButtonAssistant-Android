package com.shubham16.volumeassistant;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AssistantService extends Service {
    private static final String AI_NAME = "Jarvis";

    private static final String NOTIFICATION_CHANNEL_ID = "My_Not_Channel";
    public static final String NOTIFICATION_CHANNEL_NAME = "Name of channel";
    private static final String NOTIFICATION_CHANNEL_DESC = "Description of channel";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private SpeechRecognizer androidRecognizer;
    private Intent recognizerIntent;
    private AudioManager audioManager;
    private Vibrator vibe;
    private boolean isReady, isOK;
    private TextToSpeech myTTS;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showNotification();

        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        setRecogniserIntent();
        androidRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        setupAndroidRecognizer();
        initializeTTS();

        isReady = true;
        isOK = false;

        final BroadcastReceiver vReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (isReady) {
                    if (intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")){
                    }
                    isReady = false;
                    Log.d("inOnREc", "receive");

                    if (isOK) {

                        Log.d("inOnREc", "listening");
                        startListening();
                    }
                    if (!isOK) {
                        Log.d("inOnREc", "isOk");
                        isOK = true;
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isReady = true;
                            }
                        },200);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("inOnREc", "notOk");
                                isOK = false;
                            }
                        }, 2000);
                    }
                }

            }
        };
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction("android.media.VOLUME_CHANGED_ACTION");
        registerReceiver(vReceiver, intentFilter);
        return START_STICKY;
    }

    private void startListening() {
        if (vibe != null) {
            vibe.vibrate(100);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                androidRecognizer.startListening(recognizerIntent);
            }
        }, 300);
    }

    private void initializeTTS() {
        myTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (myTTS.getEngines().size() == 0) {
                    Toast.makeText(AssistantService.this, "There is no TTS", Toast.LENGTH_SHORT).show();
                } else {
                    myTTS.setLanguage(new Locale("hi-IN"));
                    myTTS.setVoice(new Voice("mark",Locale.forLanguageTag("hi"),Voice.QUALITY_VERY_HIGH, Voice.LATENCY_VERY_HIGH,true, null));
                    speak("          " + "Hello");
                    myTTS.setSpeechRate(1.0f);
                }
            }
        });
    }

    private void speak(final String text) {
        if (myTTS != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT >= 21) {
                        myTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
                    } else {
                        myTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
            },1000);

        }
    }

    //setup the android recogniser
    private void setRecogniserIntent() {
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en-US");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
//        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
    }

    public void setupAndroidRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            androidRecognizer.setRecognitionListener(androidListener);
        } else {
            stopForeground(true);
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myTTS.shutdown();

        if (androidRecognizer != null) {
            androidRecognizer.cancel();
            androidRecognizer.destroy();
        }
        Toast.makeText(this, "Destroy", Toast.LENGTH_SHORT).show();
    }

    private RecognitionListener androidListener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {

        }

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onRmsChanged(float v) {

        }

        @Override
        public void onBufferReceived(byte[] bytes) {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onPartialResults(Bundle bundle) {

        }

        @Override
        public void onEvent(int i, Bundle bundle) {

        }

        @Override
        public void onError(int n) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                    isReady = true;
                    isOK = false;
                }
            }, 500);

            Toast.makeText(getApplicationContext(), "No audio found", Toast.LENGTH_SHORT).show();
            errorVibrate();

        }

        @Override
        public void onResults(Bundle bundle) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                    isReady = true;
                    isOK = false;
                }
            }, 500);
            ArrayList<String> arrayList = bundle.getStringArrayList("results_recognition");
            if (arrayList != null) {
                action(arrayList.get(0).toLowerCase());
            }
        }
    };

    private void showNotification() {
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Shubham Assistant")
                .setTicker("TICKER")
                .setContentIntent(pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            builder.setColor(Color.WHITE);
            builder.setSmallIcon(R.drawable.assist);
        } else {
            builder.setSmallIcon(R.mipmap.ic_mob);
        }
        builder.setContentText("Hover your hand 2 times over proximity sensor");

        Notification notification = builder.build();
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(NOTIFICATION_CHANNEL_DESC);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
        startForeground(12, notification);
    }

    private void errorVibrate() {
        vibe.vibrate(50);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                vibe.vibrate(50);
            }
        }, 400);
    }

    private void call(String number) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + number));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            } else {
                startActivity(intent);
            }
        } else {
            startActivity(intent);
        }
    }

    private void action(String response) {
        if (response.contains("time") || response.contains("samay")) {
            if (response.contains("kya") || response.contains("what")) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
                String time = dateFormat.format(new Date());
                speak("abhee " + time + " ho raha hai");
                Toast.makeText(this, "abhee " + time + " ho raha hai", Toast.LENGTH_SHORT).show();
                vibe.vibrate(50);
            }
        } else if (response.contains("on") || response.contains("chalu")) {
            if (response.contains("hotspot")) {
                if (ApManager.isApOn(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), "Hotspot is already on", Toast.LENGTH_SHORT).show();
                    speak("hotaspot pahhelee se hee chaaloo hai");
                    errorVibrate();
                } else {
                    ApManager.configApState(getApplicationContext());
                    Toast.makeText(getApplicationContext(), "Hotspot is turned on", Toast.LENGTH_SHORT).show();
                    speak("hotaspot chaaloo kar dee gaee hai");
                    vibe.vibrate(50);
                }
            }
        } else if (response.contains("off") || response.contains("band")) {
            if (response.contains("hotspot")) {
                if (ApManager.isApOn(getApplicationContext())) {
                    ApManager.configApState(getApplicationContext());
                    Toast.makeText(getApplicationContext(), "Hotspot is turned off", Toast.LENGTH_SHORT).show();
                    speak("hotaspot bannd kar dee gaee hai");
                    vibe.vibrate(50);

                } else {
                    Toast.makeText(getApplicationContext(), "Hotspot is already off", Toast.LENGTH_SHORT).show();
                    speak("hotaspot pahhelee se hee bannd hai");
                    errorVibrate();
                }
            }
        } else if (response.contains("call")) {
            if (response.contains("karo") || response.contains("to")) {
                if (response.contains("ashutosh")) {
                    call("0000111111");
                    speak("ashutosh ko kol kiya ja raha hai");
                } else if (response.contains("piyush")) {
                    call("0000111111");
                    speak("piyoosh ko kol kiya ja raha hai");
                } /*else if (response.contains("home") || response.contains("mummy") || response.contains("ghar")){
                    call("8318587748");
                } else if (response.contains("papa")){
                    if (response.contains("office")){
                        call("9452775145");
                    } else {
                        call("9889871300");
                    }
                }*/ else if (response.contains("shubham")) {
                    if (response.contains("home")) {
                        call("0000111111");
                        speak("shubham hom ko kol kiya ja raha hai");
                    } else if (response.contains("jio")) {
                        call("0000111111");
                        speak("shubham jio ko kol kiya ja raha hai");
                    } else {
                        call("0000111111");
                        speak("shubham ko kol kiya ja raha hai");
                    }
                }
            }
        } else {
            errorVibrate();
            Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
        }
    }


        /*if (response.contains("समय")){
            if (response.contains("क्या") || response.contains("कितना")) {
                Toast.makeText(getApplicationContext(), new Date().toString(), Toast.LENGTH_SHORT).show();
                vibe.vibrate(100);
            }*/
}
