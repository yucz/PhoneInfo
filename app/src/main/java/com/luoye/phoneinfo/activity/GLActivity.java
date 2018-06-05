package com.luoye.phoneinfo.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.luoye.phoneinfo.gl.GLSurfaceViewDemo;
import com.luoye.phoneinfo.gl.OpenGLRenderer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by zyw on 18-5-31.
 */

public class GLActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new GLSurfaceViewDemo(this));
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toast("返回主界面查看显卡信息");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private Toast toast;
    private  void toast(CharSequence msg){
        if(toast==null){
            toast=Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT);
        }
        else{
            toast.setText(msg);
        }
        toast.show();
    }
}
