package cn.sinjet.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import cn.sinjet.module.ToDevNaviCodec;
import cn.sinjet.sinjetservice.SinjetService;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.LOCATION_SERVICE;


public class NaviHelperCommandReceiver extends BroadcastReceiver {

    private ToDevNaviCodec mNaviCodec = new ToDevNaviCodec();
    private LocationManager locationManager;
    private int lastSpeed = 0;
    String valueInstructionCode;
    String valueDistance;
    String[] parts;
    Message lastMessage;
    int distance;

    @Override
    public void onReceive(final Context context, Intent intent) {

        lastMessage = new Message();
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        valueDistance = intent.getStringExtra("Distance");
        valueInstructionCode = intent.getStringExtra("InstructionCode");
        Log.d("ssss", "valueInstructionCode - " + valueInstructionCode);

        parts = valueDistance.split(" ");

//        if (getSpeed() != null) {
//            lastSpeed = Math.round(getSpeed());
//            Log.d("aaaa", "speed = " + lastSpeed);
//        }
//        mNaviCodec.sendLimitSpeed(110, 100);


        if (parts[0].contains(".")) {
            distance = Math.round(Float.valueOf(parts[0]) * 1000);
            Log.d("ssss", "is Float - " + distance);
            lastMessage.setDistance(distance);
            lastMessage.setImageCode(getImageCodeInstruction(valueInstructionCode));
            mNaviCodec.sendnaviInfo(getImageCodeInstruction(valueInstructionCode), distance);

        } else {
            int distance = Integer.valueOf(parts[0]);
            Log.d("ssss", "not Float - " + distance);
            lastMessage.setDistance(distance);
            lastMessage.setImageCode(getImageCodeInstruction(valueInstructionCode));
            mNaviCodec.sendnaviInfo(getImageCodeInstruction(valueInstructionCode), distance);
        }
    }


    public class Message {
        private int imageCode;
        private int distance;

        public Message() {

        }

        public Message(int imageCode, int distance) {
            this.imageCode = imageCode;
            this.distance = distance;
        }

        public int getImageCode() {
            return imageCode;
        }

        public void setImageCode(int imageCode) {
            this.imageCode = imageCode;
        }

        public int getDistance() {
            return distance;
        }

        public void setDistance(int distance) {
            this.distance = distance;
        }
    }



    public int getImageCodeInstruction(String InstructionCode) {
		/*
	 * iconType:
	    1 default
		2 left                    V
		3 right                   V
		4 leftfront               V
		5 rightfront              V
		6 leftback
		7 rightback
		8 leftandaround           V
		9 straight                V
		10 arriveWaypoint         V
		11 enteraoundabout        V
		12 outroundabout          V
		13 arrivedserviceArea
		14 arrivedtollgate
		15 arriveddestination     V
		16
		17 turn_ring_front        V
		18 turn_ring_left         V
		19 turn_ring_leftback
		20 turn_ring_leftfront
		21 turn_ring_right        V
		22 turn_ring_rightback
		23  turn_ring_rightfront
		24 turn_ring_back         V
	 * */

        if (InstructionCode.equals("TURN_LEFT")) {
            return 2;

        } else if (InstructionCode.equals("TURN_RIGHT")) {
            return 3;

        } else if (InstructionCode.equals("KEEP_LEFT")) {
            return 4;

        } else if (InstructionCode.equals("KEEP_RIGHT")) {
            return 5;

        } else if (InstructionCode.equals("CONTINUE_STRAIGHT")) {
            return 9;

        } else if (InstructionCode.equals("ROUNDABOUT_LEFT")) {
            return 18;

        } else if (InstructionCode.equals("ROUNDABOUT_EXIT_LEFT")) {
            return 18;

        } else if (InstructionCode.equals("ROUNDABOUT_STRAIGHT")) {
            return 17;

        } else if (InstructionCode.equals("ROUNDABOUT_EXIT_STRAIGHT")) {
            return 17;

        } else if (InstructionCode.equals("ROUNDABOUT_RIGHT")) {
            return 21;

        } else if (InstructionCode.equals("ROUNDABOUT_EXIT_RIGHT")) {
            return 21;

        } else if (InstructionCode.equals("ROUNDABOUT_U")) {
            return 24;

        } else if (InstructionCode.equals("ROUNDABOUT_EXIT_U")) {
            return 24;

        } else if (InstructionCode.equals("APPROACHING_DESTINATION")) {
            return 15;

        } else if (InstructionCode.equals("EXIT_LEFT")) {
            return 4;

        } else if (InstructionCode.equals("EXIT_RIGHT")) {
            return 5;

        } else if (InstructionCode.equals("WAYPOINT_DELAY")) {
            return 10;

        } else if (InstructionCode.equals("U_TURN")) {
            return 8;

        } else if (InstructionCode.equals("ROUNDABOUT_ENTER")) {
            return 11;

        } else if (InstructionCode.equals("ROUNDABOUT_EXIT")) {
            return 12;

        }
        return 1;
    }

    public Float getSpeed() {

        Location gpsLocation = null;

        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            if (locationManager != null) {
                try {//first try to find location
                    gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if (gpsLocation == null) {
                        Log.d("aaaa", "gpsLocation is null");
                    } else {
                        Log.d("aaaa", String.valueOf(gpsLocation.getLatitude()));
                        Log.d("aaaa", String.valueOf(gpsLocation.getLongitude()));
                    }
                } catch (SecurityException e) {
                    Log.d("aaaa", "Error: getLastKnownLocation failed - " + e);

                }

                if (gpsLocation == null) {//second try to find location
                    try {
                        gpsLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (gpsLocation != null) {
                            Log.d("aaaa", String.valueOf(gpsLocation.getLatitude()));
                            Log.d("aaaa", String.valueOf(gpsLocation.getLongitude()));
                        } else {
                            Log.d("aaaa", "gpsLocation is null");
                        }
                    } catch (SecurityException e) {
                        Log.d("aaaa", "Error: getLastKnownLocation failed 2");

                    }
                }

            } else {
                Log.d("aaaa", "locationManager is null!");
            }

        }
        return gpsLocation.getSpeed();
    }

    private boolean isServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if("cn.sinjet.sinjetservice".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
