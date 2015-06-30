package dantech.com.objecttracker;

import android.graphics.Canvas;
import android.view.MotionEvent;

public interface MenuItem {

    public void drawItem(Canvas canvas);

    public void touchItem(MotionEvent e);

    public int getValue();
}
