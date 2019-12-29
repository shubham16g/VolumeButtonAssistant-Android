package com.shubham16.volumeassistant;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import androidx.annotation.Nullable;

public class AService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private SpeechRecognizer speechRecognizer;
    private Intent recIntent;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecognizer.setRecognitionListener(recognitionListener);
        } else {

        }

        recIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en-US");
        recIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN");
        recIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);

        return START_STICKY;
    }

    private RecognitionListener recognitionListener = new RecognitionListener() {
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
        public void onError(int i) {
//
        }

        @Override
        public void onResults(Bundle bundle) {
//
        }

        @Override
        public void onPartialResults(Bundle bundle) {

        }

        @Override
        public void onEvent(int i, Bundle bundle) {

        }
    };
}
