package dantech.com.objecttracker;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import java.util.ArrayList;

public class Menu {

    private int xIconPos;
    private int yIconPos;
    private int iconDim;
    private ArrayList<MenuItem> menuItems;
    private Paint paint = new Paint();
    private int xOffset;
    private int yOffset;
    private boolean menuOpen;

    public Menu(int x, int y, int d){
        xIconPos = x;
        yIconPos = y;
        iconDim = d;
        xOffset = d/6;
        yOffset = d/6;
        menuItems = new ArrayList<MenuItem>();
        menuItems.add(new NumberSlider());
        menuItems.add(new NumberSlider());
        menuItems.add(new NumberSlider());
        menuOpen = false;
    }

    private void drawMenuIcon(Canvas canvas){
        paint.setColor(Color.GRAY);
        canvas.drawRect(xIconPos, yIconPos, xIconPos + iconDim, yIconPos + iconDim, paint);
        paint.setColor(Color.LTGRAY);
        canvas.drawRect(xIconPos + xOffset, yIconPos + yOffset / 2, xIconPos + iconDim - xOffset, yIconPos + yOffset / 2 + yOffset, paint);
        canvas.drawRect(xIconPos+xOffset, yIconPos+yOffset*2, xIconPos + iconDim-xOffset, yIconPos + yOffset*3, paint);
        canvas.drawRect(xIconPos+xOffset, yIconPos+yOffset*3 +yOffset/2, xIconPos + iconDim-xOffset, yIconPos + yOffset*4 +yOffset/2, paint);
    }

    public void drawMenu(Canvas canvas){
        drawMenuIcon(canvas);
        if(menuOpen){

        }
    }

    public void touched(MotionEvent e){

    }
}
