package com.luoye.phoneinfo.util;

import android.app.ActivityManager;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Point;
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
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.WindowManager;

import com.luoye.phoneinfo.cpu.CpuBridge;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by zyw on 18-8-7.
 *
 * 本类中带Info字样的方法是返回字符串
 * 否则返回具体数据
 */

public class InfoUtils {

    private static final String DATA_PATH = Environment.getDataDirectory().getAbsolutePath();

    /**
     *  请求位置
     * @param locationManager
     * @param locationListener
     * @return
     */
    public static  String requestLocationInfo(LocationManager locationManager, LocationListener locationListener){

        StringBuilder stringBuilder=new StringBuilder();
        Location location =locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if(location==null)
        {
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                try {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 100, locationListener);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            else if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                try {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 100, locationListener);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            else if(locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER))
            {
                try {
                    locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 500, 100, locationListener);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

        }else{
            stringBuilder.append("经度(Longitude):"+location.getLongitude()+"\n");
            stringBuilder.append("纬度(Latitude):"+location.getLatitude()+"\n");
        }
        return stringBuilder.toString();
    }

    /**
     * 获取传感器信息
     * @param sensorManager
     * @return
     */
    public static String getSensorInfo(SensorManager sensorManager){
        StringBuilder stringBuilder=new StringBuilder();
        List<Sensor> sensorList=sensorManager.getSensorList(SensorManager.SENSOR_STATUS_NO_CONTACT);
        stringBuilder.append("传感器数量："+sensorList.size()+"\n");
        for(int i=0;i<sensorList.size();i++){
            Sensor sensor=sensorList.get(i);

            stringBuilder.append("> "+sensor.getName()+"("+sensor.getType()+")"+"\n");

        }

        return  stringBuilder.toString();
    }


    /**
     * 获取传感器信息,用‘|’隔开
     * @param sensorManager
     * @return
     */
    public static String getSensorInfoFormat(SensorManager sensorManager){
        StringBuilder stringBuilder=new StringBuilder();
        List<Sensor> sensorList=sensorManager.getSensorList(SensorManager.SENSOR_STATUS_NO_CONTACT);
        for(int i=0;i<sensorList.size();i++){

            Sensor sensor = sensorList.get(i);

            if(i==sensorList.size()-1){
                //最后一项后不加‘|’
                stringBuilder.append(sensor.getName());
            }else {
                stringBuilder.append(sensor.getName() + "|");
            }
        }

        return  stringBuilder.toString();
    }

    /**
     * 获取摄像头信息
     * @param cameraManager
     * @return
     */
    public  static  String getCameraInfo(CameraManager cameraManager) throws  CameraAccessException{
        String[] cameraList=null;
        cameraList=cameraManager.getCameraIdList();

        StringBuilder stringBuilder=new StringBuilder();
        int idx=0;
        for(String camera:cameraList){
            CameraCharacteristics cameraCharacteristics=cameraManager.getCameraCharacteristics(camera);
            Integer facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
            StreamConfigurationMap streamConfigurationMap = (StreamConfigurationMap) cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Size[] sizes=streamConfigurationMap.getOutputSizes(ImageFormat.JPEG);
            stringBuilder.append((facing==CameraCharacteristics.LENS_FACING_FRONT?"前置摄像头":"后置摄像头")+":"+sizes[0].getWidth()+"x"+sizes[0].getHeight()+"\n");
        }
        return stringBuilder.toString();
    }


    /**
     * 获取JVM prop信息
     * @return
     */
    public static String getJVMPropInfo(){
        Map<String,String> map=getJVMProp();
        StringBuilder stringBuilder=new StringBuilder();
        for(Map.Entry<String,String> entry:map.entrySet()){
            stringBuilder.append(entry.getKey()+":"+entry.getValue()+"\n");
        }
        return stringBuilder.toString();
    }


    /**
     * 获取JVM自带的prop
     * @return
     */
    public static  Map<String,String> getJVMProp(){
        Map<String,String> map=new HashMap<>();
        Properties properties=System.getProperties();
        Enumeration<?> em=properties.propertyNames();
        for (; em.hasMoreElements();){
            String key=(String)em.nextElement();
            map.put(key,System.getProperty(key));
        }
        return map;
    }

