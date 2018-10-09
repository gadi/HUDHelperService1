package cn.sinjet.sinjetservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import cn.sinjet.communication.usbport.UsbDeviceManager;
import cn.sinjet.module.ModuleDispatcher;
import cn.sinjet.util.MyLog;

public class SinjetService extends Service{
	static final String tag = "service";
	public final static int MSG_HEAT_BEAT = 100;
	public static final int MSG_START = 200;
    private static  Context mContext = null;
    private UsbDeviceManager mUsbDeviceManager = new UsbDeviceManager(this);
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return mMessager.getBinder();
	}
	
	public static Context getContext(){
		return mContext;
	}
	 private final Messenger mMessager = new Messenger(new ServiceHandler());
//     private  Messenger mUIClient = null;
     class ServiceHandler extends Handler{
 		@Override 
     	public void handleMessage(Message msg) {
     		// TODO Auto-generated method stub
     		super.handleMessage(msg);
     		Log.d(tag, "handlerMessage what="+msg.what);
     		switch(msg.what){
    		case MSG_START:{
//    			ViewModel.getIns().setUIClient(msg.replyTo);
    			ModuleDispatcher.getIns().initModule(SinjetService.this);
    			Message heatbeat = Message.obtain(this, MSG_HEAT_BEAT);
                this.sendMessageDelayed(heatbeat, 5000);
    		}break;
    		
    		case MSG_HEAT_BEAT:{
    		  }break;
     		}
     	 }
      }
   
    @Override
    public void onCreate() {
    	// TODO Auto-generated method stub
    	super.onCreate();
    	MyLog.i(tag, "service onCreate");
    	mUsbDeviceManager.onCreate();
    	
    	mContext = this;
    	ModuleDispatcher.getIns();
    	ModuleDispatcher.getIns().initModule(SinjetService.this);
    } 
    
    @Override
    public void onDestroy() {
    	// TODO Auto-generated method stub
    	MyLog.i(tag, "service onDestroy");
    	mUsbDeviceManager.onDestroy();
    	ModuleDispatcher.getIns().uninit();
    	super.onDestroy();
    }
    @Override
    public boolean onUnbind(Intent intent) {
    	// TODO Auto-generated method stub
    	Log.d(tag, "service onUnbind");
    	return super.onUnbind(intent);
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	// TODO Auto-generated method stub
    	mUsbDeviceManager.onStart(intent, startId);
    	return super.onStartCommand(intent, flags, startId);
    }
}
