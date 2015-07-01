package dantech.com.objecttracker;

import android.graphics.Canvas;
import android.view.MotionEvent;

public interface MenuItem {

    int textSize = 40;

    public void drawItem(Canvas canvas);

    public boolean touchItem(MotionEvent e);

    public float getValue();

    public void setValue(float v);
}
