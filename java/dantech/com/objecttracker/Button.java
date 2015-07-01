package dantech.com.objecttracker;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

public class Button implements MenuItem {

    private int val;
    private Paint paint  = new Paint();
    private String name;
    private int xPos;
    private int yPos;
    private int width;
    private int height;
    private int padding = 8;
    private boolean toggleAble;

    public Button(String n, int x, int y, boolean t){
        xPos = x;
        yPos = y;
        name = n;
        paint.setTextSize(textSize);
        width = (int) paint.measureText(n)+padding*2;
        height = textSize+padding;
        toggleAble = t;
        val = 0;
    }

    @Override
    public void drawItem(Canvas canvas){
        if(val == 0) {
            paint.setColor(Color.DKGRAY);
            canvas.drawRect(xPos, yPos, xPos + width, yPos + height, paint);
            paint.setColor(Color.LTGRAY);
            canvas.drawText(name, xPos + padding, yPos + height - padding, paint);
        }
        else{
            paint.setColor(Color.LTGRAY);
            canvas.drawRect(xPos, yPos, xPos + width, yPos + height, paint);
            paint.setColor(Color.DKGRAY);
            canvas.drawText(name, xPos + padding, yPos + height - padding, paint);
        }
    }

    @Override
    public boolean touchItem(MotionEvent e) {
        if(e.getX() >= xPos && e.getX() <= xPos+width && e.getY() >= yPos && e.getY() <= yPos+height){
            if(toggleAble){
                if(val == 0)
                    val = 1;
                else
                    val = 0;
            }
            return true;
        }
        return false;
    }

    @Override
    public float getValue() {
        return val;
    }

    @Override
    public void setValue(float v) {
        val = Math.round(v);
    }
}
