package com.codemachine00.microphoneindicator;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import android.os.Process;

import com.codemachine00.micuv.AudioIndicatorCallback;

public class RecorderHandler {

    private int audioSource = MediaRecorder.AudioSource.DEFAULT;
    private int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    private int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    private int sampleRate = 44100;
    AudioRecord recorder;
    private Thread thread;
    private AudioIndicatorCallback callback;

    public RecorderHandler() {
    }

    public RecorderHandler(AudioIndicatorCallback callback) {
        this.callback = callback;
    }

    public void setCallback(AudioIndicatorCallback callback) {
        this.callback = callback;
    }

    public boolean IsRunning() {
        if (recorder != null) {
            return recorder.getState() == AudioRecord.STATE_INITIALIZED;
        }
        return false;
    }

    public void start() {
        if (thread != null) return;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);

                int minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioEncoding);
                 recorder = new AudioRecord(audioSource, sampleRate, channelConfig, audioEncoding, minBufferSize);

                if (recorder.getState() == AudioRecord.STATE_UNINITIALIZED) {
                    Thread.currentThread().interrupt();
                    return;
                } else {
                    Log.i(RecorderHandler.class.getSimpleName(), "Started.");
                    //callback.onStart();
                }
                byte[] buffer = new byte[minBufferSize];
                recorder.startRecording();

                while (thread != null && !thread.isInterrupted() && recorder.read(buffer, 0, minBufferSize) > 0) {
                    callback.onBufferAvailable(buffer, minBufferSize);
                }
                recorder.stop();
                recorder.release();
            }
        }, RecorderHandler.class.getName());
        thread.start();
    }

    public void stop() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }
}
