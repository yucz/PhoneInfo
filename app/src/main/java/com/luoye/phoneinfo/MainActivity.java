package com.luoye.phoneinfo;

import android.app.ActivityManager;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.location.GnssStatus;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.OnNmeaMessageListener;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.Html;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.luoye.phoneinfo.activity.GLActivity;
import com.luoye.phoneinfo.activity.GpsActivity;
import com.luoye.phoneinfo.cpu.CpuBridge;
import com.luoye.phoneinfo.gl.OpenGLRenderer;
import com.luoye.phoneinfo.util.IOUtils;
import com.luoye.phoneinfo.util.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
    private CameraManager cameraManager;
    private SensorManager sensorManager;

    private  ScrollView scrollView;
    private  int scrollX,scrollY;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private  void init(){
        textView = (TextView) findViewById(R.id.tv);
        scrollView =(ScrollView) findViewById(R.id.scrollView);
        textView.setText("");
        telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        wifiManager=(WifiManager)getSystemService(Context.WIFI_SERVICE);
        bluetoothManager=(BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        cameraManager=(CameraManager)getSystemService(Context.CAMERA_SERVICE);
        sensorManager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);


        registerReceiver(new GLBroadcastReceiver(),new IntentFilter(OpenGLRenderer.ACTION_GL_INFO));
        PermissionManager.Builder()
                .permission(PermissionEnum.READ_PHONE_STATE,PermissionEnum.ACCESS_FINE_LOCATION,PermissionEnum.ACCESS_COARSE_LOCATION,PermissionEnum.WRITE_EXTERNAL_STORAGE)
                .askAgain(true)
                .askAgainCallback(new AskAgainCallback() {
                    @Override
                    public void showRequestPermission(UserResponse response) {

                    }
                })
                .callback(new FullCallback() {
                    @Override
                    public void result(ArrayList<PermissionEnum> permissionsGranted, ArrayList<PermissionEnum> permissionsDenied, ArrayList<PermissionEnum> permissionsDeniedForever, ArrayList<PermissionEnum> permissionsAsked) {
                        toast("落叶制作");
                        printInfo();
                    }
                })
                .ask(this);

    }

    private  void printInfo()
    {
        printPackageInfo();
        printScreenInfo();
        printTimeInfo();
        printImeiInfo();
        printMacAddrInfo();
        printBasicInfo();
        printWifiInfo();
        printBluetoothInfo();
        printGpsInfo();
        printBaseStationInfo();
        printMemInfo();
        printNativeCpuInfo();
        printCpuInfo();
        printProperties();
        try {
            printCameraInfo();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        printSensorInfo();

    }

    /**
     * 打印传感器信息
     */
    private  void printSensorInfo(){
        appendLine("---------------Sensor--------------");
        StringBuilder stringBuilder=new StringBuilder();
        List<Sensor> sensorList=sensorManager.getSensorList(SensorManager.SENSOR_STATUS_NO_CONTACT);
        appendLine("传感器数量："+sensorList.size()+"\n");
        for(int i=0;i<sensorList.size();i++){
            Sensor sensor=sensorList.get(i);

                stringBuilder.append("> "+sensor.getName()+"("+sensor.getType()+")"+"\n");

        }

        appendLine(stringBuilder.toString());

    }
    /**
     * 打印摄像头信息
     * @throws CameraAccessException
     */
    private  void printCameraInfo() throws CameraAccessException{
        String[] cameraList=cameraManager.getCameraIdList();

        StringBuilder stringBuilder=new StringBuilder();
        appendLine("---------------Camera--------------");
        int idx=0;
        for(String camera:cameraList){
            CameraCharacteristics cameraCharacteristics=cameraManager.getCameraCharacteristics(camera);
            Integer facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
            StreamConfigurationMap streamConfigurationMap = (StreamConfigurationMap) cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Size[] sizes=streamConfigurationMap.getOutputSizes(ImageFormat.JPEG);
            stringBuilder.append((facing==CameraCharacteristics.LENS_FACING_FRONT?"前置摄像头":"后置摄像头")+":"+sizes[0].getWidth()+"x"+sizes[0].getHeight()+"\n");
        }
        appendLine(stringBuilder.toString());
    }

    /**
     * 底层获取cpu信息
     */
    private  void printNativeCpuInfo(){
        appendLine("---------------CPU Native--------------");
        appendLine("cpu架构："+CpuBridge.getCpuStructure());
        appendLine("cpu个数："+CpuBridge.getCpuCount());
        appendLine("cpuid："+CpuBridge.getCpuId());
        appendLine("cpu特性："+CpuBridge.getFeatures());
    }

    /**
     * 打印包的信息
     */
    private void printPackageInfo(){
        appendLine("---------------Package--------------");
        PackageManager packageManager=getPackageManager();
        try {
            PackageInfo packageInfo=packageManager.getPackageInfo(getPackageName(),0);
            appendLine("apk:"+packageInfo.applicationInfo.sourceDir);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印屏幕的信息
     */
    private void printScreenInfo(){
        appendLine("---------------Screen--------------");
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        appendLine("分辨率："+screenHeight+","+screenWidth);
    }

    private  void printTimeInfo(){

        appendLine("---------------Time--------------");
        appendLine("时间戳(1)："+System.currentTimeMillis());
        appendLine("时间戳(2)："+ Calendar.getInstance().getTimeInMillis());
        appendLine("时间戳(3)："+ new Date().getTime());
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateText=simpleDateFormat.format(new Date());
        appendLine("具体时间："+dateText);
    }

    private  void printImeiInfo(){

        appendLine("---------------IMEI--------------");
        appendLine("imei(0):"+telephonyManager.getImei(0));
        appendLine("imei(1):"+telephonyManager.getImei(1));
    }

    private  void printMacAddrInfo(){

        appendLine("---------------MAC--------------");
        try {

            Enumeration<NetworkInterface> enumeration=
                    NetworkInterface.getNetworkInterfaces();
            while(enumeration.hasMoreElements()) {
                NetworkInterface networkInterface=enumeration.nextElement();
                byte[] macBytes =networkInterface.getHardwareAddress();

                if(macBytes==null)
                    continue;
                String mac="";
                for (byte b:macBytes){
                    mac+=String.format("%02X:",b);
                }
                appendLine(networkInterface.getName()+":"+mac.substring(0,mac.length()-1));
            }

        } catch (Exception e) {
            e.printStackTrace();
            textView.append(e.toString());
        }
    }

    /**
     * 打印基本信息
     */
    private  void printBasicInfo(){
        appendLine("---------------Build--------------");
        appendLine("ro.build.id:"+ Build.ID);
        appendLine("ro.build.display.id:"+ Build.DISPLAY);
        appendLine("ro.build.fingerprint:"+ Build.FINGERPRINT);
        appendLine("ro.serialno:"+ Build.SERIAL);
        appendLine("ro.product.brand:"+ Build.BRAND);
        appendLine("ro.product.name:"+ Build.PRODUCT);
        appendLine("ro.product.device:"+ Build.DEVICE);
        appendLine("ro.product.board:"+ Build.BOARD);
        appendLine("ro.product.model:"+ Build.MODEL);
        appendLine("ro.product.manufacturer:"+ Build.MANUFACTURER);
        appendLine("Build.getRadioVersion(基带版本):"+ Build.getRadioVersion());
        appendLine("ro.hardware:"+ Build.HARDWARE);
        appendLine("cpu_abi:"+ Build.CPU_ABI);
        appendLine("cpu_abi2:"+ Build.CPU_ABI2);
        appendLine("---------------Prop Key--------------");
        appendLine("manufacturer(中间层):"+ getNativeProperties("ro.product.manufacturer"));
        appendLine("ro.board.platform:"+getNativeProperties("ro.board.platform"));
        appendLine("ro.board.platform:"+getNativeProperties("ro.product.cpu.abi"));
        appendLine("---------------DeviceInfo--------------");
        appendLine("getSubscrierId:"+telephonyManager.getSubscriberId());
        appendLine("getSimSerialNumber:"+telephonyManager.getSimSerialNumber());
        appendLine("AndroidId:"+ Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
        appendLine("getLine1Number:"+ telephonyManager.getLine1Number());
    }

    private  void printWifiInfo(){
        WifiInfo wifiInfo=wifiManager.getConnectionInfo();
        appendLine("---------------WIFI--------------");
        appendLine("MAC："+ wifiInfo.getMacAddress());
        appendLine("ConnectionInfo BSSID：\n"+ wifiInfo.getBSSID());
        appendLine("SSID："+ wifiInfo.getSSID());
        appendLine("IP："+ Utils.int2Ip(wifiInfo.getIpAddress()));
        appendLine("");
        if (wifiManager.isWifiEnabled()) {
            List scanResults = wifiManager.getScanResults();
            if (scanResults == null || scanResults.size() == 0) {
                return ;
            }
            int i = 0;
            while (i < scanResults.size() && i < 7) {
                ScanResult scanResult = (ScanResult)scanResults.get(i);
                appendLine("BSSID"+"("+scanResult.SSID+")：\n"+scanResult.BSSID);
                appendLine("");
                i++;
            }
        }

    }

    private  void printBluetoothInfo(){
        appendLine("---------------蓝牙--------------");
        try {
            appendLine("" + bluetoothManager.getAdapter().getAddress());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private  void printGpsInfo(){
        appendLine("---------------GPS--------------");

        Location location =locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        int idx=0;

        //locationManager.addNmeaListener(onNmeaMessageListener);
        //locationManager.registerGnssStatusCallback(gnssCallback);
        if(location==null)
        {
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                appendLine("开始GPS定位...");
                try {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 100, mLocationListener);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            else if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                appendLine("开始网络定位...");
                try {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 100, mLocationListener);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            else if(locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER))
            {
                appendLine("开始PASSIVE定位...");
                try {
                    locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 500, 100, mLocationListener);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            else{
                appendLine("");
            }


        }else{
            appendLine("经度(Longitude):"+location.getLongitude());
            appendLine("纬度(Latitude):"+location.getLatitude());
        }
    }

    private  void printBaseStationInfo(){
        appendLine("---------------基站--------------");
        appendLine("NetworkCountryIso:" +telephonyManager.getNetworkCountryIso());
        appendLine("NetworkOperator:" +telephonyManager.getNetworkOperator());
        appendLine("NetworkOperatorName:" +telephonyManager.getNetworkOperatorName());
        appendLine("NetworkType:" +telephonyManager.getNetworkType());
        appendLine("DataState:" +telephonyManager.getDataState());
        appendLine("SimCountryIso:" +telephonyManager.getSimCountryIso());
        appendLine("SimOperator:" +telephonyManager.getSimOperator());
        appendLine("SimOperatorName:" +telephonyManager.getSimOperatorName());
        appendLine("PhoneType:" +(telephonyManager.getPhoneType()==0?"0(CDMA)":"1(GSM)"));
        CellLocation cel = telephonyManager.getCellLocation();
        int phoneType = telephonyManager.getPhoneType();
        //移动联通 GsmCellLocation
        if (phoneType == TelephonyManager.PHONE_TYPE_GSM && cel instanceof GsmCellLocation) {
            GsmCellLocation gsmCellLocation = (GsmCellLocation) cel;
            appendLine("Cid:" +gsmCellLocation.getCid()+"  Lac:"+gsmCellLocation.getLac());

        }
        //电信CdmaCellLocation
        else if(phoneType == TelephonyManager.PHONE_TYPE_CDMA && cel instanceof CdmaCellLocation){
            CdmaCellLocation cdmaCellLocation = (CdmaCellLocation) cel;
            appendLine("基站Id:"+cdmaCellLocation.getBaseStationId());
            //此处参考高德地图
            appendLine("Cid:"+cdmaCellLocation.getSystemId()+" Lac:"+cdmaCellLocation.getNetworkId());
            appendLine("lat:" +cdmaCellLocation.getBaseStationLatitude()+"  long:"+cdmaCellLocation.getBaseStationLongitude());
        }
        else{
            appendLine("SIM卡不可用，CellLocation获取失败");
        }
    }

    private  void printMemInfo(){
        appendLine("---------------内存--------------");
        appendLine("可用:" +getSystemAvailableMemorySize());
        appendLine("最大:" +getSystemTotalMemorySize());
        appendLine("");
        appendLine(IOUtils.readFile(new File("/proc/meminfo")));
    }

    private  void printCpuInfo(){
        appendLine("---------------CPU--------------");
        appendLine("处理器个数:" +Runtime.getRuntime().availableProcessors());
        appendLine("");
        appendLine(IOUtils.readFile(new File("/proc/cpuinfo")));
    }

    /**
     * 最大内存
     * @return
     */
    private String getSystemTotalMemorySize(){
        //获得MemoryInfo对象
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo() ;
        //获得系统可用内存，保存在MemoryInfo对象上
        ActivityManager activityManager=(ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(memoryInfo) ;
        long memSize = memoryInfo.totalMem ;
        //字符类型转换
        String availMemStr = formatFileSize(memSize);

        return availMemStr ;
    }

    /**
     * 可用内存
     * @return
     */
    private String getSystemAvailableMemorySize(){
        //获得MemoryInfo对象
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo() ;
        //获得系统可用内存，保存在MemoryInfo对象上
        ActivityManager activityManager=(ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(memoryInfo) ;
        long memSize = memoryInfo.availMem ;
        //字符类型转换
        String availMemStr = formatFileSize(memSize);

        return availMemStr ;
    }

    /**
     * 调用系统函数，字符串转换 long -String KB/MB
     */
    private String formatFileSize(long size){
        return Formatter.formatFileSize(MainActivity.this, size);
    }

    /**
     * 打印Java Properties
     * @return
     */
    private void printProperties() {
        appendLine("---------------Java prop--------------");

        Properties properties=System.getProperties();
        Enumeration<?> em=properties.propertyNames();
        for (; em.hasMoreElements();){
            String key=(String)em.nextElement();
            appendLine(key+"："+System.getProperty(key));
        }
    }
    /**
     * 中间层获取Properties
     * @param key
     * @return
     */
    private String getNativeProperties(String key) {
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


    private  class GLBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(OpenGLRenderer.ACTION_GL_INFO)){
                appendLine("---------------OpenGL--------------");
                appendLine(
                        "GL_RENDERER：\n"+intent.getStringExtra("GL_RENDERER")+"\n\n"+
                                "GL_VENDOR：\n"+intent.getStringExtra("GL_VENDOR")+"\n\n"+
                                "GL_VERSION：\n"+intent.getStringExtra("GL_VERSION")+"\n\n"+
                                "GL_EXTENSIONS：\n"+intent.getStringExtra("GL_EXTENSIONS")+"\n"

                );
            }
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



    private LocationListener mLocationListener=new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            appendLine("经度："+location.getLongitude());
            appendLine("纬度："+location.getLatitude());
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            appendLine("onStatusChanged："+s);
        }

        @Override
        public void onProviderEnabled(String s) {
            appendLine("onProviderEnabled："+s);
        }

        @Override
        public void onProviderDisabled(String s) {
            appendLine("onProviderDisabled："+s);
        }
    };


    @Override
    protected void onPause() {
        super.onPause();
        if(mLocationListener!=null) {
            locationManager.removeUpdates(mLocationListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.refresh){
            textView.setText("");
            printInfo();
        }
        else if(item.getItemId()==R.id.refresh_prop){
            updateProperties();
            textView.setText("");
            printInfo();
        }
        else if(item.getItemId()==R.id.opengl){
            Intent intent=new Intent(this, GLActivity.class);
            startActivity(intent);
        }
        else if(item.getItemId()==R.id.gps){
            Intent intent=new Intent(this, GpsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
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