    /**
     * 获取cpu信息
     * @return
     */
    public  static  String getCpuInfo(){
        StringBuilder stringBuilder =new StringBuilder();
        stringBuilder.append("处理器个数:" +Runtime.getRuntime().availableProcessors()+"\n");
        stringBuilder.append("cpu架构："+ CpuBridge.getCpuStructure()+"\n");
        stringBuilder.append("cpu个数："+CpuBridge.getCpuCount()+"\n");
        stringBuilder.append("cpuId："+CpuBridge.getCpuId()+"\n");
        stringBuilder.append("cpu特性："+CpuBridge.getFeatures()+"\n");
        stringBuilder.append("\n");
        stringBuilder.append(getProcCpuInfo());
        return stringBuilder.toString();
    }

    /**
     * 获取/proc/cpuinfo的内存信息
     * @return
     */
    public static  String getProcCpuInfo(){
        return IOUtils.readFile(new File("/proc/cpuinfo"));
    }

    /**
     * 获取存储空间信息
     * @return
     */
    public static  String getStorageInfo(){
        StringBuilder stringBuilder=new StringBuilder();
        String dataPath = Environment.getDataDirectory().getAbsolutePath();
        stringBuilder.append("路径："+dataPath+"\n");
        StatFs statFs = new StatFs(dataPath);
        long blockCount=statFs.getBlockCount();
        long blockSize=statFs.getBlockSize();
        long storageSize=blockSize*blockCount;
        long e=(storageSize>>10);
        int size=(int) (((( e) / 1024.0) / 1024.0) + 0.8);
        stringBuilder.append("BlockSize:"+blockSize+"\n");
        stringBuilder.append("BlockCount:"+blockCount+"\n");
        stringBuilder.append("内部存储大小："+size+"G"+"\n");
        return stringBuilder.toString();
    }

    public static  String getMemInfo(Context context){
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("可用内存："+getSystemAvailableMemorySizeInfo(context)+"\n");
        stringBuilder.append("最大内存："+getSystemTotalMemorySizeInfo(context)+"\n");
        stringBuilder.append("\n");
        stringBuilder.append(getProcMemInfo());

        return stringBuilder.toString();
    }

    /**
     * 获取/proc/meminfo的内存信息
     * @return
     */
    public static  String getProcMemInfo(){
        return IOUtils.readFile(new File("/proc/meminfo"));
    }

    /**
     * 获取data分区的块大小
     * @return
     */
    public static long getDataBockSize(){
        StatFs statFs = new StatFs(DATA_PATH);
        long blockSize=statFs.getBlockSize();
        return blockSize;
    }

    /**
     * 获取data分区的块数量
     * @return
     */
    public static long getDataBockCount(){
        StatFs statFs = new StatFs(DATA_PATH);
        long blockCount=statFs.getBlockCount();
        return blockCount;
    }


    /**
     *最大运行内存
     * @return
     */
    public static String getSystemTotalMemorySizeInfo(Context context){
        String availMemStr = Formatter.formatFileSize(context,getSystemTotalMemorySize(context));
        return availMemStr ;
    }


