package dantech.com.objecttracker;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;

public class NumberSlider implements MenuItem{

    private float val;
    private Paint paint  = new Paint();
    private String name;
    private float minVal;
    private float maxVal;
    private boolean intValue;
    private int xPos;
    private int yPos;
    private int width;
    private int height;
    private int sliderX;
    private int padding = 4;

    public NumberSlider(String t, int mi, int ma, float def, boolean i, int x, int y, int w, int h){
        minVal = mi;
        maxVal = ma;
        if(i)
           ma++;
        if(def < minVal || def > maxVal-1)
            val = (minVal+maxVal)/2;
        else
            val = def;
        xPos = x;
        yPos = y;
        width = w;
        height = h;
        sliderX = (int) Functions.map(val, minVal, maxVal, xPos, xPos + width);
        name = t;
        intValue = i;
    }

    @Override
    public void drawItem(Canvas canvas) {
        paint.setColor(Color.LTGRAY);
        canvas.drawRect(xPos-padding, yPos-padding, xPos+width+padding, yPos+height+padding, paint);

        //slider arrow
        paint.setColor(Color.GRAY);
        Path sliderPath = new Path();
        sliderPath.moveTo(sliderX-height/2, yPos);
        sliderPath.lineTo(sliderX+height/2, yPos);
        sliderPath.lineTo(sliderX, yPos+height);
        canvas.drawPath(sliderPath, paint);

        paint.setColor(Color.BLACK);
        paint.setTextSize(textSize/2);
        canvas.drawText(name, xPos, yPos-padding*2, paint);
        paint.setTextSize(textSize);
        canvas.drawText(""+val, xPos+width/2, yPos+height+textSize, paint);
    }

    @Override
    public boolean touchItem(MotionEvent e) {
        if(e.getX() >= xPos && e.getX() <= xPos+width && e.getY() >= yPos && e.getY() <= yPos+height){
            if(intValue)
                val = (int) Functions.map(e.getX(), xPos, xPos + width, minVal, maxVal);
            else
                val = (float) Functions.map(e.getX(), xPos, xPos + width, minVal, maxVal);
            sliderX = (int) e.getX();
            return true;
        }
        return false;
    }

    @Override
    public float getValue() {
        return val;
    }

    @Override
    public void setValue(float v) {}
}
