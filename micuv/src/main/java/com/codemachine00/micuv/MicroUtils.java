package com.codemachine00.micuv;

import android.util.Log;

public class MicroUtils {

    public static int getAmplitudeFromBuffer(byte[] data, int read) {
        int cAmplitude= 0;

        for (int i=0; i<read/2; i++) {
            short curSample = getShort(data[i*2], data[i*2+1]);
            if (curSample > cAmplitude) {
                cAmplitude = curSample;
            }
        }

        Log.d("amplitude-w",Integer.toString(cAmplitude));

        return cAmplitude;
    }

    private static short getShort(byte argB1, byte argB2) {
        return (short)(argB1 | (argB2 << 8));
    }

    public static double getRealDecibel(int amplitude) {
        if (amplitude < 0) amplitude *= -1;
        double amp = (((double) amplitude) / 32767.0d) * 100.0d;
        if (amp == 0.0d) {
            amp = 1.0d;
        }
        double decibel = Math.sqrt(100.0d / amp);
        decibel *= decibel;
        if (decibel > 100.0d) {
            decibel = 100.0d;
        }
        return ((-1.0d * decibel) + 1.0d) / Math.PI;
    }

    public static double resizeNumber(double value) {
        int temp = (int) (value * 10.0d);
        return temp / 10.0d;
    }



//    public int[] getAmplitudes() {
//        if (amplitudes == null) amplitudes = getAmplitudesFromBytes(bytes);
//        return amplitudes;
//    }
//
//    public double[] getDecibels() {
//        if (amplitudes == null) amplitudes = getAmplitudesFromBytes(bytes);
//        if (decibels == null) {
//            decibels = new double[amplitudes.length];
//            for (int i = 0; i < amplitudes.length; i++) {
//                decibels[i] = resizeNumber(getRealDecibel(amplitudes[i]));
//            }
//        }
//        return decibels;
//    }

    public int[] getAmplitudeLevels(byte[] bytes) {
        int[] amplitudes = getAmplitudesFromBytes(bytes);

        int major = 0;
        int minor = 0;
        for (int i : amplitudes) {
            if (i > major) major = i;
            if (i < minor) minor = i;
        }
        int amplitude = Math.max(major, minor * (-1));
        return new int[] {major, minor};
    }


    public static int getAmplitude(byte[] bytes) {
        int[] amplitudes = getAmplitudesFromBytes(bytes);

        int major = 0;
        int minor = 0;
        for (int i : amplitudes) {
            if (i > major) major = i;
            if (i < minor) minor = i;
        }
        int amplitude = Math.max(major, minor * (-1));
        return amplitude;
    }

    private static int[] getAmplitudesFromBytes(byte[] bytes) {
        int[] amps = new int[bytes.length / 2];
        for (int i = 0; i < bytes.length; i += 2) {
            short buff = bytes[i + 1];
            short buff2 = bytes[i];

            buff = (short) ((buff & 0xFF) << 8);
            buff2 = (short) (buff2 & 0xFF);

            short res = (short) (buff | buff2);
            amps[i == 0 ? 0 : i / 2] = (int) res;
        }
        return amps;
    }



    private static final float MAX_REPORTABLE_AMP = 32767f;
    private static final float MAX_REPORTABLE_DB = 90.3087f;

    public static float getNormalizedAmplitude(int rawAmplitude) {
        return (float) (MAX_REPORTABLE_DB + (20 * Math.log10(rawAmplitude / MAX_REPORTABLE_AMP)));
    }

}
