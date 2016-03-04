package com.intel.jiejia.mycsdkdemo.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.intel.context.Sensing;
import com.intel.context.error.ContextError;
import com.intel.context.exception.ContextProviderException;
import com.intel.context.sensing.InitCallback;
import com.intel.jiejia.mycsdkdemo.R;
import com.intel.jiejia.mycsdkdemo.activity.DialogActivity;
import com.intel.jiejia.mycsdkdemo.activity.ShotApplication;
import com.intel.jiejia.mycsdkdemo.listeners.FlickListener;
import com.intel.jiejia.mycsdkdemo.listeners.IApplicationListener;
import com.intel.jiejia.mycsdkdemo.listeners.MySensingListener;
import com.intel.jiejia.mycsdkdemo.listeners.ShakingListener;
import com.intel.jiejia.mycsdkdemo.utils.TipHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;

public class Service1 extends Service
{
    private LinearLayout mFloatLayout = null;
    private WindowManager.LayoutParams wmParams = null;
    private WindowManager mWindowManager = null;
    private LayoutInflater inflater = null;
    private ImageButton mFloatView = null;

    private static final String TAG = "MainActivity";

    private SimpleDateFormat dateFormat = null;
    private String strDate = null;
    private String pathImage = null;
    private String nameImage = null;

    private MediaProjection mMediaProjection = null;
    private VirtualDisplay mVirtualDisplay = null;
    private IApplicationListener.UpdateNotifier mUpdateNotifier;
    public static int mResultCode = 0;
    public static Intent mResultData = null;
    public static MediaProjectionManager mMediaProjectionManager1 = null;

    private WindowManager mWindowManager1 = null;
    private int windowWidth = 0;
    private int windowHeight = 0;
    private ImageReader mImageReader = null;
    private DisplayMetrics metrics = null;
    private int mScreenDensity = 0;
    private Sensing mFlickSensing;
    private FlickListener mFlickListener;
    private Vibrator vibrator;
    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        super.onCreate();

        createFloatView();

        createVirtualEnvironment();

        setListeners();
        mFloatView.setVisibility(View.GONE);
    }

    private void setListeners() {
        mUpdateNotifier = new IApplicationListener.UpdateNotifier() {

            @Override
            public void notifyUpdate(final String info) {
//                mFloatView.setVisibility(View.INVISIBLE);
                startVirtual();
//                Handler handler1 = new Handler();
//                handler1.postDelayed(new Runnable() {
//                    public void run() {
//                        //start virtual
//                        startVirtual();
//                    }
//                }, 100);
//                startCapture();
//                Handler handler2 = new Handler();
//                handler2.postDelayed(new Runnable() {
//                    public void run() {
//                        //capture the screen
//                        startCapture();
//                    }
//                }, 500);
                startCapture();
                mFloatView.setVisibility(View.VISIBLE);
                vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                long[] pattern = {80, 50, 40, 30}; // OFF/ON/OFF/ON...
                vibrator.vibrate(pattern, 2);
                Handler handler3 = new Handler();
                handler3.postDelayed(new Runnable() {
                    public void run() {
                        vibrator.cancel();
                        mFloatLayout.setVisibility(View.GONE);
                        mFloatView.setVisibility(View.GONE);
                        //stopVirtual();

                    }
                }, 3000);


            }

            @Override
            public void notifyError(final String error) {
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
            }
        };
        mFlickListener=new FlickListener(mUpdateNotifier);
        mFlickSensing=new Sensing(this, new MySensingListener());
        startDaemon(mFlickSensing, mFlickListener);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO Auto-generated method stub
        return null;
    }

    private void createFloatView()
    {
        wmParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager)getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        wmParams.type = LayoutParams.TYPE_PHONE;
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.CENTER;
        wmParams.x = 0;
        wmParams.y = 0;
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        inflater = LayoutInflater.from(getApplication());
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_layout, null);
        mWindowManager.addView(mFloatLayout, wmParams);
        mFloatView = (ImageButton)mFloatLayout.findViewById(R.id.float_id);

        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        mFloatView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                wmParams.x = (int) event.getRawX() - mFloatView.getMeasuredWidth() / 2;
                wmParams.y = (int) event.getRawY() - mFloatView.getMeasuredHeight() / 2 - 25;
                mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                return false;
            }
        });

        mFloatView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // hide the button
                mFloatView.setVisibility(View.GONE);
                mFloatLayout.setVisibility(View.GONE);

