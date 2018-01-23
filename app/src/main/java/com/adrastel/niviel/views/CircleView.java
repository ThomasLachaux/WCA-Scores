package com.adrastel.niviel.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.adrastel.niviel.R;

public class CircleView extends View {


    private String text = null;
    private float textSize;
    private int background;


    private Paint textPaint;
    private Paint backgroundPaint;


    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleView, 0, 0);

        try {
            text = array.getString(R.styleable.CircleView_text);
            background = array.getColor(R.styleable.CircleView_backgroundColor, 0x00000000);
            textSize = array.getDimension(R.styleable.CircleView_textSize, 0);
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        finally {
            array.recycle();
        }


        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(Paint.Align.CENTER);

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        int w = canvas.getWidth();
        int h = canvas.getHeight();

        int pl = getPaddingLeft();
        int pr = getPaddingRight();
        int pt = getPaddingTop();
        int pb = getPaddingBottom();

        int useableWidth = w - (pl + pr);
        int useableHeight = h - (pt + pb);

        // Background
        int radius = Math.min(useableWidth, useableHeight) / 2;

        // Centers the background
        int circleX = pl + (useableWidth / 2);
        int circleY = pl + (useableHeight / 2);

        // Text
        // Centers the text
        int textY = (int) ((useableHeight / 2) - ((textPaint.descent() + textPaint.ascent()) / 2));

        backgroundPaint.setColor(background);

        try {
            canvas.drawCircle(circleX, circleY, radius, backgroundPaint);

            canvas.drawText(text, circleX, textY, textPaint);

            canvas.translate(1, 1);

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setText(String text) {
        this.text = text;
        invalidate();
        requestLayout();
    }

    public void setBackground(int background) {
        this.background = background;
        invalidate();
        requestLayout();
    }
}
