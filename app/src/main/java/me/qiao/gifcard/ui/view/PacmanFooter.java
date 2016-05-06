package me.qiao.gifcard.ui.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by Qiao on 2016/4/26.
 * function：
 */
public class PacmanFooter extends View{
    private Paint mPaint;

    private float translateX;
    private int alpha;
    private float degrees;

    private ValueAnimator ciclerAnim;

    private float x,y,width;
    private RectF rectF;

    public PacmanFooter(Context context) {
        super(context);

        mPaint=new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize;

        Resources r = Resources.getSystem();
        if (heightMode == MeasureSpec.UNSPECIFIED || heightMode == MeasureSpec.AT_MOST) {
            heightSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, r.getDisplayMetrics());
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if(changed) {
            x = getWidth() / 2;
            y = getHeight() / 2;
            width = Math.min(x, y);
            final float circle = width/1.7f;
            rectF=new RectF(x-circle,y-circle,x+circle,y+circle);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPacman(canvas,mPaint);
        drawCircle(canvas,mPaint);
    }

    private void drawPacman(Canvas canvas,Paint paint){
        paint.setAlpha(255);
        canvas.drawArc(rectF,degrees,360-2*degrees,true,paint);
    }

    private void drawCircle(Canvas canvas, Paint paint) {
        paint.setAlpha(alpha);
        canvas.drawCircle(translateX, y, width/4, paint);
    }

    public void startAnim(){
        if(ciclerAnim==null) {
            ciclerAnim=ValueAnimator.ofFloat(1,0);
            ciclerAnim.setDuration(650);
            ciclerAnim.setInterpolator(new LinearInterpolator());
            ciclerAnim.setRepeatCount(-1);
            ciclerAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float ratio = (float) animation.getAnimatedValue();
                    translateX = x + width*2*ratio;
                    alpha = (int)(122*(1+ratio));
                    degrees = Math.abs(0.5f-ratio)*90f;
                    postInvalidate();
                }
            });
        }
        ciclerAnim.start();
    }

    public void stopAnim(){
        if(ciclerAnim!=null) ciclerAnim.cancel();
    }
}
