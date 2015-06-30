package dantech.com.objecttracker;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import java.util.ArrayList;

public class Menu {

    //TODO: menu touch: open and adjust items

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
        yOffset = d/5;
        menuItems = new ArrayList<MenuItem>();
        menuItems.add(new NumberSlider("Tolerance",10,60,35,true,100,100,150,50));
        menuItems.add(new NumberSlider("Resolution",1,10,4,true,100,300,150,50));
        menuOpen = false;
    }

    private void drawMenuIcon(Canvas canvas){
        paint.setColor(Color.GRAY);
        canvas.drawRect(xIconPos, yIconPos, xIconPos + iconDim, yIconPos + iconDim, paint);
        paint.setColor(Color.LTGRAY);
        canvas.drawRect(xIconPos + xOffset, yIconPos + yOffset / 2, xIconPos + iconDim - xOffset, yIconPos + yOffset / 2 + yOffset, paint);
        canvas.drawRect(xIconPos + xOffset, yIconPos + yOffset * 2, xIconPos + iconDim - xOffset, yIconPos + yOffset * 3, paint);
        canvas.drawRect(xIconPos+xOffset, yIconPos+yOffset*3 +yOffset/2, xIconPos + iconDim-xOffset, yIconPos + yOffset*4 +yOffset/2, paint);
    }

    public void drawMenu(Canvas canvas, int sw, int sh){
        drawMenuIcon(canvas);
        if(menuOpen){
            paint.setARGB(150, 255, 255, 255);
            canvas.drawRect(0,0,sw,sh,paint);
            for(MenuItem m : menuItems)
                m.drawItem(canvas);
        }
    }

    public void touched(MotionEvent e){
        if(!menuOpen){
            if(e.getX() >= xIconPos && e.getX() <= xIconPos+iconDim && e.getY() >= yIconPos && e.getY() <= yIconPos+iconDim){
                menuOpen = true;
            }
        }
        else{
            for(int i = 0; i < menuItems.size(); i++){
                menuItems.get(i).touchItem(e);
            }
        }
    }
}