//                Handler handler1 = new Handler();
//                handler1.postDelayed(new Runnable() {
//                    public void run() {
//                        //start virtual
//                        startVirtual();
//                    }
//                }, 50);
//
//                Handler handler2 = new Handler();
//                handler2.postDelayed(new Runnable() {
//                    public void run() {
//                        //capture the screen
//                        startCapture();
//                    }
//                }, 150);
//
//                Handler handler3 = new Handler();
//                handler3.postDelayed(new Runnable() {
//                    public void run() {
//                        mFloatView.setVisibility(View.VISIBLE);
//                        //stopVirtual();
//                    }
//                }, 3000);
            }
        });

        Log.i(TAG, "created the float sphere view");
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void createVirtualEnvironment(){
        dateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
        strDate = dateFormat.format(new java.util.Date());
        pathImage = Environment.getExternalStorageDirectory().getAbsolutePath() + "/screenshots/";
        nameImage = pathImage+strDate+".png";
        mMediaProjectionManager1 = (MediaProjectionManager)getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        mWindowManager1 = (WindowManager)getApplication().getSystemService(Context.WINDOW_SERVICE);
        windowWidth = mWindowManager1.getDefaultDisplay().getWidth();
        windowHeight = mWindowManager1.getDefaultDisplay().getHeight();
        metrics = new DisplayMetrics();
        mWindowManager1.getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;
        mImageReader = ImageReader.newInstance(windowWidth, windowHeight, 0x1, 2); //ImageFormat.RGB_565

        Log.i(TAG, "prepared the virtual environment");
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void startVirtual(){
        if (mMediaProjection != null) {
            Log.i(TAG, "want to display virtual");
            virtualDisplay();
        } else {
            Log.i(TAG, "start screen capture intent");
            Log.i(TAG, "want to build mediaprojection and display virtual");
            setUpMediaProjection();
            virtualDisplay();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setUpMediaProjection(){
        mResultData = ((ShotApplication)getApplication()).getIntent();
        mResultCode = ((ShotApplication)getApplication()).getResult();
        mMediaProjectionManager1 = ((ShotApplication)getApplication()).getMediaProjectionManager();
        mMediaProjection = mMediaProjectionManager1.getMediaProjection(mResultCode, mResultData);
        Log.i(TAG, "mMediaProjection defined");
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void virtualDisplay(){
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("screen-mirror",
                windowWidth, windowHeight, mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader.getSurface(), null, null);
        Log.i(TAG, "virtual displayed");
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startCapture(){
        strDate = dateFormat.format(new java.util.Date());
        nameImage = pathImage+strDate+".png";

        Image image = mImageReader.acquireLatestImage();
        if (image==null)
            return;
        int width = image.getWidth();
        int height = image.getHeight();
        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
        image.close();
        Log.i(TAG, "image data captured");
        File destDir = new File(pathImage);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        if(bitmap != null) {
            try{
                File fileImage = new File(nameImage);
                if(!fileImage.exists()){
                    fileImage.createNewFile();
                    Log.i(TAG, "image file created");
                }
                FileOutputStream out = new FileOutputStream(fileImage);
                if(out != null){
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.flush();
                    out.close();
                    Intent media = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri contentUri = Uri.fromFile(fileImage);
                    media.setData(contentUri);
                    this.sendBroadcast(media);
                    Log.i(TAG, "screen image saved");
                    TipHelper.PlaySound(Service1.this);
                    mFloatView.setImageURI(contentUri);
                    mFloatLayout.setVisibility(View.VISIBLE);
                    mFloatView.setVisibility(View.VISIBLE);

//                    Intent intent =new Intent(Service1.this, DialogActivity.class);
//                    intent.putExtra("uri",contentUri.toString());
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
                }
            }catch(FileNotFoundException e) {
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void tearDownMediaProjection() {
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
        Log.i(TAG, "mMediaProjection undefined");
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void stopVirtual() {
        if (mVirtualDisplay == null) {
            return;
        }
        mVirtualDisplay.release();
        mVirtualDisplay = null;
        Log.i(TAG, "virtual display stopped");
    }

    @Override
    public void onDestroy()
    {
        disableProvider(mFlickListener,mFlickSensing);
        // to remove mFloatLayout from windowManager
        super.onDestroy();
        if(mFloatLayout != null)
        {
            mWindowManager.removeView(mFloatLayout);
        }
        tearDownMediaProjection();
        Log.i(TAG, "application destroy");
    }

    private void startDaemon(final Sensing mSensing, final IApplicationListener mListening) {
        mSensing.start(new InitCallback() {
            public void onSuccess() {
                Toast.makeText(getApplicationContext(),
                        "Context Sensing Daemon Started", Toast.LENGTH_SHORT)
                        .show();
//                try {
//                    mSensing.enableSensing(mListening.getContextType(), mListening.getProviderOptionsBundle());
//                } catch (ContextProviderException e) {
//                    e.printStackTrace();
//                }
                enableProvider(mSensing, mListening);
                /*
                 * After successfully starting the Context Sensing Daemon, we
                 * can enable the sensing of context states such as activity
                 * recognition, location, etc.
                 */


            }

            public void onError(ContextError error) {
                Toast.makeText(getApplicationContext(),
                        "Error: " + error.getMessage(), Toast.LENGTH_LONG)
                        .show();
            }
        });
        enableProvider(mSensing, mListening);
    }
    private void enableProvider(Sensing mSensing,IApplicationListener listeners) {
        try {
            mSensing.addContextTypeListener(listeners.getContextType(), listeners);
            mSensing.enableSensing(listeners.getContextType(), listeners.getProviderOptionsBundle());
        } catch (ContextProviderException e) {
            Toast.makeText(getApplicationContext(), "Error adding listener to provider: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            Log.e("jj", "Error adding listener: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            Toast.makeText(getApplicationContext(), "Error adding listener to provider: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            Log.e("jj", "Error adding listener: " + e.getMessage());
        }
    }
    private void disableProvider(IApplicationListener listener,Sensing mySensing) {

        try {

            if (!listener.shouldNotStartSensing()) {
                mySensing.disableSensing(listener.getContextType());
            }
            mySensing.removeContextTypeListener(listener);
            listener.setIsRunning(false);
        } catch (ContextProviderException e) {
            Toast.makeText(getApplicationContext(), "Error removing listener from provider: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

    }
}