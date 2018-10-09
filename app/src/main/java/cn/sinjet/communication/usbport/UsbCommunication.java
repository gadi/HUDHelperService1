package cn.sinjet.communication.usbport;

import java.util.concurrent.atomic.AtomicBoolean;

import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbRequest;
import android.util.Log;
import cn.sinjet.util.HexDump;

public class UsbCommunication {
	   private static final String tag = UsbCommunication.class.getSimpleName();
//       public final static int mVendorId = 1155;
//       public final static int mProductId = 22336;
       public final static int SINJET_VENTOR_ID = 33827;
       public final static int PRODUCT_ID_1 = 513;
       public final static int PRODUCT_ID_2 = 514;
       final static int USB_TIMEOUT_IN_MS = 5000;

       private UsbEndpoint mEndpointIn = null;
       private UsbEndpoint mEndpointOut = null;
       private UsbDeviceConnection mConnection = null;
       private UsbDevice mDevice = null;
       private UsbInterface mUsbInterface = null;
       private Thread mSendThread = null;
       private Thread mRecvThread = null;
       
       protected SerialBuffer serialBuffer;
       protected WorkerThread workerThread;
       protected WriteThread writeThread;
       protected ReadThread readThread;
       
       private static boolean mr1Version;
       protected boolean asyncMode = true;
       private UsbRequest requestIN;
    // Get Android version if version < 4.3 It is not going to be asynchronous read operations
       static
       {
//           if(android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.JELLY_BEAN_MR1)
//               mr1Version = true;
//           else
               mr1Version = false;
       }
       
       public UsbCommunication(final UsbDevice usbDevice,final UsbDeviceConnection connection){
    	   mDevice = usbDevice;
           mConnection = connection;
           serialBuffer = new SerialBuffer(mr1Version);
           // Initialize UsbRequest
       }
       
       public static boolean isSinjetUsbDevice(int vendorId,int productId){
	       	if(vendorId != SINJET_VENTOR_ID) return false;
	       	if(productId ==  PRODUCT_ID_1
	       	  ||productId == PRODUCT_ID_2) 
	       	return true;
	       	return false;
       }
       OnUsbReceivedListener mOnUsbReceivedListener = null;
       public interface OnUsbReceivedListener{
    	   void onUsbReceived(byte[] data,int len);
       }
       public void setOnUsbReceivedListener(OnUsbReceivedListener listener){
    	   mOnUsbReceivedListener = listener;
       }
       
       
       private boolean openPort(){
    	   if(mDevice == null || mConnection == null) 
    		   return false;
    	   
    	   UsbEndpoint endpointIn = null;
		   UsbEndpoint endpointOut = null;
		   printLog("interface count:"+mDevice.getInterfaceCount());
		   
		   for(int i=0;i<mDevice.getInterfaceCount();i++){
		       boolean claimResult = mConnection.claimInterface(mDevice.getInterface(i), true);
		       printLog("clarm interface result:"+claimResult);
		       if(claimResult){
		    	   final UsbInterface usbInterface = mDevice.getInterface(i);
		    	   usbInterface.getInterfaceClass();
		    	   
		    	   for(int j=0;j<usbInterface.getEndpointCount();j++){
		    	         final UsbEndpoint endpoint = usbInterface.getEndpoint(j);
		    	         if(endpoint.getDirection() == UsbConstants.USB_DIR_IN)
		    	        	  endpointIn = endpoint;
		    	         else if(endpoint.getDirection() == UsbConstants.USB_DIR_OUT)
		    	        	 endpointOut = endpoint;
		    	   }
		    	   if(endpointIn != null && endpointOut != null){
		    		   mEndpointIn = endpointIn;
		    		   mEndpointOut = endpointOut;
		    		   mUsbInterface = usbInterface;
		    		   return true;
		    	   }else{
		    		   endpointIn = null;
		   			   endpointOut = null;
		    	   }
		       }
		   }
		   return false;
       }
       
       public boolean open(){
    	   if(!openPort()) 
    		   return false;
    	   
    	   requestIN = new UsbRequest();
           requestIN.initialize(mConnection, mEndpointIn);

           // Restart the working thread if it has been killed before and  get and claim interface
           restartWorkingThread();
           restartWriteThread();
           
           setThreadsParams(requestIN, mEndpointOut);
           return true;
       }
       
       public void close(){
    	   killWorkingThread();
    	   killWriteThread();
    	   if(mConnection != null){
    		   mConnection.releaseInterface(mUsbInterface);
    		   mConnection.close();
    	   }
       }
       
       protected void setThreadsParams(UsbRequest request, UsbEndpoint endpoint)
       {
           if(mr1Version)
           {
               workerThread.setUsbRequest(request);
               writeThread.setUsbEndpoint(endpoint);
           }else
           {
               readThread.setUsbEndpoint(request.getEndpoint());
               writeThread.setUsbEndpoint(endpoint);
           }
       }
       public Thread getSendThread(){
    	   return mSendThread;
       }
       public Thread getRecvThread(){
    	   return mRecvThread;
       }
       
       
       private void printLog(String text){
    	   Log.i("usb",": "+text);
//    	   ViewModel.getIns().setViewProperty(R.id.home_bt_status_text, text);
       }
       
       
       public void write(byte[] buffer){
    	   if(asyncMode)
    		   serialBuffer.putWriteBuffer(buffer);
       }
       
       protected class WriteThread extends Thread
       {
           private UsbEndpoint outEndpoint;
           private AtomicBoolean working;

