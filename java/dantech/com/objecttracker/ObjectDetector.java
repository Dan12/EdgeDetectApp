package dantech.com.objecttracker;

import android.graphics.Canvas;
import android.view.MotionEvent;
import java.util.ArrayList;

public class ObjectDetector {

    public static int resolution = 8;
    public static short tolerance = 35;
    public static int minShapeDim = 4;
    public static float minShapeDensity = 0.5f;
    public static int shapeDensCheck = 10;

    private ProcessedImage pimg;
    private int[] target = new int[]{151, 53, 42};
    private int targetRow = -1;
    private int targetCol = -1;
    private ArrayList<ShapeRectangle> shapes;
    private int width;
    private int height;

    public ObjectDetector(){
        shapes = new ArrayList<ShapeRectangle>();
    }

    public void setDims(int w, int h){
        width = w;
        height = h;
    }

    public void updateRoutine(int[] rgb){
        long st = System.nanoTime();
        if(targetCol != -1 && targetRow != -1) {
            target = Functions.getRGB(rgb, targetRow, targetCol, width);
            targetRow = -1;
            targetCol = -1;
        }
        shapes = EdgeDetect.runRoutine(rgb, target, width, height);
        long et = System.nanoTime();
        System.out.println("Edge Detect run in "+((et-st)/1000000)+"ms");
    }

    public void drawObjectDetector(Canvas canvas){
        for(ShapeRectangle s : shapes)
            s.drawSquare(canvas);
    }

    public void touchDown(MotionEvent e){
        if(e.getX() <= width*ObjectDetector.resolution && e.getY() <= height*ObjectDetector.resolution){
            targetCol = ((int)e.getX())/ObjectDetector.resolution;
            targetRow = ((int)e.getY())/ObjectDetector.resolution;
        }
    }
}
