package cn.sinjet.module;

import android.util.Log;
import cn.sinjet.communication.usbport.UsbCommunication;
import cn.sinjet.communication.usbport.UsbCommunication.OnUsbReceivedListener;
import cn.sinjet.util.MyLog;

public class CommunicationModule implements OnUsbReceivedListener{
    static private CommunicationModule instance = null;
    private UsbCommunication mUsbCommunication = null;
    public CommunicationModule(){
    	
    }
    public static CommunicationModule getInstance(){
    	if(instance == null){
    		instance = new CommunicationModule();
    	}
    	if(instance != null){
    		return instance;
    	}else{
    		Log.e("app", "err!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    		return null;
    	}
    }
    
    
    public int sendMessage(byte[] message,int length){
    	//Blue.getInstance().sendMessage(message);
//    	BluetoothModel.getInstance().connectBT();
    	MyLog.i("usb", "sending new message");
    	if(mUsbCommunication != null){
            mUsbCommunication.sendMessage(message,length);		
    	}
    	return 0;
    }
    
    
    public void setUsbCommunication(UsbCommunication usbCommunication){
    	this.mUsbCommunication = usbCommunication;
    	
    }
    
    
	@Override
	public void onUsbReceived(byte[] buf, int len) {
		// TODO Auto-generated method stub
		/*final StringBuilder stringBuilder = new StringBuilder(buf.length);
		for (byte byteChar : buf)
			stringBuilder.append(String.format("%02X ", byteChar));
		Log.i("usb","recvMessage: "+ stringBuilder.toString());*/
//		CarModel.getInstance().onRecvMsgFromDevice(buf, len);
	}
	
    public boolean isUSBDeviceConnected(){
    	if(this.mUsbCommunication != null)
    		return true;
    	else 
    		return false;
    }
	
}
