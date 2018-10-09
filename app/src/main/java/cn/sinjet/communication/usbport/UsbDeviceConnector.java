package cn.sinjet.communication.usbport;

import java.util.HashMap;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;
import cn.sinjet.sinjetservice.SinjetService;
import cn.sinjet.util.MyLog;


public class UsbDeviceConnector{
    final static String tag = UsbDeviceConnector.class.getSimpleName();
    private UsbManager mUsbManager = null;
    Context mContext = null;
   
    public static final String ACTION_USB_PERMISSION = "ACTION_USB_PERMISSION_RESULT"; 
    public final static String ACTION_USB_DEVICE_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED";
    public final static String ACTION_USB_DEVICE_DETACHED = "android.hardware.usb.action.USB_DEVICE_DETACHED";
    
    private boolean mNeedReconnectUsb = true;
    public void init(Context context){
		mContext = context;
		if(mUsbManager == null){
			if(mContext != null)
			    mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
		    IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		    filter.addAction(ACTION_USB_DEVICE_DETACHED);
		    mContext.registerReceiver(mUsbReceiver, filter);
		}
	}
	
    public void deInit(){
    	mContext.unregisterReceiver(mUsbReceiver);
    }
	
	private boolean isNeedReconnectUsb(){
		return mNeedReconnectUsb;
	}
	
	
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                        	printLog("permission granted true");
                        	if(UsbCommunication.isSinjetUsbDevice(device.getVendorId(), device.getProductId())){
                        		startSinjetService(context, device);                        		
                        	}
                        }
                    } else {
                    	printLog("permission granted false");
                    }
                }
            }else if(ACTION_USB_DEVICE_DETACHED.equals(action)){
            	mNeedReconnectUsb = true;
            }
        }
    };
    
    
	private void printLog(String text){
		MyLog.i("usb",": "+text);
		
//		ViewModel.getIns().setViewProperty(R.id.home_bt_status_text, text);
	}
	
	/*
	 * return:  if need to goto connecting help,return false else return true
	 * */
	public boolean searchForSinjetUsbDev(Activity activity){
		boolean ret = false;
		if(mUsbManager == null) return false;
		if(!isNeedReconnectUsb()) return true;
		final HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
		 for (UsbDevice device:deviceList.values()) {
			 String devInfo ="vedorId:"+device.getVendorId() +" productId:"+device.getProductId()+" devName:"+device.getDeviceName()+" proto:"+device.getDeviceProtocol();
//		    	if(tvLog != null) tvLog.setText(tvLog.getText().toString()+"\n"+devInfo);
			 printLog("usb device info:"+devInfo);
	     	if(UsbCommunication.isSinjetUsbDevice(device.getVendorId(),device.getProductId())){
	     		if(!mUsbManager.hasPermission(device)){
	     			 printLog("has no permisstion of usb device!");
	     			 if(activity != null){
		     			  PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, 0, new Intent(UsbDeviceConnector.ACTION_USB_PERMISSION), 0);
		     			  if(pendingIntent != null) mUsbManager.requestPermission(device, pendingIntent);
	     			 }
	     		}else{
	     			printLog("has permisstion of usb device!");
	     			startSinjetService(activity, device);
	     		}
	     	    ret = true;
	     	}
		 }
		mNeedReconnectUsb = false;
		return ret;
	}
	
	
    public void connectUsbDevice(Activity activity,UsbDevice device){
    	if(mUsbManager == null) return;
    	if(!isNeedReconnectUsb()) return;
    	if(!mUsbManager.hasPermission(device)){
			 printLog("has no permisstion of usb device!");
			 if(activity != null){
    			  PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, 0, new Intent(UsbDeviceConnector.ACTION_USB_PERMISSION), 0);
    			  if(pendingIntent != null) mUsbManager.requestPermission(device, pendingIntent);
			 }
		}else{
			printLog("has permisstion of usb device!");
			startSinjetService(activity, device);
		}
    	mNeedReconnectUsb = false;
    }
    
    private void startSinjetService(Context context,UsbDevice device){
    	Intent intent = new Intent(context,SinjetService.class);
		intent.putExtra(UsbManager.EXTRA_DEVICE, device);
		context.startService(intent);
    }
}
