package com.intel.jiejia.mycsdkdemo.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

import com.intel.jiejia.mycsdkdemo.R;

/**
 * Created by jiejia on 1/26/2016.
 */
public class DialogActivity  extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.shot_layout);
        Uri uri =  Uri.parse(getIntent().getExtras().getString("uri"));
        ImageView imageView=(ImageView)findViewById(R.id.iv_shot_screen);
        imageView.setImageURI(uri);
    }

}