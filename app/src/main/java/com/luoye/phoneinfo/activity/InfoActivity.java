package com.luoye.phoneinfo.activity;

import android.app.ActivityManager;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.luoye.phoneinfo.R;
import com.luoye.phoneinfo.adaper.InfoListAdapter;
import com.luoye.phoneinfo.bean.InfoItem;
import com.luoye.phoneinfo.cpu.CpuBridge;
import com.luoye.phoneinfo.gl.OpenGLRenderer;
import com.luoye.phoneinfo.util.IOUtils;
import com.luoye.phoneinfo.util.InfoUtils;
import com.luoye.phoneinfo.util.Utils;

import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import rebus.permissionutils.AskAgainCallback;
import rebus.permissionutils.FullCallback;
import rebus.permissionutils.PermissionEnum;
import rebus.permissionutils.PermissionManager;

public class InfoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TelephonyManager telephonyManager;
    private  LocationManager locationManager;
    private WifiManager wifiManager;
    private BluetoothManager bluetoothManager;
    private CameraManager cameraManager;
    private SensorManager sensorManager;
    private InfoListAdapter adapter;
    private List<InfoItem> infoItemList;
    private String gpuBrand;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        initService();
        init();
    }

    private  void initService(){
        telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        locationManager=(LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        wifiManager=(WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        bluetoothManager=(BluetoothManager)getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
        cameraManager=(CameraManager)getApplicationContext().getSystemService(Context.CAMERA_SERVICE);
        sensorManager=(SensorManager)getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
    }

    private  void init(){
        infoItemList=new ArrayList<>();

        adapter=new InfoListAdapter(this,infoItemList);
        recyclerView =  findViewById(R.id.info_recyclerview);
        recyclerView.addItemDecoration(new DividerItemDecoration(
                this, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        recyclerView.getItemAnimator().setChangeDuration(300);
        recyclerView.getItemAnimator().setMoveDuration(300);


        registerReceiver(new GLBroadcastReceiver(),new IntentFilter(OpenGLRenderer.ACTION_GL_INFO));

        loadList();
    }

    private void loadList(){
        infoItemList.clear();

        infoItemList.add(new InfoItem("屏幕信息", InfoUtils.getScreenSizeInfo(getWindowManager())));

        infoItemList.add(new InfoItem("设备标识", InfoUtils.getDeviceInfo(this,telephonyManager)));
        infoItemList.add(new InfoItem("Build", InfoUtils.getBuildInfo()));
        infoItemList.add(new InfoItem("SystemProp", InfoUtils.getSystemPropInfo()));
        infoItemList.add(new InfoItem("Wifi信息", InfoUtils.getWifiInfo(wifiManager)));
        infoItemList.add(new InfoItem("Mac地址", InfoUtils.getMacAddressInfo()));
        infoItemList.add(new InfoItem("基站", InfoUtils.getBaseStationInfo(telephonyManager)));
        infoItemList.add(new InfoItem("CPU", InfoUtils.getCpuInfo()));
        infoItemList.add(new InfoItem("内存", InfoUtils.getMemInfo(getApplicationContext())));
        infoItemList.add(new InfoItem("存储", InfoUtils.getStorageInfo()));
        infoItemList.add(new InfoItem("JavaProp", InfoUtils.getJVMPropInfo()));
        try {
            infoItemList.add(new InfoItem("摄像头", InfoUtils.getCameraInfo(cameraManager)));
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        infoItemList.add(new InfoItem("传感器", InfoUtils.getSensorInfo(sensorManager)));

        infoItemList.add(new InfoItem("位置",InfoUtils.requestLocationInfo(locationManager,mLocationListener)));
        adapter.notifyDataSetChanged();
    }

    /**
     * 导出信息
     */
    private  void exportInfo(String file) throws  Exception{
        JSONObject jsonObject=new JSONObject();
        //基本信息
        JSONObject basicInfo=new JSONObject();
        basicInfo.put("imei",telephonyManager.getImei());
        basicInfo.put("serialno",Build.SERIAL);
        basicInfo.put("android_id",Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
        basicInfo.put("subscriberId",telephonyManager.getSubscriberId());
        basicInfo.put("simSerialNumber",telephonyManager.getSimSerialNumber());
        basicInfo.put("model",Build.MODEL);
        basicInfo.put("device",Build.DEVICE);
        basicInfo.put("hardware",Build.HARDWARE);
        if(gpuBrand!=null)
            basicInfo.put("gpu",gpuBrand);
        basicInfo.put("hw_addr",InfoUtils.getMacAddress().get("wlan0"));
        basicInfo.put("platform",InfoUtils.getSystemProp("ro.board.platform"));
        basicInfo.put("brand",Build.BRAND);
        basicInfo.put("arch",InfoUtils.getJVMProp().get("os.arch"));
        basicInfo.put("cpuabi",Build.CPU_ABI);
        basicInfo.put("cpuabi2",Build.CPU_ABI2);
        basicInfo.put("totalmem",InfoUtils.getSystemTotalMemorySize(this)+"");
        basicInfo.put("blockSize",InfoUtils.getDataBockSize()+"");
        basicInfo.put("blockCount",InfoUtils.getDataBockCount()+"");
        basicInfo.put("sensors",InfoUtils.getSensorInfoFormat(sensorManager));
        int[] screen=InfoUtils.getScreenSize(getWindowManager());
        basicInfo.put("screenSize",screen[0]+","+screen[1]);
        //基站信息
        JSONObject baseStation=new JSONObject();
        baseStation.put("lac",InfoUtils.getLac(telephonyManager)+"");
        baseStation.put("cid",InfoUtils.getCid(telephonyManager)+"");


        jsonObject.put("basic_info",basicInfo);
        jsonObject.put("cell",baseStation);

        File f=new File(file);
        if(!f.exists()){
            f.getParentFile().mkdirs();
        }
        IOUtils.writeFile(f,jsonObject.toString());
        toast("导出成功");
    }


    private  class GLBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(OpenGLRenderer.ACTION_GL_INFO)){
                gpuBrand=intent.getStringExtra("GL_RENDERER");//gpu型号
                String glTitle="OpenGL";
                String glInfo="GL_RENDERER：\n"+intent.getStringExtra("GL_RENDERER")+"\n\n"+
                                "GL_VENDOR：\n"+intent.getStringExtra("GL_VENDOR")+"\n\n"+
                                "GL_VERSION：\n"+intent.getStringExtra("GL_VERSION")+"\n\n"+
                                "GL_EXTENSIONS：\n"+intent.getStringExtra("GL_EXTENSIONS")+"\n";
                infoItemList.add(new InfoItem(glTitle,glInfo));
                adapter.notifyDataSetChanged();
            }
        }
    }


    private LocationListener mLocationListener=new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            String loc=location.getLatitude()+","+location.getLongitude()+"\n";
            infoItemList.add(new InfoItem("位置", loc));
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
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
            loadList();
        }
        else if(item.getItemId()==R.id.opengl){
            Intent intent=new Intent(this, GLActivity.class);
            startActivity(intent);
        }
        else if(item.getItemId()==R.id.gps){
            Intent intent=new Intent(this, GpsActivity.class);
            startActivity(intent);
        }
        else if(item.getItemId()==R.id.export_info){
            final EditText editText=new EditText(this);
            editText.setHint("导出路径");
            editText.setText("/sdcard/xky.prop");
            //导出信息
            new AlertDialog.Builder(this)
                    .setTitle("导出信息")
                    .setView(editText)
                    .setPositiveButton("导出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String text=editText.getText().toString();
                            if(!TextUtils.isEmpty(text)){
                                try {
                                    exportInfo(text);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            else{
                                toast("路径不能留空");
                            }
                        }
                    })
                    .setNegativeButton("取消",null)
                    .create()

                    .show();
        }
        else if(item.getItemId()==android.R.id.home){
            finish();
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
