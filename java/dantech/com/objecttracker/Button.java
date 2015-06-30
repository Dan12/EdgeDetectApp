package dantech.com.objecttracker;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

public class Button implements MenuItem {

    //TODO: add placement, touch, and draw code

    private int val;
    private Paint paint  = new Paint();
    private String name;

    public Button(){

    }

    @Override
    public void drawItem(Canvas canvas) {

    }

    @Override
    public void touchItem(MotionEvent e) {

    }

    @Override
    public float getValue() {
        return 0;
    }
}
