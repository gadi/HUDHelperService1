package cn.sinjet.util;

import java.io.UnsupportedEncodingException;

public class Data {
	public static int byte2int(byte bufL,byte bufH){
		int int1 = byte2int(bufL); int int2 = byte2int(bufH);
		return ((int)(int1 | int2<<8))&0x0000FFFF;
	}
	
	public static int byte2int(byte buf1,byte buf2,byte buf3){
		int int1 = byte2int(buf1); int int2 = byte2int(buf2); int int3 = byte2int(buf3);
		return ((int)(int1 | int2<<8|int3<<16))&0x00FFFFFF;
	}
	public static int byte2int(byte buf){
		return ((int)buf)&0x000000FF;
	}
	public static int byteH4(byte buf){
		return (int)((buf & 0xF0)>>4);
	}
	public static int byteL4(byte buf){
		return (int)(buf & 0x0F);
	}
	
	public static String byte2Unicode(byte bufSrc[], int st, int end){//不包含bEnd
		if(st+1 >= end) return "";
	    StringBuffer sb = new StringBuffer("");
	    for(int j = st; j < end; ){
		    int high = bufSrc[j++];
		    if (high < 0) high += 256;
		    int low = bufSrc[j++];
		    if (low < 0) low += 256;
		    char c = (char)(low + (high << 8));
		    sb.append(c);
	    }
	    return sb.toString();
    }
    public static String byte2Unicode(byte abyte[], int len){
        return byte2Unicode(abyte, 0, len);
    }
    public static String byte2Unicode(byte abyte[]){
        return byte2Unicode(abyte, 0, abyte.length);
    }
    public static byte[] unicode2Byte(String s){
	    int len = s.length();
	    byte abyte[] = new byte[len << 1];
	    int j = 0;
	    for(int i = 0; i < len; i++){
		    char c = s.charAt(i);
		    abyte[j++] = (byte)(c & 0xff);
		    abyte[j++] = (byte)(c >> 8);
	    }
	    return abyte;
    }
    
    public static int bit0(byte buf) {
		// TODO Auto-generated method stub
		return ((buf&0x01)==0x01)?1:0;
	}
    public static int bit1(byte buf) {
		// TODO Auto-generated method stub
		return ((buf&0x02)==0x02)?1:0;
	}
    public static int bit2(byte buf) {
		// TODO Auto-generated method stub
		return ((buf&0x04)==0x04)?1:0;
	}
    public static int bit3(byte buf) {
		// TODO Auto-generated method stub
		return ((buf&0x08)==0x08)?1:0;
	}
    public static int bit4(byte buf) {
		// TODO Auto-generated method stub
		return ((buf&0x10)==0x10)?1:0;
	}
    public static int bit5(byte buf) {
		// TODO Auto-generated method stub
		return ((buf&0x20)==0x20)?1:0;
	}
    public static int bit6(byte buf) {
		// TODO Auto-generated method stub
		return ((buf&0x40)==0x40)?1:0;
	}
    public static int bit7(byte buf) {
		// TODO Auto-generated method stub
		return ((buf&0x80)==0x80)?1:0;
	}
}
