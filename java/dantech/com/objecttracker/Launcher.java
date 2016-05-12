package dantech.com.objecttracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;

public class Launcher extends Activity {

    private DrawView drawView;
    private BluetoothHandler bt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set this APK Full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Set app to not sleep
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //Set this APK no title
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        RelativeLayout mRelativeLayout = new RelativeLayout(this);
        drawView = new DrawView(this, null);
        drawView.setBackgroundColor(Color.WHITE);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        drawView.setLayoutParams(layoutParams);
        mRelativeLayout.addView(drawView);
        setContentView(mRelativeLayout);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        int height=dm.heightPixels;
        System.out.println(width + " , " + height);
        drawView.setScreenDims(width, height);

        bt = new BluetoothHandler(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if(e.getAction() == MotionEvent.ACTION_DOWN && !drawView.menuOpen()){
            drawView.getObjectDetector().touchDown(e);
        }
        drawView.touchEvents(e);
        if(drawView.getMenu().openBluetooth()){
            Toast.makeText(getApplicationContext(), "Nullifying Current Stream", Toast.LENGTH_SHORT).show();
            if(bt.canStartBT())
                bt.findBT();
            else
                drawView.getMenu().btDone();
        }
        return true;
    }

    public DrawView getDrawView(){
        return drawView;
    }

    protected void onDestroy(){
        System.out.println("Destroyed");
        super.onDestroy();
        System.out.println(isFinishing());
        try {
            bt.closeBT();
        } catch (IOException e) {e.printStackTrace();}
        finish();
    }

    public void launchBluetoothChooser(CharSequence[] devices, final Set<BluetoothDevice> pairedDevices){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick a device");
        builder.setItems(devices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int k = 0;
                for (BluetoothDevice device : pairedDevices) {
                    if(i == k) {
                        bt.tryConnection(device);
                        break;
                    }
                    k++;
                }

            }
        });

        builder.show();
    }

}
