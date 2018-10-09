package cn.sinjet.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceUtil {
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
    private static final String fileName = "sinjetservice";
	public SharedPreferenceUtil(Context context) {
		sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		editor = sp.edit();
	 }

	public SharedPreferences.Editor getEditor(){
		return editor;
	}
	public SharedPreferences getSp(){
		return sp;
	}
}	
