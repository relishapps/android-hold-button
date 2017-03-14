package io.relish.rmholdbutton;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by dansinclair on 18/04/16.
 */
public class RMHoldButton extends FrameLayout {

    private final static String TAG = "RMHoldButton";

    private float progress = 0;
    private int progressTime = 10; // Milliseconds

    private Context context;
    private Timer timer;
    private MyTimerTask myTimerTask;
    private RMHoldButtonProgressCallback mCallback;
    private boolean finished = false;

    private Button button;
    private View slideView;
    private TextView slideViewText;

    private int backgroundColor = Color.WHITE;
    private int animationDuration = 2000;
    private int slideColor = Color.RED;
    private int slideTextColor = Color.WHITE;
    private int cornerRadius = 10;
    private int borderWidth = 3;
    private String text;
    private int textColor = Color.RED;
    private int textSize = 20;
    private Typeface typeface = Typeface.DEFAULT;

    private Path path = new Path();
    private RectF rect = new RectF();


    public RMHoldButton(Context context) {
        this(context, null);
        this.setOnLongClickListener(longClickListener);
    }

    public RMHoldButton(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public RMHoldButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        getAttr(context, attrs);
        init();
        this.setOnLongClickListener(longClickListener);
    }

    public RMHoldButton(Context context, int slideColor, RMHoldButtonProgressCallback callback) {
        super(context);
        this.context = context;
        this.slideColor = slideColor;
        this.mCallback = callback;
        this.setOnLongClickListener(longClickListener);
        init();
    }

    private void getAttr(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RMHoldButton, 0, 0);
        backgroundColor = a.getColor(R.styleable.RMHoldButton_backgroundColor, backgroundColor);
        animationDuration = a.getInt(R.styleable.RMHoldButton_animationDuration, animationDuration);
        slideColor = a.getColor(R.styleable.RMHoldButton_slideColor, slideColor);
        slideTextColor = a.getColor(R.styleable.RMHoldButton_slideTextColor, slideTextColor);
        cornerRadius = a.getInt(R.styleable.RMHoldButton_cornerRadius, cornerRadius);
        borderWidth = a.getInt(R.styleable.RMHoldButton_borderWidth, borderWidth);
        text = a.getString(R.styleable.RMHoldButton_text);
        textSize = a.getInt(R.styleable.RMHoldButton_textSize, textSize);
    }

    private void init() {
        inflate(context, R.layout.rmholdbutton, this);

        button = (Button) findViewById(R.id.button);
        slideView = findViewById(R.id.slideView);
        slideViewText = (TextView) findViewById(R.id.slideViewText);

        button.setOnLongClickListener(this.longClickListener);
        GradientDrawable buttonBG = new GradientDrawable();
        buttonBG.setColor(backgroundColor); // Changes this drawable to use a single color instead of a gradient
        buttonBG.setCornerRadius(cornerRadius);
        buttonBG.setStroke(borderWidth, slideColor);
        button.setBackground(buttonBG);
        button.setText(text);
        button.setTextSize(textSize);
        button.setTextColor(textColor);
        button.setTypeface(typeface);

        GradientDrawable slideViewBG = new GradientDrawable();
        slideViewBG.setColor(slideColor); // Changes this drawable to use a single color instead of a gradient
        slideViewBG.setStroke(borderWidth, slideColor);
        slideView.setBackground(slideViewBG);

        slideViewText.setTextColor(slideTextColor);
        slideViewText.setTextSize(textSize);
        slideViewText.setText(text);
        slideViewText.setTypeface(typeface);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // compute the path
        path.reset();
        rect.set(0, 0, w, h);
        path.addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW);
        path.close();

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int save = canvas.save();
        canvas.clipPath(path);
        super.dispatchDraw(canvas);
        canvas.restoreToCount(save);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        ViewGroup.LayoutParams textParams = slideViewText.getLayoutParams();
        textParams.width = widthMeasureSpec;
        textParams.height = heightMeasureSpec;
        slideViewText.setLayoutParams(textParams);

    }

    private OnLongClickListener longClickListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {

            if (timer != null) {
                timer.cancel();
            }

            timer = new Timer();
            myTimerTask = new MyTimerTask();
            progress = 0;
            finished = false;
            timer.schedule(myTimerTask, 0, progressTime);

            return true;
        }
    };

    public void onProgress(float progress) {

        this.progress = progress;

        ViewGroup.LayoutParams slideViewParams = slideView.getLayoutParams();
        slideViewParams.width = (int)progress;
        slideView.setLayoutParams(slideViewParams);

        this.invalidate();
        if (mCallback != null) {
            mCallback.onProgress((int) ((progress * 100) / getTopProgress()));
        }
    }

    public void onError() {

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        progress = 0;
        reset();
        invalidate();
        if (mCallback != null) {
            mCallback.onError(0);
        }
    }

    public void onTimerFinished() {

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        reset();
        if (mCallback != null && finished == false) {
            finished = true;
            mCallback.onFinish((int) ((progress * 100) / getTopProgress()));
        }
    }

    private void reset() {

        if (slideView.getAnimation() != null) {
            slideView.clearAnimation();
        }

        ResizeWidthAnimation anim = new ResizeWidthAnimation(slideView, 0);
        anim.setDuration(animationDuration / 4);
        slideView.startAnimation(anim);

    }

    public void setmCallback(RMHoldButtonProgressCallback callback) {
        this.mCallback = callback;
    }

    private int getTopProgress() {
        return button.getWidth();
    }

    private float getProgressIncrement() {
        float increment = (float)button.getWidth() / ((float)animationDuration / (float)progressTime);
        return increment;
    }

    public void setBackgroundColor(int color) {
        this.backgroundColor = color;
        init();
    }

    public void setTypeface(Typeface tface) {
        this.typeface = tface;
        init();
    }

    public void setTextSize(int size) {
        this.textSize = size;
        init();
    }

    public void setText(String text) {
        this.text = text;
        init();
    }

    public void setTextColor(int color) {
        this.textColor = color;
        init();
    }

    public void setSlideColor(int color) {
        this.slideColor = color;
        init();
    }

    public void setSlideTextColor(int color) {
        this.slideTextColor = color;
        init();
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        invalidate();
    }

    public void setBorderWidth(int width) {
        this.borderWidth = width;
        init();
    }

    public interface RMHoldButtonProgressCallback {
        void onError(int progress);
        void onProgress(int progress);
        void onFinish(int progress);
    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            if (button.isPressed()) {
                if (progress < getTopProgress()) {
                    progress += getProgressIncrement();
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onProgress(progress);
                        }
                    });
                } else {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onTimerFinished();
                        }
                    });
                }
            } else {
                if (progress < getTopProgress()) {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onError();
                        }
                    });
                } else {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onTimerFinished();
                        }
                    });
                }
            }
        }
    }
}
