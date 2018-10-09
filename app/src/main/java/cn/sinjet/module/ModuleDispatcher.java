package cn.sinjet.module;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import cn.sinjet.sinjetservice.AutoAmapReceiver;

public class ModuleDispatcher{
	 public static final int CMD_BACK_KEY = 10;	
	 public static final int CMD_APP_VERSION = 20;
	 public static final int CMD_UI_PAGE = 30;
	 static ModuleDispatcher instance = null;
	 WorkerThread workerThread = null;
	 ModelMsgHandler modelMsgHandler = null;
	 AutoAmapReceiver autoAmapReceiver = null;
	 NaviInfoModule naviInfoModule = null;
	 Context mContext = null;
	 public static final int YF_BLUETOOTH_HFP_CALL_STATE_IDLE = -1;
	 public static final int YF_BLUETOOTH_HFP_CALL_STATE_INCOMING = 4;
	 public static final int YF_BLUETOOTH_HFP_CALL_STATE_OUTGOING = 2;
	 public static final int YF_BLUETOOTH_HFP_CALL_STATE_SPEAKING = 0;
	 public static final int YF_BLUETOOTH_HFP_CALL_STATE_CHANGEDNUMBER = 3;
	 
	 public static ModuleDispatcher getIns(){
		 if(instance == null)
			 instance = new ModuleDispatcher();
		 return instance;
	 }
	 public ModuleDispatcher(){
		 naviInfoModule = new NaviInfoModule();
		 workerThread = new WorkerThread("Model Main ProcThread");
		 modelMsgHandler = new ModelMsgHandler(this,workerThread.getLooper());
	 }
	 
     public void initModule(Context context){
    	  mContext = context;
    	  if(autoAmapReceiver == null)
    	  autoAmapReceiver = new AutoAmapReceiver(naviInfoModule);
 		  autoAmapReceiver.registerAutoAmapReceiver(context);
     }
     
     private void sendCallName(final String number,final String name){
    	 if(number == null || number.isEmpty()) return;
//    	 if(mYFBTManager == null) return;
//			String name = mYFBTManager.getNameByPhoneNumber(number);
			if(name == null || name.isEmpty()){
				send2DeviceCallName(number);
			}else{
				send2DeviceCallName(name);
			}
     }
     private void send2DeviceCallState(int state){
 		Log.i("sinjet", "sendPhoneState:"+state);
 		byte[] buf = new byte[]{0x10,(byte)state};
 		packAndSendMsg(buf, buf.length);
 	}
 	
 	private void send2DeviceCallName(String name){
 		if(name ==null || name.isEmpty()) return;
 		Log.i("sinjet", "sendCallName:"+name);
 		byte[] fullNameBuf = name.getBytes();
 		sendCallNameFrame(fullNameBuf.length,0,fullNameBuf,fullNameBuf.length);
 	}
 	
 	private void sendCallNameFrame(int length,int offset,byte[] data,int len){
 		byte[] buf = new byte[len+3];
 		buf[0] = 0x12;
 		buf[1] = (byte)length;
 		buf[2] = (byte)offset;
 		System.arraycopy(data, offset, buf, 3, len);
 		packAndSendMsg(buf, buf.length);
 	}
 	
 	private void packAndSendMsg(byte[] message,int len){
 	    byte[] szMsg = new byte[len + 5];
 		szMsg[0] = (byte) 0xFF;
 		szMsg[1] = (byte) 0x55;
 		szMsg[2] = (byte) (len + 1);
 		szMsg[3] = (byte) 0x35;
 		System.arraycopy(message, 0, szMsg, 4, len);

 		byte sum = 0;
 		int s = 0;
 		int i = 0;
 		for (i = 2; i < len + 4; i++) {
 			s += (szMsg[i] & 0xFF);
 		}
 		sum = (byte) (s & 0xFF);
 		szMsg[len + 4] = sum;
 		send2Device(szMsg, szMsg.length);
    }
     
 	
     public void uninit(){
    	 if(autoAmapReceiver != null)
    		 autoAmapReceiver.unregisterReceiver();
     }
    
     public void send2Device(byte[] buf,int len){
    	if(modelMsgHandler == null) return;
    	
    	Message msg = modelMsgHandler.obtainMessage(ModelMsgHandler.MSG_SEND_TO_DEVICE);
    	Bundle data = new Bundle();
    	data.putByteArray(ModelMsgHandler.KEY_NAME_SEND, buf);
    	msg.setData(data);
    	modelMsgHandler.sendMessage(msg);
     }
     
   
	public void finalSend2Device(byte[] sendBuf, int length) {
		// TODO Auto-generated method stub
       CommunicationModule.getInstance().sendMessage(sendBuf,length);
	}

}
