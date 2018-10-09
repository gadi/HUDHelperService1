package cn.sinjet.util;

import android.content.Intent;
import android.util.Log;
import cn.sinjet.sinjetui.SinjetApplication;

public class MyLog {
   public static void i(String tag,String msg){
	   Log.i(tag, msg);
	   Intent intent = new Intent("cn.sinjet¡£intent.USB_STATE");
       intent.putExtra("LOG_TEXT", msg);
       SinjetApplication.getInstance().getApplicationContext().sendBroadcast(intent);
   }
   public static void d(String tag,String msg){
	   Log.d(tag, msg);
   }
   public static void e(String tag,String msg){
	   Log.e(tag, msg);
   }
}