           public WriteThread()
           {
               working = new AtomicBoolean(true);
           }

           @Override
           public void run()
           {
               while(working.get())
               {
                   byte[] data = serialBuffer.getWriteBuffer();
                   if(data.length > 0)
                       mConnection.bulkTransfer(outEndpoint, data, data.length, USB_TIMEOUT_IN_MS);
               }
           }

           public void setUsbEndpoint(UsbEndpoint outEndpoint)
           {
               this.outEndpoint = outEndpoint;
           }

           public void stopWriteThread()
           {
               working.set(false);
           }
       }
       
       
       
       public void killRWThread(){
    	   if(mRecvThread == null && mSendThread == null)
    		   return;
//    	   mRecvThreadWorking = false;
//    	   mSendThreadWorking = false;
    	   try{
	    	   Thread.sleep(1000);
    	   }catch(InterruptedException e){
    		   e.printStackTrace();
    	   }   
    	   mRecvThread = null;
    	   mSendThread = null;
       }
       
       
       protected class WorkerThread extends Thread{
    	   private UsbRequest requestIN;
    	   private AtomicBoolean working;
    	   private UsbCommunication communication;
    	   public WorkerThread(UsbCommunication communication){
    		   this.communication = communication;
    		   working = new AtomicBoolean(true);
//    		   Log.i("read", "new WorkerThread");
    	   }
    	   @Override
    	   public void run(){
    		   while(working.get()){
    			   UsbRequest request = mConnection.requestWait();
    			   if(request != null && request.getEndpoint().getType() == UsbConstants.USB_ENDPOINT_XFER_BULK
                           && request.getEndpoint().getDirection() == UsbConstants.USB_DIR_IN){
//    				   Log.i("read", "ready read:");
    				   byte[] data = serialBuffer.getDataReceived();
//    				   if(data != null)
//    				   Log.i("read", "data len:"+data.length);
    				   serialBuffer.clearReadBuffer();
                       onReceivedData(data);
                    // Queue a new request
                      requestIN.queue(serialBuffer.getReadBuffer(), SerialBuffer.DEFAULT_READ_BUFFER_SIZE);
    			   }
    		   }
    	   }
    	   public void setUsbRequest(UsbRequest request)
           {
               this.requestIN = request;
           }

           public UsbRequest getUsbRequest()
           {
               return requestIN;
           }

           private void onReceivedData(byte[] data)
           {
        	   communication.onReceivedData(data);
           }

           public void stopWorkingThread()
           {
               working.set(false);
           }
       }
       
       protected class ReadThread extends Thread{
    	   private UsbEndpoint inEndpoint;
    	   private AtomicBoolean working;
    	   UsbCommunication communication = null;
           public ReadThread(UsbCommunication communication){
    		   this.communication = communication;
    		   working = new AtomicBoolean(true);
    	   }
    	   @Override
           public void run(){
    		   byte[] dataReceived = null;
    		   while(working.get()){
    			   int numberBytes = 0;
    			   if(inEndpoint != null){
    				   numberBytes = mConnection.bulkTransfer(inEndpoint, serialBuffer.getBufferCompatible(),
                               SerialBuffer.DEFAULT_READ_BUFFER_SIZE, 0);
    			   }
    			   if(numberBytes >0){
    				   dataReceived = serialBuffer.getDataReceivedCompatible(numberBytes);
    				   onReceivedData(dataReceived);
    			   }
    		   }
    	   }
    	   public void setUsbEndpoint(UsbEndpoint inEndpoint){
    		   this.inEndpoint = inEndpoint;
    	   }
    	   public void stopReadThread(){
               working.set(false);
           }
    	   private void onReceivedData(byte[] data){
    		 //onDataReceived handle received data here
    		   communication.onReceivedData(data);
    	   }
    }
   	
    protected void onReceivedData(byte[] data){
	   printLog("recv:"+HexDump.toHexString(data)+" len:"+data.length);
	   if(mOnUsbReceivedListener != null)
		   mOnUsbReceivedListener.onUsbReceived(data, data.length);
    }
       
	public void sendMessage(byte[] message, int length) {
		// TODO Auto-generated method stub
		 printLog("send:"+HexDump.toHexString(message)+" len:"+message.length);
	    write(message);
	}
	
	
	/*
     * Kill workingThread; This must be called when closing a device
     */
    protected void killWorkingThread()
    {
        if(mr1Version && workerThread != null)
        {
            workerThread.stopWorkingThread();
            workerThread = null;
        }else if(!mr1Version && readThread != null)
        {
            readThread.stopReadThread();
            readThread = null;
        }
    }

    /*
     * Restart workingThread if it has been killed before
     */
    protected void restartWorkingThread()
    {
        if(mr1Version && workerThread == null)
        {
            workerThread = new WorkerThread(this);
            workerThread.start();
            while(!workerThread.isAlive()){} // Busy waiting
        }else if(!mr1Version && readThread == null)
        {
            readThread = new ReadThread(this);
            readThread.start();
            while(!readThread.isAlive()){} // Busy waiting
        }
    }

    protected void killWriteThread()
    {
        if(writeThread != null)
        {
            writeThread.stopWriteThread();
            writeThread = null;
            serialBuffer.resetWriteBuffer();
        }
    }

    
    protected void restartWriteThread()
    {
        if(writeThread == null)
        {
            writeThread = new WriteThread();
            writeThread.start();
            while(!writeThread.isAlive()){} // Busy waiting
        }
    }
}
