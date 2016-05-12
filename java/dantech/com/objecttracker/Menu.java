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
    private boolean openBluetooth;

    public Menu(int x, int y, int d){
        xIconPos = x;
        yIconPos = y;
        iconDim = d;
        xOffset = d/6;
        yOffset = d/5;
        menuItems = new ArrayList<MenuItem>();
        menuItems.add(new NumberSlider("Tolerance",10,90,35,true,100,50,150,50));
        menuItems.add(new NumberSlider("Resolution",1,16,4,true,100,175,150,50));
        menuItems.add(new NumberSlider("Min Shape Dim",1,40,4,true,100,300,150,50));
        menuItems.add(new NumberSlider("Min Shape Dens",0,1,0.5f,false,350,50,150,50));
        menuItems.add(new NumberSlider("Shape Dens Check", 1, 16, 10, true, 350, 175, 150, 50));
        menuItems.add(new NumberSlider("Zoom", 1, 30, 1, true, 350, 300, 150, 50));
        menuItems.add(new Button("Exit Menu", 100, 414, false));
        menuItems.add(new Button("Open BT", 350, 414, false));
        menuOpen = false;
        openBluetooth = false;
    }

    private void drawMenuIcon(Canvas canvas){
        paint.setColor(Color.GRAY);
        canvas.drawRect(xIconPos, yIconPos, xIconPos + iconDim, yIconPos + iconDim, paint);
        paint.setColor(Color.LTGRAY);
        canvas.drawRect(xIconPos + xOffset, yIconPos + yOffset / 2, xIconPos + iconDim - xOffset, yIconPos + yOffset / 2 + yOffset, paint);
        canvas.drawRect(xIconPos + xOffset, yIconPos + yOffset * 2, xIconPos + iconDim - xOffset, yIconPos + yOffset * 3, paint);
        canvas.drawRect(xIconPos + xOffset, yIconPos + yOffset * 3 + yOffset / 2, xIconPos + iconDim - xOffset, yIconPos + yOffset * 4 + yOffset / 2, paint);
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

    public void touched(MotionEvent e, DrawView drawView){
        if(!menuOpen){
            if(e.getX() >= xIconPos && e.getX() <= xIconPos+iconDim && e.getY() >= yIconPos && e.getY() <= yIconPos+iconDim){
                menuOpen = true;
            }
        }
        else{
            //0-tolerance, 1-resolution, 2-Min Dim, 3-Min Dens, 4-Dens Check, 5-zoom, 6-exit, 7-bluetooth
            for(int i = 0; i < menuItems.size(); i++){
                if(menuItems.get(i).touchItem(e)){
                    switch(i){
                        case 0:
                            ObjectDetector.tolerance = (int) menuItems.get(0).getValue();
                            break;
                        case 1:
                            ObjectDetector.resolution = (int) menuItems.get(1).getValue();
                            break;
                        case 2:
                            ObjectDetector.minShapeDim = (int) menuItems.get(2).getValue();
                            break;
                        case 3:
                            ObjectDetector.minShapeDensity = menuItems.get(3).getValue();
                            break;
                        case 4:
                            ObjectDetector.shapeDensCheck = (int) menuItems.get(4).getValue();
                            break;
                        case 5:
                            drawView.setZoom((int) menuItems.get(5).getValue());
                            break;
                        case 6:
                            closeMenu();
                            break;
                        case 7:
                            if(menuItems.get(7).getValue() == 0 && e.getAction() == MotionEvent.ACTION_DOWN){
                                openBluetooth = true;
                                //menuItems.get(7).setValue(1);
                            }
                            break;
                    }
                }
            }
        }
    }

    private void closeMenu(){
        System.out.println("Close");
        menuOpen = false;
    }

    public boolean isOpen() {
        return menuOpen;
    }

    public void btDone(){
        menuItems.get(7).setValue(0);
    }

    public boolean openBluetooth(){
        if(openBluetooth){
            openBluetooth = false;
            return true;
        }
        return false;
    }
}
