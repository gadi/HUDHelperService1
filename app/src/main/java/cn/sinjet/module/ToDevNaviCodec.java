package cn.sinjet.module;

import java.util.Calendar;

import android.util.Log;

public class ToDevNaviCodec {
	/*
	 * message frame    0xFF 0x55 length mode command param ... checksum 
	 * */
	private void packAndSendMsg(byte[] message,int len){
	    byte[] szMsg = new byte[len + 5];
		szMsg[0] = (byte) 0xFF;
		szMsg[1] = (byte) 0x55;
		szMsg[2] = (byte) (len + 1);
		szMsg[3] = (byte) 0x01;
		System.arraycopy(message, 0, szMsg, 4, len);

		byte sum = 0;
		int s = 0;
		int i = 0;
		for (i = 2; i < len + 4; i++) {
			s += (szMsg[i] & 0xFF);
		}
		sum = (byte) (s & 0xFF);
		szMsg[len + 4] = sum;
		
		ModuleDispatcher.getIns().send2Device(szMsg,len+5);
    }
	
	
	/*
	 * Segment info
	 * iconType: 
	 *  1 default 
		2 left   
		3 right  
		4 leftfront 
		5 rightfront 
		6 leftback  
		7 rightback 
		8 leftandaround 
		9 straight  
		10 arriveWaypoint 
		11 enteraoundabout
		12 outroundabout
		13 arrivedserviceArea
		14 arrivedtollgate
		15 arriveddestination
		16  
		17 turn_ring_front
		18 turn_ring_left
		19 turn_ring_leftback
		20 turn_ring_leftfront
		21 turn_ring_right
		22 turn_ring_rightback
		23  turn_ring_rightfront
		24 turn_ring_back 
	 * */
    public void sendnaviInfo(int iconType,int distance){
 	     byte[] buf = new byte[6];
 	     buf[0] = 0x0;
 	     buf[1] = (byte)iconType;
 	     buf[2] = (byte)(distance&0xFF);
 	     buf[3] = (byte)((distance&0xFF00)>>8);
 	     buf[4] = (byte)((distance&0xFF0000)>>16);
 	     buf[5] = (byte)((distance&0xFF000000)>>24);
 	     packAndSendMsg(buf, 6);
    }
	    
    //remain distance to the destination
	public void sendLineInfo(int retainDistance) {
		// TODO Auto-generated method stub
		byte[] buf = new byte[5];
 	     buf[0] = 0x2;
 	     buf[1] = (byte)(retainDistance&0xFF);
 	     buf[2] = (byte)((retainDistance&0xFF00)>>8);
 	     buf[3] = (byte)((retainDistance&0xFF0000)>>16);
 	     buf[4] = (byte)((retainDistance&0xFF000000)>>24);
 	     packAndSendMsg(buf, 5);
	}
	
	//telling HUD if it is navigating
	public void notifyIsNaviRunning(byte flag){
		Log.i("sinjet", "notifyIsNaviRunning:"+flag);
		byte[] buf = new byte[]{(byte)0xE0,flag};
		packAndSendMsg(buf, 2);
	}
	
	//Limit Speed
	public void sendLimitSpeed(int speed, int limitSpeed) {
		// TODO Auto-generated method stub
		byte[] buf = new byte[5];
 	     buf[0] = 0x1;
 	     buf[1] = (byte)(speed&0xFF);
 	     buf[2] = (byte)((speed&0xFF00)>>8);
 	     buf[3] = (byte)(limitSpeed&0xFF);
 	     buf[4] = (byte)((limitSpeed&0xFF00)>>8);
 	     packAndSendMsg(buf, 5);
	}
	
	/*Camera infos
	 * distance: distance to the camera
	 * dsp:  0,hide 1,show
	 * type: 0,speed camera   1,monitoring camera 2,traffic light camera 3,regular camera 
	 * 4,bus lane camera 5,emergency lane camera 
	*/
	public void sendCameraDistance(int distance,int dsp,int type) {
		// TODO Auto-generated method stub
		byte[] buf = new byte[5];
 	     buf[0] = 0x10;
 	     buf[1] = (byte)(distance&0xFF);
 	     buf[2] = (byte)((distance&0xFF00)>>8);
 	     buf[3] = (byte)(dsp);
 	     buf[4] = (byte)(type);
 	     packAndSendMsg(buf, 5);
	}
	
