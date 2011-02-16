/*
 * Copyright (C) 2011 nagamatu@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gmail.nagamatu.sample.voicewidget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;

import java.util.ArrayList;

public class VoiceActivity extends Activity {
    public static final String PREFS_NAME = "voice";
    public static final String PREFS_VOICE_RECOGNITION_RESULT = "result";
    public static final String EXTRA_APP_WIDGET_ID = "app_widget_id";
    private static final String TAG = "VoiceWidget";
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 0;
    private int mAppWidgetId;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice_activity);

        Log.d(TAG, "onCreate: " + getIntent().getData());
        mAppWidgetId = Integer.parseInt(getIntent().getData().toString());
        
        final int appWidgetId = getIntent().getIntExtra(EXTRA_APP_WIDGET_ID, -1);
        Log.d(TAG, "onCreate: " + appWidgetId);
    }

    @Override
    public void onResume() {
        super.onResume();
        startVoiceRecognitionActivity();
    }

    /*
    private void kickVoiceRecognition() {
        final SpeechRecognizer recognizer = SpeechRecognizer.createSpeechRecognizer(this);
        recognizer.setRecognitionListener(this);
        final Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "VoiceWidget");
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        recognizer.startListening(intent);
    }
    */

    /**
     * Fire an intent to start the speech recognition activity.
     */
    private void startVoiceRecognitionActivity() {
        final Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "VoiceWidget");
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    /**
     * Handle the results from the recognition activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            saveRecognitionResults(data);
            updateRequestForVoiceWidget();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void saveRecognitionResults(final Intent data) {
        final SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        final Editor editor = prefs.edit();
        final ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        if (matches.size() > 0) {
            editor.putString(PREFS_VOICE_RECOGNITION_RESULT, matches.get(0));
        }
        editor.commit();
        return;
    }

    private void updateRequestForVoiceWidget() {
        if (mAppWidgetId != -1) {
            sendBroadcast(new Intent(this, VoiceWidget.class)
                .setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] { mAppWidgetId }));
        }
        finish();
    }
}