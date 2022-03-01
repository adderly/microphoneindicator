package com.codemachine00.micuv;

public interface AudioIndicatorCallback {
    void onBufferAvailable(byte[] buffer, int size);
}