	/*Camera infos
	 * distance: distance to the camera
	 * dsp:  0,hide 1,show
	 * type: 0,speed camera   1,monitoring camera 2,traffic light camera 3,regular camera 
	 * 4,bus lane camera 5,emergency lane camera 
	*/
	public void sendCameraDistanceEx(int distance,int dsp,int type) {
		// TODO Auto-generated method stub
		byte[] buf = new byte[5];
 	     buf[0] = 0x13;
 	     buf[1] = (byte)(distance&0xFF);
 	     buf[2] = (byte)((distance&0xFF00)>>8);
 	     buf[3] = (byte)(dsp);
 	     buf[4] = (byte)(type);
 	     packAndSendMsg(buf, 5);
	}
		
	//LaneInfo 
	/*
	 * lanes count:  laneInfo.length()
	 * laneInfo[0..n]  0:regular  1:highlight 
	 * */
	public void sendLaneInfo(int[] laneInfo) {
		// TODO Auto-generated method stub
		if(laneInfo == null){
		    byte[] buf = new byte[2];
		    buf[0] = 0x11;
		    buf[1] = 0x0;
			packAndSendMsg(buf, buf.length);				
		}else{
			byte[] buf = new byte[2+laneInfo.length];
			buf[0] = 0x11;
			buf[1] = (byte)laneInfo.length;
			for(int i=2;i<buf.length;i++)
				buf[i] = (byte)laneInfo[i-2];
		    packAndSendMsg(buf, buf.length);
	  }
    }
	//LaneInfo
	/*
	 * lanes count:  laneInfo.length()/2
	 * laneInfo[i*2] background lane icon
	 * laneInfo[i*2+1] foreground lane icon
	 * icon type: 0,straight 
	 *            1,left
	 *            2,straight & left
	 *            3,right
	 *            4,straight & right
	 *            5,left turn around
	 *            6,left & right
	 *            7,left & straight & right
	 *            8,right turn around
	 *            9,straight&left turn around
	 *            a,straight&right
	 *            b,left& left turn around
	 *            c,right& right turn around
	 *            d,reserved
	 *            e,reserved
	 *            f,reserved
	 * */
	public void sendLaneInfoEx(int[] laneInfo) {
		// TODO Auto-generated method stub
		if(laneInfo == null){
		    byte[] buf = new byte[1];
		    buf[0] = 0x12;
//			    buf[1] = 0x0;
			packAndSendMsg(buf, buf.length);				
		}else{
			byte[] buf = new byte[1+laneInfo.length];
			buf[0] = 0x12;
//				buf[1] = (byte)laneInfo.length;
			for(int i=1;i<buf.length;i++)
				buf[i] = (byte)laneInfo[i-1];
		    packAndSendMsg(buf, buf.length);
	 }
  }
	//sendNextRoadName	
	public void sendNextRoadName(String roadName){
		if(roadName == null) return;
		byte[] buf = new byte[1+roadName.getBytes().length];
		buf[0] = 0x05;
		System.arraycopy(roadName.getBytes(), 0, buf, 1, roadName.getBytes().length);
		packAndSendMsg(buf, buf.length);
	}
		
	//reserved	
	public void sendRoundAboutNum(int num){
		byte[] buf = new byte[2];
		buf[0] = 0x14;
		buf[1] = (byte)num;
		packAndSendMsg(buf, buf.length);
	}
		
	//reserved
	private void notifyAutoNaviState(int flag){
		byte[] message = new byte[2];
		message[0] = (byte)0xA0;
		message[1] = (byte)flag;
		int len = 2;
		
		byte[] szMsg = new byte[len + 5];
		szMsg[0] = (byte) 0xFF;
		szMsg[1] = (byte) 0x55;
		szMsg[2] = (byte) (len + 1);
		szMsg[3] = (byte) 0x08;
		System.arraycopy(message, 0, szMsg, 4, len);

		byte sum = 0;
		int s = 0;
		int i = 0;
		for (i = 2; i < len + 4; i++) {
			s += (szMsg[i] & 0xFF);
		}
		sum = (byte) (s & 0xFF);
		szMsg[len + 4] = sum;
		
		ModuleDispatcher.getIns().send2Device(szMsg,len+5);
	}
	
	//reserved
	public void sendCurrentTime() {
		// TODO Auto-generated method stub
		long time = System.currentTimeMillis();
	    final Calendar calendar = Calendar.getInstance();
	    calendar.setTimeInMillis(time);
	    int hour = calendar.get(Calendar.HOUR_OF_DAY);
	    int minute = calendar.get(Calendar.MINUTE);
	    int second = calendar.get(Calendar.SECOND);
		byte buf[] = new byte[4];
		buf[0] = 0x07;
		buf[1] = (byte)hour; 
		buf[2] = (byte)minute;
		buf[3] = (byte)second;
		Log.d("ing", "sendCurrentTime"+hour+","+minute+","+second);
		packAndSendMsg(buf, buf.length);
	}
}
