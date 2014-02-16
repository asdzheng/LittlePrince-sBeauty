package com.asd.littleprincesbeauty.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ImageViewSubClass extends ImageView {  
	  
    public ImageViewSubClass(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);  
    }  
  
    public ImageViewSubClass(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
  
    public ImageViewSubClass(Context context) {  
        super(context);  
    }  
      
    @Override  
    protected void onDraw(Canvas canvas) {  
        super.onDraw(canvas);  
          
        //获取控件需要重新绘制的区域  
        Rect rect=canvas.getClipBounds();  
        rect.bottom--;  
        rect.right--;  
        Paint paint=new Paint();  
        paint.setColor(Color.RED);  
        paint.setStyle(Paint.Style.STROKE);  
        paint.setStrokeWidth(3);  
        canvas.drawRect(rect, paint);  
    }  
     
}  
