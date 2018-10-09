package cn.sinjet.module;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class NaviInfoModule implements INaviInfo{
	final static String tag = NaviInfoModule.class.getSimpleName();
	private int limitSpeed = 0;
    private int distance = 0, iconType = 0;
    private int bDspCamera = 0,cameraDistance = 0;
	private int retainDistance = 0;
	private String mNextRoadName = "";
    private boolean mIsAmapNavigating = false;
    private int roundAboutNum = -1;
	private ToDevNaviCodec mNaviCodec = new ToDevNaviCodec();
    public NaviInfoModule(){
    	mTimerHeartbeat.postDelayed(mRunnableHeartbeat, 10000);
    }
  
    Handler mTimerHeartbeat = new Handler();
    Runnable mRunnableHeartbeat = new Runnable(){

 	@Override
 	public void run() {
 		// TODO Auto-generated method stub
 		mNaviCodec.notifyIsNaviRunning(isNavigating());
 		mTimerHeartbeat.postDelayed(mRunnableHeartbeat, 3000);
 	}
 	   
    };
    
	  private void clearData(){
		   limitSpeed = 0;
		   distance = 0; iconType = 0;
		   bDspCamera = 0;cameraDistance = 0;
		   retainDistance = 0;
		   roundAboutNum = -1;
		   mNextRoadName = ""; 
	  }
	  
	  private int mTimesNotNavigatingButGotTurnInfo = 0;
	  private void notNavigatingButGetTurnInfo(){
		  mTimesNotNavigatingButGotTurnInfo++;
		  clearData();
		  if(mTimesNotNavigatingButGotTurnInfo >= 3){
			  mTimesNotNavigatingButGotTurnInfo = 0;
			  mIsAmapNavigating = true;
		  }
	  }
  
  @Override
	public void onStepInfo(int iconType, int distance) {
		// TODO Auto-generated method stub
//		isNaviRunning = 1;	
//		setNaviRunningForGettingInfo();
		Log.d(tag,"get front:"+distance+"m ::"+iconType);
		if(this.distance != distance || this.iconType != iconType){
			this.distance = distance;
	        this.iconType = iconType;
			/*if(distance >= 2000 && iconType != 9)//not straight and distance is more than 150
				iconType = 9;*/		    
			mNaviCodec.sendnaviInfo(iconType, distance);
		    if(!mIsAmapNavigating) {
	    	  notNavigatingButGetTurnInfo();//get this message,means it is navigating
	        }
       }
	}


	@Override
	public void onLineInfo(int retainDistance) {
		// TODO Auto-generated method stub
		Log.d(tag,"get LineInfo retainDistance:"+retainDistance);
		if(this.retainDistance  == retainDistance)
			return;
//		isNaviRunning = 1;	
		this.retainDistance = retainDistance;
		mNaviCodec.sendLineInfo(retainDistance);
	}


	@Override
	public void onLimitSpeed(int speed, int limitSpeed) {
		// TODO Auto-generated method stub
//		isNaviRunning = 1;	
		if(this.limitSpeed == limitSpeed)
			return;
		this.limitSpeed = limitSpeed;
		mNaviCodec.sendLimitSpeed(speed,limitSpeed);
	}


	@Override
	public void onCameraDistance(int distance, int dsp,int type) {
		// TODO Auto-generated method stub
		mNaviCodec.sendCameraDistanceEx(cameraDistance,this.bDspCamera,type);
		if(bDspCamera == dsp){
			return;
		}
		this.cameraDistance = distance;
		this.bDspCamera = dsp;
		mNaviCodec.sendCameraDistance(cameraDistance,this.bDspCamera,type);
	}
	


	@Override
	public void onLaneInfo(int[] laneInfo) {
		// TODO Auto-generated method stub
		mNaviCodec.sendLaneInfo(laneInfo);
	}
	@Override
	public void onLaneInfoEx(int[] laneInfo) {
		// TODO Auto-generated method stub
		mNaviCodec.sendLaneInfoEx(laneInfo);
	}
	
	private byte isNavigating(){
    	if(mIsAmapNavigating)
    		return 1;
    	else 
    		return 0;
    }
	
	
  
   
   
	@Override
	public void onAmapNavigating(boolean isNavigating) {
		// TODO Auto-generated method stub
		if(mTimesNotNavigatingButGotTurnInfo != 0)
			mTimesNotNavigatingButGotTurnInfo = 0;
		
		if(!isNavigating) clearData();
	
    	mIsAmapNavigating = isNavigating;	
    	mNaviCodec.notifyIsNaviRunning(isNavigating());
	}	

	@Override
	public void onNextRoadName(String roadName) {
		// TODO Auto-generated method stub
		if(mNextRoadName.equals(roadName))
			return;
		mNextRoadName = roadName;
		Log.i(tag, "next road name:"+roadName);
		mNaviCodec.sendNextRoadName(roadName);
	}   
	

	@Override
	public void onRoundAboutNum(final int num) {
		// TODO Auto-generated method stub
		if(this.roundAboutNum == num)
			return;
		this.roundAboutNum = num;
		mNaviCodec.sendRoundAboutNum(num);
	}

	
}
