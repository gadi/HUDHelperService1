package cn.sinjet.sinjetservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import cn.sinjet.module.NaviInfoModule;
import cn.sinjet.util.MyLog;


public class AutoAmapReceiver extends BroadcastReceiver{
	static final String tag = "autoamap";
	Context mContext;
	NaviInfoModule mNaviInfoModule = null;
	int[] mCameraArray = new int[]{-1,-1,-1,-1};
	int mNaviType = -1;
	public AutoAmapReceiver(NaviInfoModule naviInfoModule){
		mNaviInfoModule = naviInfoModule;
	}
    public void registerAutoAmapReceiver(Context context){
    	mContext = context;
    	IntentFilter filter = new IntentFilter();
    	filter.addAction("AUTONAVI_STANDARD_BROADCAST_SEND");
    	context.registerReceiver(this, filter);
//        startRecvAmapInfo();
    }
    private void startRecvAmapInfo(){
    	Intent intent = new Intent();
	     intent.setAction("AUTONAVI_STANDARD_BROADCAST_RECV");
	     intent.putExtra("KEY_TYPE", 10062);
	     mContext.sendBroadcast(intent);
    }
    
    public void unregisterReceiver(){
    	mContext.unregisterReceiver(AutoAmapReceiver.this);
    }
    
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
	   	MyLog.i(tag, "recv intent:"+intent.toString());
	    if (intent.getAction().equals("AUTONAVI_STANDARD_BROADCAST_SEND")) {
			 int  key_type =intent.getIntExtra("KEY_TYPE", 0);
			  MyLog.i(tag, "KeyType:"+key_type);
	              if (key_type==10001) {
//	            	  mNaviType = intent.getIntExtra("TYPE",-1); 
	            	  int routeRemainDis = intent.getIntExtra("ROUTE_REMAIN_DIS", 0);
	            	  int routeRemainTime = intent.getIntExtra("ROUTE_REMAIN_TIME", 0);
//	            	  handleRemainRoute(routeRemainDis,routeRemainTime);
	            	  MyLog.i(tag, "remain time:"+routeRemainTime+" remain dis:"+routeRemainDis);
//	            	  if(mNaviType == 1 && routeRemainDis < 50) navigationEnd();
	            	  
	            	  String curRoadName = intent.getStringExtra("CUR_ROAD_NAME");
	            	  String nextRoadName = intent.getStringExtra("NEXT_ROAD_NAME");
	            	  MyLog.i(tag, "curRoad:"+curRoadName+" nextRoad:"+nextRoadName);
	            	  if(nextRoadName != null && !nextRoadName.isEmpty()){
	            		  mNaviInfoModule.onNextRoadName(nextRoadName);
//	            		  ViewModel.getIns().setViewProperty(R.id.navi_next_road_name, ""+nextRoadName);
	            	  }
	            	  int serviceareaDistance = intent.getIntExtra("SAPA_DIST", -1);
	            	  //int serviceArea type int "SAPA_TYPE"
	            	  int cameraDistance = intent.getIntExtra("CAMERA_DIST", -1);
	            	  int cameraType = intent.getIntExtra("CAMERA_TYPE", -1);
	            	  int cameraSpeed = intent.getIntExtra("CAMERA_SPEED", -1);
	            	  MyLog.i(tag, "camera dis:"+cameraDistance+" type:"+cameraType+" speed:"+cameraSpeed);
	            	  handleCameraInfos(cameraDistance,cameraType,cameraSpeed);
	            	  
	            	  int cameraIndex = intent.getIntExtra("CAMERA_INDEX", -1);
	            	  int icon = intent.getIntExtra("ICON", -1);
//	            	  int remainDis = intent.getIntExtra("ROUTE_REMAIN_DIS", -1);
	            	  int setRemainDis = intent.getIntExtra("SEG_REMAIN_DIS", -1);
	            	  if(icon != -1 && setRemainDis != -1)
	            	  handleNaviIcon(setRemainDis, icon);
	            	  
	            	  MyLog.i(tag, "in "+setRemainDis+" meters "+icon);
	            	  int limitedSpeed = intent.getIntExtra("LIMITED_SPEED", -1);
	            	  MyLog.i(tag, "limited speed:"+limitedSpeed);
//	            	  int curSetNum = intent.getIntExtra("CUR_POINT_NUM", 0);
//	            	  MyLog.i(tag, "curSetNum:"+curSetNum);
	            	  int roundAboutNum = intent.getIntExtra("ROUNG_ABOUT_NUM",0);
	            	  MyLog.i(tag, "roundAboutNum:"+roundAboutNum);
	            	  handleRoundAboutNum(roundAboutNum);
	            	  
	            	  boolean arriveStatus = intent.getBooleanExtra("ARRIVE_STATUS", false);
	            	  MyLog.i(tag, "arriveStatus:"+arriveStatus);
	            	  if(arriveStatus) {
	            		  navigationEnd();
	            	  }
	            	  
	            	  String SAPAName = intent.getStringExtra("SAPA_NAME");
	            	  MyLog.i(tag, "SAPAName:"+SAPAName);
	            	  int SAPADistance = intent.getIntExtra("SAPA_DIST",-1);
	            	  MyLog.i(tag, "SAPADistance:"+SAPAName+"m");
                      handleSAPAInfo(SAPAName,SAPADistance);	            	  
	            	  
	              }else if(key_type == 13011){
	              }else if(key_type == 10074){
	              }else if(key_type == 10016){
	            	  mNaviInfoModule.onLaneInfo(null);
	            	  mNaviInfoModule.onLaneInfoEx(null);
            		  startRecvAmapInfo();
	              }else if(key_type == 10019){//auto running state
	            	  int state = intent.getIntExtra("EXTRA_STATE", -1);
	            	  handleAutoamapState(state);
	              }
	    }
	}
	
	private void handleRoundAboutNum(final int num){
		mNaviInfoModule.onRoundAboutNum(num);
	}
	private void handleAutoamapState(final int state){
		MyLog.i(tag, "amap state:"+state);
		switch(state){
		case 8:// navigation start
			mNaviInfoModule.onAmapNavigating(true);
			break;
		case 9:// navigation end
			mNaviInfoModule.onAmapNavigating(false);
			break;
		case 10://simulator start
			mNaviInfoModule.onAmapNavigating(true);
			break;
		case 11://simulator pause
			mNaviInfoModule.onAmapNavigating(false);
			break;
		case 12://simulator end
			mNaviInfoModule.onAmapNavigating(false);
			break;
		case 39://arrive destination
			mNaviInfoModule.onAmapNavigating(false);
			break;
		case 40://heart beat  one time each minute
			//mNaviInfoModule.onAmapNavigating(true);
			break;
		}
	}
	
	
	private void navigationEnd(){
	   mNaviInfoModule.onAmapNavigating(false);
	}
	
  
   
   private void handleSAPAInfo(String SAPAName,int SAPADistance){
   }
   
 
	private void handleCameraInfos(int cameraDistance, int cameraType, int cameraSpeed) {
		// TODO Auto-generated method stub
		if(cameraType == -1 && cameraDistance == -1){//means there is no camera
			mNaviInfoModule.onCameraDistance(0, 0, 0);
			mNaviInfoModule.onLimitSpeed(0, 0);
			return;
  	    }else{
  	    	mNaviInfoModule.onCameraDistance(cameraDistance, 1, cameraType);
  	    	if(cameraType == 0) {//limit speed camera
  	    		mNaviInfoModule.onLimitSpeed(cameraDistance, cameraSpeed);
  	    	}
  	    }
		
	}
	
	
	
	
	
	private void handleNaviIcon(int distance,int icon){
		int iconType = 1;
	    if(icon >0 && icon < 17) iconType = icon;
	    else{
	    	switch(icon){
	    	case 17:  iconType = 11; break;
	    	case 18:  iconType = 12; break;
	    	case 19:  iconType = 8; break;
	    	case 20:  iconType = 9; break;
	    	}
	    }
	    mNaviInfoModule.onStepInfo(iconType,distance);
	}
	

	
}
