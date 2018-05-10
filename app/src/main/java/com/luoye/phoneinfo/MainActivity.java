package com.luoye.phoneinfo;

import android.Manifest;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.GnssStatus;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.OnNmeaMessageListener;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.luoye.phoneinfo.util.Utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import rebus.permissionutils.AskAgainCallback;
import rebus.permissionutils.FullCallback;
import rebus.permissionutils.PermissionEnum;
import rebus.permissionutils.PermissionManager;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private TelephonyManager telephonyManager;
    private  LocationManager locationManager;
    private WifiManager wifiManager;
    private BluetoothManager bluetoothManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.tv);
        textView.setText("");
        telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        wifiManager=(WifiManager)getSystemService(Context.WIFI_SERVICE);
        bluetoothManager=(BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        PermissionManager.Builder()
                .permission(PermissionEnum.READ_PHONE_STATE,PermissionEnum.ACCESS_FINE_LOCATION,PermissionEnum.ACCESS_COARSE_LOCATION)
                .askAgain(true)
                .askAgainCallback(new AskAgainCallback() {
                    @Override
                    public void showRequestPermission(UserResponse response) {

                    }
                })
                .callback(new FullCallback() {
                    @Override
                    public void result(ArrayList<PermissionEnum> permissionsGranted, ArrayList<PermissionEnum> permissionsDenied, ArrayList<PermissionEnum> permissionsDeniedForever, ArrayList<PermissionEnum> permissionsAsked) {
                        getInfo();
                    }
                })
                .ask(this);
    }

    private  void getInfo()
    {
        getPackageInfo();
        getScreenInfo();
        getTimeInfo();
        getImeiInfo();
        getMacAddrInfo();
        getPropInfo();
        getWifiInfo();
        getBluetoothInfo();
        getGpsInfo();
        getBsInfo();
    }

    /**
     * 获取包的信息
     */
    private void getPackageInfo(){
        appendLine("---------------package--------------");
        PackageManager packageManager=getPackageManager();
        try {
            PackageInfo packageInfo=packageManager.getPackageInfo(getPackageName(),0);
            appendLine("apk:"+packageInfo.applicationInfo.sourceDir);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取屏幕的信息
     */
    private void getScreenInfo(){
        appendLine("---------------screen--------------");
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        appendLine("分辨率："+screenHeight+","+screenWidth);
    }

    private  void getTimeInfo(){

        appendLine("---------------time--------------");
        appendLine("时间戳(1)："+System.currentTimeMillis());
        appendLine("时间戳(2)："+ Calendar.getInstance().getTimeInMillis());
        appendLine("时间戳(3)："+ new Date().getTime());
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateText=simpleDateFormat.format(new Date());
        appendLine("具体时间："+dateText);
    }

    private  void getImeiInfo(){

        textView.append("---------------IMEI--------------\n");
        textView.append("imei(0):"+telephonyManager.getImei(0)+"\n");
        textView.append("imei(1):"+telephonyManager.getImei(1)+"\n");
    }

    private  void getMacAddrInfo(){

        textView.append("---------------MAC Address--------------\n");
        try {

            Enumeration<NetworkInterface> enumeration=
                    NetworkInterface.getNetworkInterfaces();
            while(enumeration.hasMoreElements()) {
                NetworkInterface networkInterface=enumeration.nextElement();
                byte[] macBytes =networkInterface.getHardwareAddress();

                if(macBytes==null//||!networkInterface.getName().equals("wlan0")
                        )
                    continue;
                String mac="";
                String mb="";
                for (byte b:macBytes){
                    mac+=String.format("%2X:",b);
                    mb+=(b+":");
                }
                textView.append(networkInterface.getName()+"-MAC:"+mac.substring(0,mac.length()-1)+"\n");
                //textView.append(networkInterface.getName()+"-mb:"+mb+"\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
            textView.append(e.toString());
        }
    }

    private  void getPropInfo(){
        textView.append("---------------Prop--------------\n");
        textView.append("ro.build.id:"+ Build.ID+"\n");
        textView.append("ro.build.display.id:"+ Build.DISPLAY+"\n");
        textView.append("ro.build.fingerprint:"+ Build.FINGERPRINT+"\n");
        textView.append("ro.serialno:"+ Build.SERIAL+"\n");
        textView.append("ro.product.brand:"+ Build.BRAND+"\n");
        textView.append("ro.product.name:"+ Build.PRODUCT+"\n");
        textView.append("ro.product.device:"+ Build.DEVICE+"\n");
        textView.append("ro.product.board:"+ Build.BOARD+"\n");
        textView.append("ro.product.model:"+ Build.MODEL+"\n");
        textView.append("ro.product.manufacturer:"+ Build.MANUFACTURER+"\n");
        appendLine("getSubscrierId:"+telephonyManager.getSubscriberId());
        appendLine("getSimSerialNumber:"+telephonyManager.getSimSerialNumber());
        appendLine("AndroidId:"+ Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
        appendLine("Build.getRadioVersion(基带版本):"+ Html.fromHtml(Build.getRadioVersion()));
        appendLine("getLine1Number:"+ telephonyManager.getLine1Number());
        appendLine("manufacturer(中间层):"+ getProperties("ro.product.manufacturer"));
        appendLine("ro.hardware:"+ Build.HARDWARE);
    }

    private  void getWifiInfo(){
        textView.append("---------------WIFI--------------\n");
        WifiInfo wifiInfo=wifiManager.getConnectionInfo();
        textView.append("MAC:"+ wifiInfo.getMacAddress()+"\n");
        textView.append("BSSID:"+ wifiInfo.getBSSID()+"\n");
        textView.append("SSID:"+ wifiInfo.getSSID()+"\n");
        textView.append("IP:"+ Utils.int2Ip(wifiInfo.getIpAddress())+"\n");
    }

    private  void getBluetoothInfo(){
        appendLine("---------------蓝牙--------------");
        appendLine(""+bluetoothManager.getAdapter().getAddress());
    }

    private  void getGpsInfo(){
        textView.append("---------------GPS--------------\n");

        Location location =locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        int idx=0;

        //locationManager.addNmeaListener(onNmeaMessageListener);
        //locationManager.registerGnssStatusCallback(gnssCallback);
        if(location==null)
        {
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                textView.append("开始GPS定位...\n");
                try {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 100, mLocationListener);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            else if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                textView.append("开始网络定位...\n");
                try {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 100, mLocationListener);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            else if(locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER))
            {
                textView.append("开始PASSIVE定位...\n");
                try {
                    locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 500, 100, mLocationListener);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            else{
                textView.append("\n");
            }


        }else{
            textView.append("经度(Longitude):"+location.getLongitude()+"\n");
            textView.append("纬度(Latitude):"+location.getLatitude()+"\n");
        }
    }

    private  void getBsInfo(){
        appendLine("---------------基站--------------");
        appendLine("NetworkCountryIso:" +telephonyManager.getNetworkCountryIso());
        appendLine("NetworkOperator:" +telephonyManager.getNetworkOperator());
        appendLine("NetworkOperatorName:" +telephonyManager.getNetworkOperatorName());
        appendLine("NetworkType:" +telephonyManager.getNetworkType()+",0(未知)");
        appendLine("DataState:" +telephonyManager.getDataState());
        appendLine("SimCountryIso:" +telephonyManager.getSimCountryIso());
        appendLine("SimOperator:" +telephonyManager.getSimOperator());
        appendLine("SimOperatorName:" +telephonyManager.getSimOperatorName());
        appendLine("PhoneType:" +telephonyManager.getPhoneType());
    }

    /**
     * 中间层获取Properties
     * @param key
     * @return
     */
    private String getProperties(String key) {
        try {
            Class clazz=Class.forName("android.os.SystemProperties");
            Method get=clazz.getDeclaredMethod("get",String.class);
            String value=(String)get.invoke(null,key);
            return value;

        } catch (ClassNotFoundException e) {
            System.out.println("找不到类");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            System.out.println("参数错误");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            System.out.println("目标错误");
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            System.out.println("找不到方法");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 自定义系统才可以获取到
     */
    private void updateProperties() {
        try {
            Class clazz=Class.forName("android.os.Build");
            Method updateProperties=clazz.getDeclaredMethod("updateProperties");
            updateProperties.invoke(null);

        } catch (ClassNotFoundException e) {
            System.out.println("找不到类");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            System.out.println("参数错误");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            System.out.println("目标错误");
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            System.out.println("找不到方法");
            e.printStackTrace();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handleResult(this, requestCode, permissions, grantResults);
    }

    private  void appendLine(String text){
        textView.append(text+"\n");
    }

    private  GnssStatus.Callback gnssCallback=new GnssStatus.Callback() {
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
    };

    private  OnNmeaMessageListener onNmeaMessageListener=new OnNmeaMessageListener() {
        @Override
        public void onNmeaMessage(String s, long l) {
            appendLine(s);
        }
    };

    private LocationListener mLocationListener=new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            textView.append("经度："+location.getLongitude()+"\n");
            textView.append("纬度："+location.getLatitude()+"\n");
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            textView.append("onStatusChanged："+s+"\n");
        }

        @Override
        public void onProviderEnabled(String s) {
            textView.append("onProviderEnabled："+s+"\n");
        }

        @Override
        public void onProviderDisabled(String s) {
            textView.append("onProviderDisabled："+s+"\n");
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(mLocationListener);
        locationManager.removeNmeaListener(onNmeaMessageListener);
        locationManager.unregisterGnssStatusCallback(gnssCallback);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.refresh){
            textView.setText("");
            getInfo();
        }
        else if(item.getItemId()==R.id.refresh_prop){
            updateProperties();
            textView.setText("");
            getInfo();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
}
