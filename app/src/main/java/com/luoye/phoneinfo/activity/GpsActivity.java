package com.luoye.phoneinfo.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.location.GnssStatus;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.location.OnNmeaMessageListener;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.luoye.phoneinfo.R;

/**
 * Created by zyw on 18-7-19.
 * 显示GPS动态信息的界面
 * 包含NMEA数据，卫星信息等
 */

public class GpsActivity  extends AppCompatActivity{
    private TextView textView;
    private  NmeaMsgListener nmeaMsgListener;
    private  NmeaMessageListener nmeaMessageListener;
    private  GnssStatusCallback gnssStatusCallback;
    private LocationManager locationManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gps_layout);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        textView=(TextView) findViewById(R.id.gps_textview);
        init();
    }

    private  void init(){
        locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N) {
            nmeaMsgListener = new NmeaMsgListener();
            gnssStatusCallback=new GnssStatusCallback();
            locationManager.registerGnssStatusCallback(gnssStatusCallback);
            locationManager.addNmeaListener(nmeaMsgListener);
        }
        else{
            nmeaMessageListener=new NmeaMessageListener();
            locationManager.addNmeaListener(nmeaMessageListener);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * 7.0以下不被支持
     */
    @TargetApi(24)
    private  class GnssStatusCallback extends GnssStatus.Callback {
        @Override
        public void onStarted() {
            super.onStarted();
        }

        @Override
        public void onStopped() {
            super.onStopped();
        }

        @Override
        public void onFirstFix(int ttffMillis) {
            super.onFirstFix(ttffMillis);
        }

        @Override
        public void onSatelliteStatusChanged(GnssStatus status) {
            appendLine("--------卫星实时信息------>");
            appendLine("卫星数："+status.getSatelliteCount()
                    +"\nsvid:"+status.getSvid(0)
                    +"\ncn0s:"+status.getCn0DbHz(0)
                    +"\nAzimuthDegrees(方位角):"+status.getAzimuthDegrees(0)
                    +"\nElevationDegrees(海拔角度):"+status.getElevationDegrees(0)
            );
        }
    }

    /**
     * 7.0以下不被支持
     */
    @TargetApi(24)
    private class NmeaMsgListener implements OnNmeaMessageListener {

        @Override
        public void onNmeaMessage(String message, long timestamp) {
            appendLine("--------NMEA数据------>");
            appendLine(message);
        }
    }
    /**
     * 7.0以下使用
     */
    private  class NmeaMessageListener implements GpsStatus.NmeaListener{

        @Override
        public void onNmeaReceived(long timestamp, String nmea) {
            appendLine("--------NMEA数据------>");
            appendLine(nmea);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if(locationManager!=null) {

            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N) {
                if(nmeaMsgListener!=null) {
                    locationManager.removeNmeaListener(nmeaMsgListener);
                }
            }
            else{
                if(nmeaMessageListener!=null){
                    locationManager.removeNmeaListener(nmeaMessageListener);
                }
            }
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N) {
                if(gnssStatusCallback!=null)
                    locationManager.unregisterGnssStatusCallback(gnssStatusCallback);
            }
        }
    }

    private  void appendLine(String text){
        textView.append(text+"\n");
    }
}
