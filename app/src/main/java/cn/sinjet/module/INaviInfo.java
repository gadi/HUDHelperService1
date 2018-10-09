package cn.sinjet.module;

public interface INaviInfo {
	  public static final int STEP_INFO = 1;
	  public static final int LINE_INFO = 2;
	  public static final int LIMIT_SPEED = 3;
	  public static final int CAMERA_DISTANCE = 4;
	  public static final int LANE_INFO = 5;
	  public static final int BAIDU_NAVIGATING = 6;
	  public static final int AMAP_NAVIGATING = 7;
	  public static final int ROAD_NAME = 8;
	  public static final int LANE_INFO_EX = 9;
	  public static final int ROUND_ABOUT_NUM = 10;
	 void onStepInfo(int iconType,int distance);
	 void onLineInfo(int retainDistance);
	 void onAmapNavigating(boolean isNavigating);
	 void onLimitSpeed(int speed,int limitSpeed);
	 void onCameraDistance(int distance,int dsp,int type);
	 void onLaneInfo(int[] laneInfo);
	 void onLaneInfoEx(int[] laneInfo);
	 void onNextRoadName(String roadName);
	 void onRoundAboutNum(int num);
}
