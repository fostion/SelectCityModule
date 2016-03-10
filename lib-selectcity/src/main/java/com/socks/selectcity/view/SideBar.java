package com.socks.selectcity.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.socks.selectcity.R;
import com.socks.selectcity.utils.PxUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 字母索引
 */
public class SideBar extends View {

    public static String[] INDEX_STRING = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"};

    private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
    private List<String> indexStrings;
    private int choose = -1;
    private Paint paint = new Paint();
    private TextView mTextDialog;
    private int textSize;

    public SideBar(Context context) {
        this(context, null);
    }

    public SideBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SideBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setBackgroundColor(ContextCompat.getColor(getContext(),R.color.side_bg));
        indexStrings = Arrays.asList(INDEX_STRING);
        textSize = PxUtils.sp2px(getContext(),15);
    }

    /**
     * 重写这个方法
     */
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        float singleHeight = (height * 1f) / indexStrings.size();// 获取每一个字母的高度
        singleHeight = (height * 1f - singleHeight / 2) / indexStrings.size();

        for (int i = 0; i < indexStrings.size(); i++) {
            paint.setColor(Color.parseColor("#00BFFF"));
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            paint.setAntiAlias(true);
            paint.setTextSize(textSize);

            if (i == choose) {
                paint.setColor(Color.parseColor("#3399ff"));
                paint.setFakeBoldText(true);
            }

            float xPos = width / 2 - paint.measureText(indexStrings.get(i)) / 2;
            float yPos = singleHeight*i + singleHeight;
            canvas.drawText(indexStrings.get(i), xPos, yPos, paint);
            paint.reset();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();
        final int oldChoose = choose;
        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
        final int c = (int) (y / getHeight() * indexStrings.size());

        switch (action) {
            case MotionEvent.ACTION_UP:
                setBackgroundColor(ContextCompat.getColor(getContext(),R.color.side_bg));
                choose = -1;
                invalidate();
                if (mTextDialog != null) {
                    mTextDialog.setVisibility(View.GONE);
                }
                break;
            default:
                if (oldChoose != c) {
                    if (c >= 0 && c < indexStrings.size()) {
                        if (listener != null) {
                            listener.onTouchingLetterChanged(indexStrings.get(c));
                        }
                        if (mTextDialog != null) {
                            mTextDialog.setText(indexStrings.get(c));
                            mTextDialog.setVisibility(View.VISIBLE);
                        }

                        choose = c;
                        invalidate();
                    }
                }

                break;
        }
        return true;
    }

    public void setIndexText(ArrayList<String> indexStrings) {
        this.indexStrings = indexStrings;
        invalidate();
    }


    public void setTextView(TextView mTextDialog) {
        this.mTextDialog = mTextDialog;
    }

    public void setOnTouchingLetterChangedListener(
            OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    public interface OnTouchingLetterChangedListener {
        void onTouchingLetterChanged(String s);
    }

}