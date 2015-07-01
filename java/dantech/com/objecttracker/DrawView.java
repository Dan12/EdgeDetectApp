package dantech.com.objecttracker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class DrawView extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback{

    private SurfaceHolder mHolder;
    private Camera mCamera;
    private boolean isPreviewRunning = false;
    private volatile byte[] yuvData;
    private Bitmap bmp;
    private int width;
    private int height;
    private long startTime = System.nanoTime();

    private ObjectDetector objectDetector;
    private Paint paint = new Paint();
    private Menu menu;
    private int screenWidth;
    private int screenHeight;
    private boolean sendData = false;

    public DrawView(Context context, AttributeSet attrs) {
        super(context);

        mHolder = getHolder();
        mHolder.addCallback(this);

        objectDetector = new ObjectDetector();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        System.out.println("Draw and Compute");
        long st = System.nanoTime();
        if(yuvData != null) {
            int res = ObjectDetector.resolution;
            int[] rgbints = YUVDecoder.decodeYUV(yuvData, width, height, res);
            yuvData = null;
            bmp = Bitmap.createScaledBitmap(Bitmap.createBitmap(rgbints, 0, width / ObjectDetector.resolution, width / ObjectDetector.resolution, height / ObjectDetector.resolution, Bitmap.Config.RGB_565),width,height,false);
            objectDetector.setDims(width/res,height/res);
            objectDetector.updateRoutine(rgbints);
            sendData = true;
        }
        if(bmp != null)
            canvas.drawBitmap(bmp,0,0,null);
        objectDetector.drawObjectDetector(canvas);
        menu.drawMenu(canvas, screenWidth, screenHeight);
        long et = System.nanoTime();
        System.out.println("Drawing and Computations completed in " + ((et - st) / 1000000) + "ms");
        invalidate();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        synchronized (this) {
            if (isPreviewRunning)
                return;
            this.setWillNotDraw(false); // This allows us to make our own draw
            // calls to this canvas

            mCamera = Camera.open();

            isPreviewRunning = true;
            Camera.Size size = mCamera.getParameters().getPreviewSize();
            width = size.width;
            height = size.height;
            objectDetector.setDims(width/ObjectDetector.resolution, height/ObjectDetector.resolution);
            System.out.println(width + "," + height);
            menu = new Menu(width+40,40,(screenWidth-width)-80);

            mCamera.setParameters(CameraParams.getParameters(mCamera));

            System.out.println("New");

            //comment out if you don't want live preview
            try {
                mCamera.setPreviewDisplay(holder);
            } catch (IOException e) {
                Log.e("Camera", "mCamera.setPreviewDisplay(holder);");
            }

            mCamera.startPreview();
            mCamera.setPreviewCallback(this);

        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        synchronized (this) {
            try {
                if (mCamera != null) {
                    mHolder.removeCallback(this);
                    mCamera.setPreviewCallback(null);
                    mCamera.stopPreview();
                    isPreviewRunning  = false;
                    mCamera.release();
                }
            } catch (Exception e) {
                Log.e("Camera", e.getMessage());
            }
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        System.out.println("Got Preview Data");
        if (!isPreviewRunning)
            return;

        Canvas canvas = null;

        if (mHolder == null) {
            return;
        }

        try {
            synchronized (mHolder) {
                yuvData = data;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // do this in a finally so that if an exception is thrown
            // during the above, we don't leave the Surface in an
            // inconsistent state
            if (canvas != null) {
                mHolder.unlockCanvasAndPost(canvas);
            }
        }

        long endTime = System.nanoTime();
        System.out.print("Time to get pixel data: ");
        System.out.println((endTime - startTime) / 1000000);
        startTime = System.nanoTime();
    }

    public void setScreenDims(int w, int h){
        screenWidth = w;
        screenHeight = h;
    }

    public void touchEvents(MotionEvent e){
        menu.touched(e, this);
    }

    public void setZoom(int v){
        Camera.Parameters p = mCamera.getParameters();
        p.setZoom(v);
        mCamera.setParameters(p);
    }

    public boolean menuOpen(){
        return menu.isOpen();
    }

    public Menu getMenu(){
        return menu;
    }

    public boolean canSendData(){
        return sendData;
    }

    public int getCenterX(){
        sendData = false;
        return 0;
    }

    public ObjectDetector getObjectDetector(){
        return objectDetector;
    }
}
