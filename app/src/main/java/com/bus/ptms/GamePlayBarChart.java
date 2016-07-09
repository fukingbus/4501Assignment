package com.bus.ptms;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import java.util.List;

/**
 * Created by xeonyan on 9/7/2016.
 */
public class GamePlayBarChart extends View {
    Paint textPaint;
    Paint linePaint;
    Paint barPaint;
    float scaleFactor;
    List<Bundle> list;

    public GamePlayBarChart(Context context) {
        super(context);
        init();
    }

    public GamePlayBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GamePlayBarChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    public void setSource(List<Bundle> list){
        this.list = list;
        invalidate();
    }
    private void init(){
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        scaleFactor = metrics.density;
        textPaint = new Paint();
        linePaint = new Paint();
        barPaint = new Paint();
        textPaint.setColor(0xFFC5C5C5);
        barPaint.setColor(getResources().getColor(R.color.colorHoloBlue));
        textPaint.setTextSize(20*scaleFactor);
    }
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(list!=null) {
            int fullHeight = getHeight();
            int padding = (int) (10 * scaleFactor);
            int maxBarHeight = fullHeight - 8 * padding;
            int maxBarBottom = fullHeight - 8 - padding * 3;

            float startX = 0;
            float barWidth = padding * 4;

            for (int i = 0; i < list.size(); i++) {
                // loop normally 5 times to draw 5 bars & texts
                int correct = list.get(i).getInt("correct");
                float barTop = maxBarBottom - (maxBarHeight * (correct * 10) / 100);
                canvas.drawRect(startX, barTop, startX + barWidth, maxBarBottom, barPaint);
                canvas.drawText(list.get(i).getString("duration"), startX, fullHeight - padding - 16, textPaint);
                canvas.drawText(list.get(i).getString("date"), startX, fullHeight, textPaint);
                startX += barWidth * 2;
            }
        }
    }
}
