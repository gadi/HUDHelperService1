package cn.sinjet.sinjetservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AutoStartupReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.i("app", "onReceive: "+intent.getAction());
        startSinjetService(context, intent.getAction());
	}

	
	private void startSinjetService(Context context,String action){
		Intent intent = new Intent(context,SinjetService.class);
		intent.setAction(action);
		context.startService(intent);
	}
  /*  private void displayLimitSpeed(int limitSpeed){
		
	}*/
	
	
}
