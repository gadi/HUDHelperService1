package cn.sinjet.module;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class ModelMsgHandler extends Handler{
	public final static int MSG_INIT_APP = 1;
	public final static int MSG_SEND_TO_DEVICE = 2;
	public final static int MSG_RECV_FROM_DEVICE = 3;
	
	public final static String KEY_NAME_SEND = "KEY_NAME_SEND";
	public final static String KEY_NAME_RECV = "KEY_NAME_RECV";
	
	//MyApplication app;
	static String tag = "model";
	ModuleDispatcher models;
     public ModelMsgHandler(ModuleDispatcher models, Looper looper) {
		// TODO Auto-generated constructor stub
    	 super(looper);
    	 this.models = models;
	}
     
     @Override
    public void handleMessage(android.os.Message msg) {
    	// TODO Auto-generated method stub
    	//super.handleMessage(msg);
    	int what  = msg.what;
 		switch(what){
 		case MSG_INIT_APP:{
 		}break;
 		case MSG_SEND_TO_DEVICE:{
 			Bundle bundle = msg.getData();
 			byte[] sendBuf = bundle.getByteArray(KEY_NAME_SEND);
 			models.finalSend2Device(sendBuf,sendBuf.length);
 		}break;
 		case MSG_RECV_FROM_DEVICE:
 			break;
        }
    }
     
     
}
