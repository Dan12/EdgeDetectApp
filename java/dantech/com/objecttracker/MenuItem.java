package dantech.com.objecttracker;

import android.graphics.Canvas;
import android.view.MotionEvent;

public interface MenuItem {

    int textSize = 34;

    public void drawItem(Canvas canvas);

    public void touchItem(MotionEvent e);

    public float getValue();
}
