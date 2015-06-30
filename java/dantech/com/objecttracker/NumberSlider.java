package dantech.com.objecttracker;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

public class NumberSlider implements MenuItem{

    private float val;
    private Paint paint  = new Paint();
    private String name;
    private float minVal;
    private float maxVal;
    private boolean intValue;

    public NumberSlider(int x, int y){

    }

    @Override
    public void drawItem(Canvas canvas) {

    }

    @Override
    public void touchItem(MotionEvent e) {

    }

    @Override
    public int getValue() {
        return 0;
    }
}
