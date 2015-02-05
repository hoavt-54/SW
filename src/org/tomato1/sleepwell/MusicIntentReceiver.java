package org.tomato1.sleepwell;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

public class MusicIntentReceiver extends android.content.BroadcastReceiver {
	public static final String ACTION_AUDIO_BECOMING_NOISY = "dfasdfasdfasdf";
	public static final String ACTION_KEY_CODE_PLAY = "fasdfasdf";
	   @Override
	   public void onReceive(Context ctx, Intent intent) {
		   
	      if (intent.getAction().equals(
	                    android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
	    	  	Intent intentNoisy = new Intent(ACTION_AUDIO_BECOMING_NOISY);
	    	  ctx.sendBroadcast(intentNoisy);
	      }
	      
	      else if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
	            KeyEvent event = (KeyEvent)intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
	            Intent intentKeyPlay = new Intent(ACTION_KEY_CODE_PLAY);
		    	  ctx.sendBroadcast(intentKeyPlay);
	        }
	      
	   }
	   
	   
	   

	}