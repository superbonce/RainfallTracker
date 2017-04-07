package com.tdot.rainfalltrackerfree.model;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

public class SymbolsImageView extends ImageView{
/*
 * TG Nov 14 draw cross on image
 */
	private Paint currentPaint;
	// Public access to cross points
	public float x1=0,x2=0,x3=0,x4=0;
	public float y1=0,y2=0,y3=0,y4=0;
	public int col=0xFF000000;
	public int wid=5;
	//And the width and height of this drawable
	public int width,height;
	    
    public SymbolsImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        currentPaint = new Paint();
        currentPaint.setDither(true);
        currentPaint.setColor(col);  // alpha.r.g.b
        currentPaint.setStyle(Paint.Style.STROKE);
        currentPaint.setStrokeJoin(Paint.Join.ROUND);
        currentPaint.setStrokeCap(Paint.Cap.ROUND);
        currentPaint.setStrokeWidth(2);
    }
    
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        width=r;
        height=b;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        currentPaint.setColor(col);
        canvas.drawLine(x1,y1,x2,y2, currentPaint);
        canvas.drawLine(x3,y3,x4,y4, currentPaint);
        //currentPaint.setStrokeWidth(wid);
        //canvas.drawCircle(x1, y3, 20, currentPaint);
        //currentPaint.setStrokeWidth(2);

    }

}
