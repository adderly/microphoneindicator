package com.codemachine00.micuv;

import android.animation.Animator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

public class MicrophoneIndicator extends View {

    protected float targetAmplitude = 0;
    private boolean targetAmplitudeNormalized;
    protected float animatedSegmentAmount = 0;

    private Paint paint = new Paint();
    private int mContentHeight;
    private int mContentWidth;
    private int mPaddingLeft;
    private int mPaddingTop;
    private int mPaddingRight;
    private int mPaddingBottom;

    private int mLeft;
    private int mTop;
    private int mRight;

    final int AMP_MAX = 2000;
    final int FPS = 60;
    ValueAnimator valueAnimator = null;
    final String AMP_ANIM_VALUE = "AMP_ANIM";

    private Handler handler;
    final int SEGMENT_WIDTH = 60;
    final float AMP_SUB = 48f;
    final long ANIM_DURATION = 340;
    private boolean IsInitialized = false;
    private int spacing;
    private int segmentHeight;
    private int segmentColWidth;
    private int segmentAmount;
    private float ratioAmp;
    private double ratioSegment;


    public MicrophoneIndicator(Context context) {
        super(context);
        init(null, 0);
    }

    public MicrophoneIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public MicrophoneIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    protected void init(AttributeSet attrs, int defStyle) {
        paint.setColor(Color.BLACK);
        handler = new Handler(Looper.getMainLooper());
    }

    public void setAmplitude(int ampl) {
        targetAmplitude = ampl;
        targetAmplitudeNormalized = false;
        handler.post(() -> {
            animateValue(ampl);
        });
    }

    public void setAmplitude(float ampl, boolean isNormalized) {
        targetAmplitude = ampl;
        targetAmplitudeNormalized = isNormalized;
        handler.post(() -> {
            animateValue(ampl);
        });
    }

    protected void animateValue(float ampl) {
        if (valueAnimator == null){
            valueAnimator = new ValueAnimator();
            valueAnimator.setDuration(ANIM_DURATION);
            valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

            valueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
//                    targetAmplitude = 0.0f;
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });


            valueAnimator.addUpdateListener( (animator) -> {
                animatedSegmentAmount = (float) animator.getAnimatedValue(AMP_ANIM_VALUE);
                Log.d("lastanimatedAmplitude=", String.valueOf(animatedSegmentAmount));
                invalidate();
            });
        } else {
            valueAnimator.cancel();
        }

        float normalized = targetAmplitudeNormalized ? targetAmplitude: MicroUtils.getNormalizedAmplitude((int)targetAmplitude);
        normalized = Math.max(normalized - AMP_SUB, 1f);
        double r = ratioSegment/ratioAmp;

        float drawSegmentAmount = (float) (normalized * r);

        PropertyValuesHolder valHolder = PropertyValuesHolder.ofFloat(AMP_ANIM_VALUE, animatedSegmentAmount, drawSegmentAmount);
        valueAnimator.setValues(valHolder);
        valueAnimator.start();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setColor(Color.WHITE);

        if (!IsInitialized) {
            mPaddingLeft = getPaddingLeft();
            mPaddingTop = getPaddingTop();
            mPaddingRight = getPaddingRight();
            mPaddingBottom = getPaddingBottom();

            mContentWidth = getWidth() - mPaddingLeft - mPaddingRight;
            mContentHeight = getHeight() - mPaddingTop - mPaddingBottom;

            paint.setColor(Color.GREEN);
            segmentAmount = Math.max(mContentHeight, 1) / 12;

            spacing = 5;
            segmentHeight = (int) (mContentHeight  / segmentAmount);
            segmentColWidth = SEGMENT_WIDTH / 2;

            ratioAmp = 1/AMP_SUB;
            ratioSegment = (double)1/segmentAmount;

            IsInitialized =  true;
        }

        DrawIndicatorAnimated(canvas);
    }

    private void DrawIndicatorAnimated(Canvas canvas) {
        if (animatedSegmentAmount < 2.3f) return; //This value is insignificant

        for (int n = 0;n < animatedSegmentAmount;n++) {
            float percentage = (float) n / segmentAmount;
            //Log.d("amplitudeVal2",  " percentage = "+String.valueOf(percentage) + " segment = "+ String.valueOf(segmentAmount));
            if (percentage < 0.60) {
                paint.setColor(Color.GREEN);
            } else if (percentage < 0.84) {
                paint.setColor(Color.YELLOW);
            } else if (percentage > 0.84) {
                paint.setColor(Color.RED);
            }

            int stepAmount = mContentHeight - n * segmentHeight;
            canvas.drawRect(
                    0,
                    stepAmount,
                    segmentColWidth,
                    stepAmount + segmentHeight - spacing,
                    paint);
            canvas.drawRect(
                    segmentColWidth + spacing,
                    stepAmount,
                    segmentColWidth + segmentColWidth + spacing,
                    stepAmount + segmentHeight - spacing,
                    paint);
        }

    }


    private void DrawIndictor(Canvas canvas) {
        float normalized = targetAmplitudeNormalized ? targetAmplitude: MicroUtils.getNormalizedAmplitude((int)targetAmplitude);
        normalized = Math.max(normalized - AMP_SUB, 1f);
        double r = ratioSegment/ratioAmp;

        double drawSegmentAmount = normalized * r;

        for (int n = 0;n < drawSegmentAmount;n++) {
            float percentage = (float) n / segmentAmount;
            //Log.d("amplitudeVal2",  " percentage = "+String.valueOf(percentage) + " segment = "+ String.valueOf(segmentAmount));
            if (percentage < 0.60) {
                paint.setColor(Color.GREEN);
            } else if (percentage < 0.84) {
                paint.setColor(Color.YELLOW);
            } else if (percentage > 0.84) {
                paint.setColor(Color.RED);
            }

            int stepAmount = mContentHeight - n * segmentHeight;
            canvas.drawRect(
                    0,
                    stepAmount,
                    segmentColWidth,
                    stepAmount + segmentHeight - spacing,
                    paint);
            canvas.drawRect(
                    segmentColWidth + spacing,
                    stepAmount,
                    segmentColWidth + segmentColWidth + spacing,
                    stepAmount + segmentHeight - spacing,
                    paint);
        }

    }
}
