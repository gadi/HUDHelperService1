package cn.sinjet.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import cn.sinjet.module.ToDevNaviCodec;


public class NaviHelperCommandReceiver extends BroadcastReceiver {
    private ToDevNaviCodec mNaviCodec = new ToDevNaviCodec();

    @Override
    public void onReceive(final Context context, Intent intent) {
        Float f;
        //Log.d("ssss", "NaviHelperCommandReceiver");
        String valueDistance = intent.getStringExtra("Distance");
        String valueInstructionCode = intent.getStringExtra("InstructionCode");
        Log.d("ssss", "valueInstructionCode - " + valueInstructionCode);

        String[] parts = valueDistance.split(" ");


        if (parts[0].contains(".")) {
            int distance = Math.round(Float.valueOf(parts[0]) * 1000);
            Log.d("ssss", "is Float - " + distance);
            mNaviCodec.sendnaviInfo(getImageCodeInstruction(valueInstructionCode), distance);
        } else {
            int distance = Integer.valueOf(parts[0]);
            Log.d("ssss", "not Float - " + distance);
            mNaviCodec.sendnaviInfo(getImageCodeInstruction(valueInstructionCode), distance);
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
}