    /**
     *最大运行内存
     * @return
     */
    public static long getSystemTotalMemorySize(Context context){
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo() ;
        ActivityManager activityManager=(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(memoryInfo) ;
        long memSize = memoryInfo.totalMem ;
        return memSize;
    }


    /**
     * 可用运行内存
     * @return
     */
    public static long getSystemAvailableMemorySize(Context context){
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo() ;
        ActivityManager activityManager=(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(memoryInfo) ;
        long memSize = memoryInfo.availMem ;
        return memSize;
    }

    /**
     * 可用运行内存
     * @return
     */
    public static String getSystemAvailableMemorySizeInfo(Context context){
        String availMemStr = Formatter.formatFileSize(context,getSystemAvailableMemorySize(context));

        return availMemStr ;
    }


    /**
     * 获取基站信息
     * @param telephonyManager
     * @return
     */
    public static  String getBaseStationInfo(TelephonyManager telephonyManager){
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("NetworkCountryIso:" +telephonyManager.getNetworkCountryIso()+"\n");
        stringBuilder.append("NetworkOperator:" +telephonyManager.getNetworkOperator()+"\n");
        stringBuilder.append("NetworkOperatorName:" +telephonyManager.getNetworkOperatorName()+"\n");
        stringBuilder.append("NetworkType:" +telephonyManager.getNetworkType()+"\n");
        stringBuilder.append("DataState:" +telephonyManager.getDataState()+"\n");
        stringBuilder.append("SimCountryIso:" +telephonyManager.getSimCountryIso()+"\n");
        stringBuilder.append("SimOperator:" +telephonyManager.getSimOperator()+"\n");
        stringBuilder.append("SimOperatorName:" +telephonyManager.getSimOperatorName()+"\n");
        stringBuilder.append("PhoneType:" +(telephonyManager.getPhoneType()==0?"0(CDMA)":"1(GSM)")+"\n");

        CellLocation cel = telephonyManager.getCellLocation();
        int phoneType = telephonyManager.getPhoneType();
        //移动联通 GsmCellLocation
        if (phoneType == TelephonyManager.PHONE_TYPE_GSM && cel instanceof GsmCellLocation) {
            GsmCellLocation gsmCellLocation = (GsmCellLocation) cel;
            stringBuilder.append("Cid:" +gsmCellLocation.getCid()+"  Lac:"+gsmCellLocation.getLac()+"\n");
            stringBuilder.append("psc:"+gsmCellLocation.getPsc()+"\n");

        }
        //电信CdmaCellLocation
        else if(phoneType == TelephonyManager.PHONE_TYPE_CDMA && cel instanceof CdmaCellLocation){
            CdmaCellLocation cdmaCellLocation = (CdmaCellLocation) cel;
            stringBuilder.append("基站Id:"+cdmaCellLocation.getBaseStationId()+"\n");
            stringBuilder.append("NetworkId(NID|LAC):"+cdmaCellLocation.getNetworkId()+" BaseStationId(BID|CID):"+cdmaCellLocation.getBaseStationId()+"\n");
            stringBuilder.append("lat:" +cdmaCellLocation.getBaseStationLatitude()+"  long:"+cdmaCellLocation.getBaseStationLongitude()+"\n");
        }
        else{
            stringBuilder.append("SIM卡不可用，CellLocation获取失败"+"\n");
        }

        List<CellInfo> cellInfos= telephonyManager.getAllCellInfo();
        if(cellInfos==null||cellInfos.size()>0) {
            stringBuilder.append("【附近基站信息】\n");
            for (CellInfo cellInfo : cellInfos) {
                if(cellInfo instanceof CellInfoGsm){
                    CellIdentityGsm cellIdentityGsm=((CellInfoGsm) cellInfo).getCellIdentity();
                    stringBuilder.append("GSM--->lac:"+cellIdentityGsm.getLac()
                            +",cid:"+cellIdentityGsm.getCid()
                            +",Mnc:"+cellIdentityGsm.getMnc()+",Mcc"+cellIdentityGsm.getMcc()+"\n");
                }
                else if(cellInfo instanceof CellInfoCdma){
                    CellIdentityCdma cellIdentityCdma=((CellInfoCdma) cellInfo).getCellIdentity();
                    //bid相当于cid,nid相当于lac
                    stringBuilder.append("CDMA--->BasestationId(BID|CID):"
                            +cellIdentityCdma.getBasestationId()
                            +",NetworkId(NID|LAC):"
                            +cellIdentityCdma.getNetworkId()+"\n");
                }
                else if(cellInfo instanceof CellInfoLte){
                    //ci相当于cid,tac相当于lac
                    CellIdentityLte cellIdentityLte=((CellInfoLte) cellInfo).getCellIdentity();
                    stringBuilder.append("LTE--->ci(CID):"+cellIdentityLte.getCi()+",tac(LAC):"
                            +cellIdentityLte.getTac()
                            +",Mnc:"+cellIdentityLte.getMnc()
                            +",Mcc:"+cellIdentityLte.getMcc()+"\n");
                }
                else if(cellInfo instanceof CellInfoWcdma){
                    CellIdentityWcdma cellIdentityWcdma =((CellInfoWcdma)cellInfo).getCellIdentity();
                    stringBuilder.append("WCDMA--->cid:"+cellIdentityWcdma.getCid()
                            +",lac:"+cellIdentityWcdma.getLac()
                            +",Mnc:"+cellIdentityWcdma.getMnc()
                            +",Mcc:"+cellIdentityWcdma.getMcc()+"\n");
                }
                else{
                    stringBuilder.append("未知基站--->"+"\n");
                }
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 获取Lac(location area code)
     * @param telephonyManager
     * @return
     */
    public static  int getLac(TelephonyManager telephonyManager){

        CellLocation cel = telephonyManager.getCellLocation();
        int phoneType = telephonyManager.getPhoneType();
        //移动联通 GsmCellLocation
        if (phoneType == TelephonyManager.PHONE_TYPE_GSM && cel instanceof GsmCellLocation) {
            GsmCellLocation gsmCellLocation = (GsmCellLocation) cel;
            return gsmCellLocation.getLac();

        }
        //电信CdmaCellLocation
        else if(phoneType == TelephonyManager.PHONE_TYPE_CDMA && cel instanceof CdmaCellLocation){
            CdmaCellLocation cdmaCellLocation = (CdmaCellLocation) cel;
            return cdmaCellLocation.getNetworkId();
        }
        return -1;

    }

    /**
     * 获取Cid(cell id)
     * @param telephonyManager
     * @return
     */
    public static  int getCid(TelephonyManager telephonyManager){

        CellLocation cel = telephonyManager.getCellLocation();
        int phoneType = telephonyManager.getPhoneType();
        //移动联通 GsmCellLocation
        if (phoneType == TelephonyManager.PHONE_TYPE_GSM && cel instanceof GsmCellLocation) {
            GsmCellLocation gsmCellLocation = (GsmCellLocation) cel;
            return gsmCellLocation.getCid();

        }
        //电信CdmaCellLocation
        else if(phoneType == TelephonyManager.PHONE_TYPE_CDMA && cel instanceof CdmaCellLocation){
            CdmaCellLocation cdmaCellLocation = (CdmaCellLocation) cel;
            return cdmaCellLocation.getBaseStationId();
        }
        return -1;

    }

    /**
     * 获取蓝牙地址
     * @param bluetoothManager
     * @return
     */
    public static  String getBtAddress(BluetoothManager bluetoothManager){
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("蓝牙地址：" + bluetoothManager.getAdapter().getAddress()+"\n");
        return stringBuilder.toString();
    }

    /**
     * 获取wifi信息
     * @param wifiManager
     * @return
     */
    public static  String getWifiInfo(WifiManager wifiManager){
        StringBuilder stringBuilder=new StringBuilder();
        WifiInfo wifiInfo=wifiManager.getConnectionInfo();
        stringBuilder.append("MAC："+ wifiInfo.getMacAddress()+"\n");
        stringBuilder.append("ConnectionInfo BSSID：\n"+ wifiInfo.getBSSID()+"\n");
        stringBuilder.append("SSID："+ wifiInfo.getSSID()+"\n");
        stringBuilder.append("IP："+ Utils.int2Ip(wifiInfo.getIpAddress())+"\n");
        stringBuilder.append("\n");
        if (wifiManager.isWifiEnabled()) {
            List scanResults = wifiManager.getScanResults();
            if (scanResults != null || scanResults.size() != 0) {
                int i = 0;
                while (i < scanResults.size() && i < 7) {
                    ScanResult scanResult = (ScanResult) scanResults.get(i);
                    stringBuilder.append("BSSID" + "(" + scanResult.SSID + ")：\n" + scanResult.BSSID+"\n");
                    i++;
                }
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 获取设备主要信息
     * @param context
     * @param telephonyManager
     * @return
     */
    public static  String getDeviceInfo(Context context,TelephonyManager telephonyManager){
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("IMEI(0):"+telephonyManager.getImei(0)+"\n");
        stringBuilder.append("IMEI(1):"+telephonyManager.getImei(1)+"\n");
        stringBuilder.append("ro.serialno:"+ Build.SERIAL+"\n");
        stringBuilder.append("getSubscrierId:"+telephonyManager.getSubscriberId()+"\n");
        stringBuilder.append("getSimSerialNumber:"+telephonyManager.getSimSerialNumber()+"\n");
        stringBuilder.append("AndroidId:"+ Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID)+"\n");
        stringBuilder.append("getLine1Number:"+ telephonyManager.getLine1Number()+"\n");
        return stringBuilder.toString();
    }

    /**
     * 打印系统常用的prop
     * @return
     */
    public static String getSystemPropInfo(){
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("ro.product.manufacturer:"+ getSystemProp("ro.product.manufacturer")+"\n");
        stringBuilder.append("ro.board.platform:"+getSystemProp("ro.board.platform")+"\n");
        stringBuilder.append("ro.product.cpu.abi:"+getSystemProp("ro.product.cpu.abi")+"\n");
        return stringBuilder.toString();
    }

    /**
     * 中间层获取SystemProperties
     * @param key
     * @return
     */
    public static String getSystemProp(String key) {
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
     * 获取Build的主要信息
     * @return
     */
    public static  String getBuildInfo(){
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("ro.build.id:"+ Build.ID+"\n");
        stringBuilder.append("ro.build.display.id:"+ Build.DISPLAY+"\n");
        stringBuilder.append("ro.build.fingerprint:"+ Build.FINGERPRINT+"\n");
        stringBuilder.append("ro.product.brand:"+ Build.BRAND+"\n");
        stringBuilder.append("ro.product.name:"+ Build.PRODUCT+"\n");
        stringBuilder.append("ro.product.device:"+ Build.DEVICE+"\n");
        stringBuilder.append("ro.product.board:"+ Build.BOARD+"\n");
        stringBuilder.append("ro.product.model:"+ Build.MODEL+"\n");
        stringBuilder.append("ro.product.manufacturer:"+ Build.MANUFACTURER+"\n");
        stringBuilder.append("Build.getRadioVersion(基带版本):"+ Build.getRadioVersion()+"\n");
        stringBuilder.append("ro.hardware:"+ Build.HARDWARE+"\n");
        stringBuilder.append("cpu_abi:"+ Build.CPU_ABI+"\n");
        stringBuilder.append("cpu_abi2:"+ Build.CPU_ABI2+"\n");
        return stringBuilder.toString();
    }


    /**
     * 获取所有网卡的Mac地址
     * @return
     */
    public static Map<String,String> getMacAddress(){
        Map<String,String> macTable=new HashMap<>();

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
                macTable.put(networkInterface.getName(),mac.substring(0,mac.length()-1));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return macTable;
    }

    public static  String getMacAddressInfo(){
        StringBuilder stringBuilder=new StringBuilder();
        Map<String,String> table=getMacAddress();
        for(Map.Entry<String,String> entry:table.entrySet()){
            stringBuilder.append(entry.getKey()+":"+entry.getValue()+"\n");
        }
        return stringBuilder.toString();
    }


    /**
     * 屏幕分辨率
     * @param windowManager
     * @return
     */
    public static int[] getScreenSize(WindowManager windowManager){
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getRealMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        return new int[]{screenWidth,screenHeight};
    }

    /**
     * 屏幕分辨率方式2
     * @param windowManager
     * @return
     */
    public static int[] getScreenSize2(WindowManager windowManager){
        Point point=new Point();
        windowManager.getDefaultDisplay().getRealSize(point);
        int screenWidth = point.x;
        int screenHeight = point.y;
        return new int[]{screenWidth,screenHeight};
    }

    /**
     * 屏幕分辨率
     * @param windowManager
     * @return
     */
    public static   String getScreenSizeInfo(WindowManager windowManager){
        int[] screenSize1=getScreenSize(windowManager);
        int[] screenSize2=getScreenSize2(windowManager);
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("分辨率方式1："+screenSize1[0]+","+screenSize1[1]+"\n");
        stringBuilder.append("分辨率方式2："+screenSize2[0]+","+screenSize2[1]);
        return stringBuilder.toString();
    }

    /**
     * 获取时间信息
     * @return
     */
    public static String getTimeInfo(){

        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("时间戳(1)："+System.currentTimeMillis()+"\n");
        stringBuilder.append("时间戳(2)："+ Calendar.getInstance().getTimeInMillis()+"\n");
        stringBuilder.append("时间戳(3)："+ new Date().getTime()+"\n");
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateText=simpleDateFormat.format(new Date());
        stringBuilder.append("具体时间："+dateText);

        return stringBuilder.toString();
    }
}
