package com.tdot.rainfalltrackerfree.model;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CrossImageView extends ImageView{
/*
 * TG Nov 14 draw cross on image
 */
	private Paint currentPaint;
	// Public access to cross points
	public float x1=0,x2=0,x3=0,x4=0;
	public float y1=0,y2=0,y3=0,y4=0;
	public int col=0xFF000000;
	public int colc=0x99ffffff;
	public int wid=5;
	public float zoom=1.0f;
	private int boxw=60;
	private int boxh=50;
	//And the width and height of this drawable
	public int width,height;
	public List<SymbolData> symbols=null;
	public int cF=0;
	    
    public CrossImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        currentPaint = new Paint();
        currentPaint.setDither(true);
        currentPaint.setColor(col);  // alpha.r.g.b
        currentPaint.setStyle(Paint.Style.STROKE);
        currentPaint.setStrokeJoin(Paint.Join.ROUND);
        currentPaint.setStrokeCap(Paint.Cap.ROUND);
        currentPaint.setStyle(Paint.Style.FILL);
        currentPaint.setStrokeWidth(2);
        //currentPaint.setShadowLayer(5.0f, 0.0f, 2.0f, 0xFF000000);
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
        if(symbols !=null)
        {
        	RectF r=new RectF();
        	currentPaint.setColor(colc);
        	currentPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        for (SymbolData item : symbols) {    
        	currentPaint.setColor(colc);
        	r.left=item.pixX-(boxw*zoom);
        	r.right=item.pixX+(boxw*zoom);
        	r.bottom=item.pixY+(boxh*zoom);
        	r.top=item.pixY-(boxh*zoom);
        	
        
        	
        canvas.drawRoundRect(r, 10, 10, currentPaint);
        currentPaint.setStyle(Paint.Style.STROKE);
        currentPaint.setColor(col);
        canvas.drawRoundRect(r, 10, 10, currentPaint);
        currentPaint.setStyle(Paint.Style.FILL);
        currentPaint.setColor(Color.BLUE); 
        currentPaint.setTextSize(50*zoom); 
        canvas.drawText(item.getPOP(cF)+"%", item.pixX-40, item.pixY+20, currentPaint);
        }
        }

    }

}
