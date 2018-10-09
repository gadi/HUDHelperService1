package cn.sinjet.communication.usbport;

import java.util.HashMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;
import cn.sinjet.module.CommunicationModule;
import cn.sinjet.sinjetservice.SinjetService;
import cn.sinjet.sinjetui.SinjetApplication;
import cn.sinjet.util.MyLog;

public class UsbDeviceManager{
    final static String tag = UsbDeviceManager.class.getSimpleName();
    private SinjetService service;
    public final static int USB_DISCONNECTED = 0;
    public final static int USB_CONNECTED = 1;
    private int mUSBStatus = USB_DISCONNECTED;
    public UsbDeviceManager(SinjetService service){
    	this.service = service;
    }
    
    UsbManager mUsbManager = null;
    
	public void onCreate() {
		// TODO Auto-generated method stub
		Log.i(tag, "onCreate");
		if(mUsbManager == null)
			mUsbManager = (UsbManager)service.getSystemService(Context.USB_SERVICE);
		IntentFilter filter = new IntentFilter(UsbDeviceConnector.ACTION_USB_DEVICE_DETACHED);
		service.registerReceiver(mUsbReceiver, filter);
	}
	
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
//		super.onStart(intent, startId);
		Log.i(tag, "onStart");
		if(intent == null) return;
		if(intent.hasExtra(UsbManager.EXTRA_DEVICE)){
            UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if(device != null)
            if(mUsbCommunication == null){	
			   new Thread(new SetupConnectionRunnable(device),"SetupUsbConnectionThread").start();
            }else{
            	MyLog.i(tag, "commnunication != null");
            }
		}
	}
	

	private UsbCommunication mUsbCommunication = null;
	private class SetupConnectionRunnable implements Runnable{
	    	UsbDevice device = null;
	        public SetupConnectionRunnable(UsbDevice device) {
				// TODO Auto-generated constructor stub
	        	this.device = device;
			}
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(this.device == null) return;
			    final UsbDeviceConnection connection = mUsbManager.openDevice(device);
	             if (connection == null) {
	            	printLog("Could not open device");
	                return;
	             }
	             
	 			 if(mUsbCommunication != null){
	 				 mUsbCommunication.close();
	 				 mUsbCommunication = null;
	 			 }
	 			 mUsbCommunication = new UsbCommunication(device,connection);
	 			 mUsbCommunication.setOnUsbReceivedListener(CommunicationModule.getInstance());
	 			 if(mUsbCommunication.open()){
	 				 CommunicationModule.getInstance().setUsbCommunication(mUsbCommunication);
	 			     setUSBStatus(USB_CONNECTED);
	 			 }else{
	 				 printLog("open usb device fail");
	 				 mUsbCommunication = null;
	 				 setUSBStatus(USB_DISCONNECTED);
	 			 }
			}
	    }
	 
	 private void printLog(String text){
		 MyLog.i("usb",text);
		 printfLogs2View(text);
		 
	 }
	 
	public void onDestroy() {
		// TODO Auto-generated method stub
		service.unregisterReceiver(mUsbReceiver);
//		super.onDestroy();
	}
	 
	 private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

	        public void onReceive(Context context, Intent intent) {
	            String action = intent.getAction();
	            if (UsbDeviceConnector.ACTION_USB_DEVICE_DETACHED.equals(action)) {
	                synchronized (this) {
	                	if(mUsbManager == null) return;
	                	boolean hasHudUsbDevice = false;
	            		final HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
	            		for (UsbDevice device:deviceList.values()) {
	            			 String devInfo ="vedorId:"+device.getVendorId() +" productId:"+device.getProductId()+" devName:"+device.getDeviceName()+" proto:"+device.getDeviceProtocol();
	            			 printLog("usb device info:"+devInfo);
	            		     if(UsbCommunication.isSinjetUsbDevice(device.getVendorId(), device.getProductId())){
	            		    	 hasHudUsbDevice = true;
	            		    	 break;
	            		     }
	            	    }
	            		if(!hasHudUsbDevice){//means it is disconnected
	            			if(mUsbCommunication != null){
	   	 					 mUsbCommunication.close();
	   	 					 mUsbCommunication = null;
	   	 				     CommunicationModule.getInstance().setUsbCommunication(mUsbCommunication);
	   	 				  }
	            	      setUSBStatus(USB_DISCONNECTED);
	            		}
	                }
	            }
	        }
	    };
	    
	    
	private void setUSBStatus(int status){
		mUSBStatus = status;
        Log.i("app", "USB state");
        Intent intent = new Intent("cn.sinjet。intent.USB_STATE");
        intent.putExtra("USB_STATE", status);
        SinjetApplication.getInstance().sendBroadcast(intent);
	}
	private void printfLogs2View(String strLog){
        Intent intent = new Intent("cn.sinjet。intent.USB_STATE");
        intent.putExtra("LOG_TEXT", strLog);
        SinjetApplication.getInstance().sendBroadcast(intent);
	}
	
}
