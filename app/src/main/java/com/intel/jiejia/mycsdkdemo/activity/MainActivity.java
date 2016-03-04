package com.intel.jiejia.mycsdkdemo.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.intel.context.Sensing;
import com.intel.context.error.ContextError;
import com.intel.context.exception.ContextProviderException;
import com.intel.context.sensing.InitCallback;
import com.intel.jiejia.mycsdkdemo.adapter.DemoAdapter;
import com.intel.jiejia.mycsdkdemo.adapter.HomeAdapter;
import com.intel.jiejia.mycsdkdemo.R;
import com.intel.jiejia.mycsdkdemo.bean.DemoInfo;
import com.intel.jiejia.mycsdkdemo.listeners.IApplicationListener;
import com.intel.jiejia.mycsdkdemo.listeners.MySensingListener;
import com.intel.jiejia.mycsdkdemo.listeners.ShakingListener;
import com.intel.jiejia.mycsdkdemo.listeners.ShakingUpdateNotifier;
import com.intel.jiejia.mycsdkdemo.service.MyService;
import com.intel.jiejia.mycsdkdemo.service.Service1;
import com.intel.jiejia.mycsdkdemo.service.ShakeService;
import com.intel.jiejia.mycsdkdemo.utils.DividerItemDecoration;
import com.intel.jiejia.mycsdkdemo.utils.SharedPreferencesUtils;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private List<DemoInfo> mDatas;
    private DemoAdapter mAdapter;
    private ShakingListener mShakingListener;
    private Sensing mSensing;
    public static ShakingUpdateNotifier mUpdateNotifier;
    private MyService.MyBinder builder;
    private MyConn conn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter = new DemoAdapter(this, mDatas));
//        mAdapter.setOnItemClickLitener(new HomeAdapter.OnItemClickLitener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                Intent intent = new Intent(MainActivity.this, mDatas.get(position).getClz());
//                startActivity(intent);
//            }
//
//            @Override
//            public void onItemLongClick(View view, int position) {
////                Class service=mDatas.get(position).getClz();
////                if (service==null){
////                    return;}
////                Intent stopIntent = new Intent(MainActivity.this, MyService.class);
////                stopService(stopIntent);
//            }
//        });

    }

    private void initData() {

        mDatas = new LinkedList<>();
        DemoInfo shakeDemo = new DemoInfo(null, "ShakeDemo",
                R.mipmap.ic_launcher, "Shake to quick Launch");
        shakeDemo.setService(ShakeService.class);


        DemoInfo tappingDemo = new DemoInfo(TappingActivity.class,
                "TappingDemo",
                R.mipmap.ic_launcher, "Tap to start application");
        tappingDemo.setService(Service1.class);
        mDatas.add(tappingDemo);
        DemoInfo flickDemo = new DemoInfo(ShotActivity.class, "FlickDemo",
                R.mipmap.ic_launcher, "Flick to shot screen ");
        flickDemo.setService(MyService.class);
        mDatas.add(flickDemo);

        mDatas.add(shakeDemo);

//        conn = new MyConn();
//        Intent server = new Intent(this, MyService.class);
//        bindService(server, conn, BIND_AUTO_CREATE);

    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.id_recyclerview);
        mUpdateNotifier=ShakingUpdateNotifier.getIntance();
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(
                this, DividerItemDecoration.VERTICAL_LIST));
//        mUpdateNotifier=ShakingUpdateNotifier.getIntance();
//        mShakingListener=new ShakingListener(mUpdateNotifier);
//        mSensing = new Sensing(this, new MySensingListener());
//        mUpdateNotifier=ShakingUpdateNotifier.getIntance();
//        mShakingListener=new ShakingListener(mUpdateNotifier);
//        mSensing = new Sensing(this, new MySensingListener());
//
//        startDaemon();
//        try {
//            mSensing.addContextTypeListener(mShakingListener.getContextType(),mShakingListener);
//        } catch (ContextProviderException e) {
//            e.printStackTrace();
//        }
    }
    @Override
    protected void onResume() {
//        startDaemon();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mUpdateNotifier.setIs(true);
                mUpdateNotifier.setmContext(MainActivity.this);
            }
        }, 500);

        super.onResume();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
//        disableProvider(mShakingListener,mSensing);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void startDaemon() {
        mSensing.start(new InitCallback() {
            public void onSuccess() {
                Toast.makeText(getApplicationContext(),
                        "Context Sensing Daemon Started", Toast.LENGTH_SHORT)
                        .show();
                invalidateOptionsMenu();
                enableProvider(mShakingListener);
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
                invalidateOptionsMenu();
            }
        });
    }

    private void enableProvider(IApplicationListener listeners) {
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
    public void disableProvider() {

        try {
            if (!mShakingListener.shouldNotStartSensing()) {
                mSensing.disableSensing(mShakingListener.getContextType());
            }
            mSensing.removeContextTypeListener(mShakingListener);
            mShakingListener.setIsRunning(false);
        } catch (ContextProviderException e) {
            Toast.makeText(getApplicationContext(), "Error removing listener from provider: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

    }
    private class MyConn implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            builder = (MyService.MyBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {


        }

    }
}
