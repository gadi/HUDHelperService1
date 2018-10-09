package cn.sinjet.sinjetui;

import android.app.Application;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class SinjetApplication extends Application {
	public static SinjetApplication mMyApplication;

	@Override
	public void onCreate(){
		// TODO Auto-generated method stub
		super.onCreate();
		mMyApplication = this;
		Log.i("app", " app onCreate");
	}
	
	public static SinjetApplication getInstance(){
		return mMyApplication;
	}
	
}
