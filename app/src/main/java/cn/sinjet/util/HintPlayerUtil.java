package cn.sinjet.util;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.util.Log;
import cn.sinjet.module.ModuleDispatcher;
import cn.sinjet.sinjetservice.SinjetService;

public class HintPlayerUtil {
	 static String tag = HintPlayerUtil.class.getSimpleName();
	 public  MediaPlayer mMediaPlayer = null;
	 AudioManager audioManager;
	 public HintPlayerUtil(Context context){
		 audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
	 }
     public  void playSound(final int soundId){
       	 //push in the player,
    	 new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				 startPlay(soundId);
			}
		}).start();
    	
     }
     
      
     private  void startPlay(int soundId){
    	 try { 
	    	 if(mMediaPlayer != null){
	    		 mMediaPlayer.release();
	    		 mMediaPlayer = null;
	    	 }
	    	 Context context = SinjetService.getContext();
	    	 AssetFileDescriptor afd = context.getResources().openRawResourceFd(soundId);
		     if (afd == null)  return;
	         mMediaPlayer = new MediaPlayer();
	         mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			 mMediaPlayer.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
			 afd.close();
			 mMediaPlayer.prepare();
			 Log.d(tag,"setAudioStreamType");
			 
			 int result = audioManager.requestAudioFocus(mOnAudioFocusChangeListener, 
					 AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
//			 if(result != AudioManager.AUDIOFOCUS_GAIN_TRANSIENT) return;
			 mMediaPlayer.start();
			 mMediaPlayer.setOnErrorListener(new OnErrorListener() {
				
				@Override
				public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
					// TODO Auto-generated method stub
					Log.e(tag, "OnError - Error code: " + arg1
							+ " Extra code: " + arg2);

					arg0.reset();
					return false;
				}
			});
			mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer player) {
					// TODO Auto-generated method stub
					mMediaPlayer = null;
					player.release();
					audioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
				}
			}); 
	     } catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
     }
     
     OnAudioFocusChangeListener mOnAudioFocusChangeListener = new OnAudioFocusChangeListener() {
		
		@Override
		public void onAudioFocusChange(int focusChange) {
			// TODO Auto-generated method stub
			
		}
	};
}
