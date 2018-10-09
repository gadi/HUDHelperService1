package cn.sinjet.activity;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.widget.TextView;
import cn.sinjet.communication.usbport.UsbDeviceConnector;
import cn.sinjet.communication.usbport.UsbDeviceManager;
import cn.sinjet.util.MyLog;
import cn.sinjet.wazehudservice.R;
public class UsbConnectActivity extends Activity{
	UsbDeviceConnector mUsbDeviceConnector;
	TextView tvUSBState;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_startup);
         tvUSBState = (TextView)findViewById(R.id.tv_usb_state);
         mUsbDeviceConnector = new UsbDeviceConnector();
		 mUsbDeviceConnector.init(this);
		 IntentFilter filter = new IntentFilter();
	     filter.addAction("cn.sinjet¡£intent.USB_STATE");
	     registerReceiver(mUSBStateReceiver, filter);
	}
	
	BroadcastReceiver mUSBStateReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.hasExtra("USB_STATE")){
				int usbState = intent.getIntExtra("USB_STATE", 0);
				if(usbState == UsbDeviceManager.USB_CONNECTED){
					tvUSBState.setText("USB device is connected");
				}else if(usbState == UsbDeviceManager.USB_DISCONNECTED){
					tvUSBState.setText("USB device is not connected");
				}
			}else if(intent.hasExtra("LOG_TEXT")){
				tvUSBState.setText(intent.getStringExtra("LOG_TEXT")+"\n\r"+tvUSBState.getText().toString());
			}
		}
		
	};
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(this.getIntent().hasExtra(UsbManager.EXTRA_DEVICE)){//if it has extra device,it means there is USB insertion event 
	        handleUsbDeviceIntent(this.getIntent());
//	        MyLog.i("app", "onResume with device");
		}else{//it means it was started by tab shortcut
//			MyLog.i("app", "onResume without device");
	    	if(!mUsbDeviceConnector.searchForSinjetUsbDev(this)){//no device connecting
	    		
	    	}else{//if there is Sinjet Usb Device
	    		
	    	}
	    }
	}
	
	/*private void delay2StartMainActivity(){
		
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Intent intent = new Intent(UsbConnectActivity.this,MainActivity.class);
	    		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    		MyLog.i("app", "start MainActivity !!!!!!!!!");
	    		startActivity(intent);		
			}
		}, 1500);
	}*/
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
//		finish();
	}
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		MyLog.i("app", "onNewIntent has Extra :"+intent.hasExtra(UsbManager.EXTRA_DEVICE));
	    handleUsbDeviceIntent(intent);
	}
	
	private void handleUsbDeviceIntent(Intent intent){
		if(intent == null) return;
		if(!intent.hasExtra(UsbManager.EXTRA_DEVICE)) return;
		
		if(mUsbDeviceConnector != null){
			UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
			if(null != device)
			mUsbDeviceConnector.connectUsbDevice(this, device);
		}
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(mUsbDeviceConnector != null)
			mUsbDeviceConnector.deInit();
		unregisterReceiver(mUSBStateReceiver);
		super.onDestroy();
	}
	
}