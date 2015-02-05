package org.tomato1.sleepwell;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;

public class SoundPlayerService extends Service implements MediaPlayer.OnErrorListener{
	
	public static final String LOG_TAG = "SoundPlayerService";
	public static final boolean isDebug = false;
	public static final String PLAYER_ID = "player_id";
	public static final String ACTION_TYPE = "fasdasdfads";
	public static final String VOULUME = "voulume";
	public static final String STOP_POINT_PLAY = "duration";
	public static final String EXTRA_SHUFFLE = "EXTRA_SHUFFLE";
	private boolean isPlaying = false;
	private List<MediaPlayer> lastStates;
	
	
	private MediaPlayer rainMediaPlayer;
	private MediaPlayer naturalMediaPlayer;
	private MediaPlayer windMediaPlayer;
	private MediaPlayer wavesMediaPlayer;
	private MediaPlayer guitarMediaPlayer;
	private MediaPlayer meditaionMediaPlayer;
	
	private boolean isRainPlaying = false;
	private boolean isNaturalPlaying = false;
	private boolean isWindPlaying = false;
	private boolean isWavesPlaying = false;
	private boolean isGuitarPlaying = false;
	private boolean isMeditaionPlaying = false;
	
	public static final int PLAYER_MAX_VOLUME = 100;
	private long timeStopPoint = - 1;
	private SharedPreferences mSettings;
	private MusicIntentReceiver musicReceiver = new MusicIntentReceiver();
	private MusicServiceIntentReceiver musicServiceIntentReceiver = new MusicServiceIntentReceiver();
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy hh:mm:ss");
	
	Handler timerHandler = new Handler();
    Runnable timerStopServiceRunnable = new Runnable() {
    	
        @Override
        public void run() {
        	if(isDebug)
        	Log.v(LOG_TAG, "runable to stop all player");
        	timeStopPoint = -1;
        	if (isPlaying())
        	onSoundBecomeNoisy();	
        }
    };
	
	
	@Override
	public void onCreate() {
		registerReceiver(musicReceiver, new IntentFilter(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY));
		registerReceiver(musicReceiver, new IntentFilter(Intent.ACTION_MEDIA_BUTTON));
		registerReceiver(musicServiceIntentReceiver, new IntentFilter(MusicIntentReceiver.ACTION_AUDIO_BECOMING_NOISY));
		registerReceiver(musicServiceIntentReceiver, new IntentFilter(MusicIntentReceiver.ACTION_KEY_CODE_PLAY));
		mSettings = PreferenceManager.getDefaultSharedPreferences(this);
		super.onCreate();
		
	}

	
	
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		

		
		Editor editor = mSettings.edit();
		int requestedPlayer = intent.getIntExtra(PLAYER_ID, 0);
		int action = intent.getIntExtra(ACTION_TYPE, MainActivity.ACTION_PLAY_PAUSE);
		
		if (action == MainActivity.ACTION_TIMER){
			
			
			long timePoint = intent.getLongExtra(STOP_POINT_PLAY, new Date().getTime() + 20 * 60 * 1000);
			long currentTime = new Date().getTime();
			if(isDebug)
			Log.v(LOG_TAG, "set timmer, current time: " + dateFormat.format(currentTime) + " stop at: " +dateFormat.format(timePoint));
			if (timePoint > currentTime)
				onTimerChanged(timePoint);
			
			
			return START_NOT_STICKY;
		}
		
		// set timer on the first time play
		if ( !isPlaying()){
			if(isDebug)
			Log.v(LOG_TAG, "no player is playing now");
			if (timeStopPoint == -1){
				if(isDebug)
				Log.v(LOG_TAG, "timer not set yet");
				timerHandler.postDelayed(timerStopServiceRunnable, 20 * 60 * 1000);
				 Calendar c = Calendar.getInstance();
				 c.add(Calendar.MINUTE, 20);
				 editor.putInt(MainActivity.HOUR_KEY, c.get(Calendar.HOUR_OF_DAY));
		         editor.putInt(MainActivity.MINUTE_KEY, c.get(Calendar.MINUTE));
			}
			
		}
		
		if(isDebug)
		Log.v(LOG_TAG, "Player: " + requestedPlayer + "   action: " + action);
		switch (requestedPlayer) {
		case MainActivity.RAIN_PLAYER:
			if (action == MainActivity.ACTION_PLAY_PAUSE){
				if (isRainPlaying){
					isRainPlaying = false;
					stop(rainMediaPlayer);
					editor.putBoolean(MainActivity.RAIN_PLAYING_KEY, false);
				}
				else {
					play(rainMediaPlayer, R.raw.rain);
					isRainPlaying = true;
					editor.putBoolean(MainActivity.RAIN_PLAYING_KEY, true);
				}
			
			}
			else if(action == MainActivity.ACTION_VOLUME){
				if (rainMediaPlayer != null && isRainPlaying){
					float volume = intent.getIntExtra(VOULUME, 50);
					if(isDebug)
					Log.v(LOG_TAG, "volume changed: "  + volume/ (float) 100);
					rainMediaPlayer.setVolume(volume/ (float) 100, volume/ (float) 100);
					
				}
			} 
		
			break;
			
			
		
		case MainActivity.NATURAL_PLAYER:
			if (action == MainActivity.ACTION_PLAY_PAUSE){
				if (isNaturalPlaying){
					if(isDebug)
					Log.v(LOG_TAG, "natural is playing and we have to stop it");
					isNaturalPlaying = false;
					stop(naturalMediaPlayer);
					
					editor.putBoolean(MainActivity.NATURAL_PLAYING_KEY, false);
					
				}
				else {
					if(isDebug)
					Log.v(LOG_TAG, "natural is null, we have to create it");
					play(naturalMediaPlayer, R.raw.natural);
					isNaturalPlaying = true;
					editor.putBoolean(MainActivity.NATURAL_PLAYING_KEY, true);
				}
			}
			else if(action == MainActivity.ACTION_VOLUME){
				if (naturalMediaPlayer != null && isNaturalPlaying){
					float volume = intent.getIntExtra(VOULUME, 50);
					naturalMediaPlayer.setVolume(volume/ (float) 100, volume/ (float) 100);

				}
			
			}
			break;
			
			
			
		case MainActivity.WIND_PLAYER:
			if (action == MainActivity.ACTION_PLAY_PAUSE){
				if (isWindPlaying){
					isWindPlaying = false;
					stop(windMediaPlayer);
					
					editor.putBoolean(MainActivity.WIND_PLAYING_KEY, false);
				}
				else {
					play(windMediaPlayer, R.raw.wind);
					isWindPlaying = true;
					editor.putBoolean(MainActivity.WIND_PLAYING_KEY, true);
				}
			
			}
			else if(action == MainActivity.ACTION_VOLUME){
				if (windMediaPlayer != null && isWindPlaying){
					float volume = intent.getIntExtra(VOULUME, 50);
					if(isDebug)
					Log.v(LOG_TAG, "volume changed: "  + volume/ (float) 100);
					windMediaPlayer.setVolume(volume/ (float) 100, volume/ (float) 100);
					
				}
			} 
		
			break;
		
			
		
			
		case MainActivity.WAVES_PLAYER:
			if (action == MainActivity.ACTION_PLAY_PAUSE){
				if (isWavesPlaying){
					isWavesPlaying = false;
					stop(wavesMediaPlayer);
					
					editor.putBoolean(MainActivity.WAVES_PLAYING_KEY, false);
				}
				else {
					play(wavesMediaPlayer, R.raw.waves);
					isWavesPlaying = true;
					editor.putBoolean(MainActivity.WAVES_PLAYING_KEY, true);
				}
			
			}
			else if(action == MainActivity.ACTION_VOLUME){
				if (wavesMediaPlayer != null && isWavesPlaying){
					float volume = intent.getIntExtra(VOULUME, 50);
					if(isDebug)
					Log.v(LOG_TAG, "volume changed: "  + volume/ (float) 100);
					wavesMediaPlayer.setVolume(volume/ (float) 100, volume/ (float) 100);
				}
			} 
		
			break;
			
			
			
			
		case MainActivity.GUITAR_PLAYER:
			if (action == MainActivity.ACTION_PLAY_PAUSE){
				if (isGuitarPlaying){
					isGuitarPlaying = false;
					stop(guitarMediaPlayer);
					
					editor.putBoolean(MainActivity.GUITAR_PLAYING_KEY, false);
				}
				else {
					play(guitarMediaPlayer, R.raw.guitar);
					isGuitarPlaying = true;
					editor.putBoolean(MainActivity.GUITAR_PLAYING_KEY, true);
				}
			
			}
			else if(action == MainActivity.ACTION_VOLUME){
				if (guitarMediaPlayer != null && isGuitarPlaying){
					float volume = intent.getIntExtra(VOULUME, 50);
					if(isDebug)
					Log.v(LOG_TAG, "volume changed: "  + volume/ (float) 100);
					guitarMediaPlayer.setVolume(volume/ (float) 100, volume/ (float) 100);
				}
			} 
		
			break;
			
			
			
			
		case MainActivity.MEDIATION_PLAYER:
			if (action == MainActivity.ACTION_PLAY_PAUSE){
				if (isMeditaionPlaying){
					isMeditaionPlaying = false;
					stop(meditaionMediaPlayer);
					
					editor.putBoolean(MainActivity.MEDITATION_PLAYING_KEY, false);
				}
				else {
					play(meditaionMediaPlayer, R.raw.meditation);
					isMeditaionPlaying = true;
					editor.putBoolean(MainActivity.MEDITATION_PLAYING_KEY, true);
				}
			
			}
			else if(action == MainActivity.ACTION_VOLUME){
				if (meditaionMediaPlayer != null && isMeditaionPlaying){
					float volume = intent.getIntExtra(VOULUME, 50);
					if(isDebug)
					Log.v(LOG_TAG, "volume changed: "  + volume/ (float) 100);
					meditaionMediaPlayer.setVolume(volume/ (float) 100, volume/ (float) 100);
				}
			} 
		
			break;

		default:
			break;
		}
		
		
		editor.apply();
		
		return (START_NOT_STICKY);
	}
	
	
 
	@Override
	public void onDestroy() {
		isPlaying = false;
		unregisterReceiver(musicServiceIntentReceiver);
		unregisterReceiver(musicReceiver);
		mSettings.edit().putBoolean(MainActivity.RAIN_PLAYING_KEY, false).apply();
		mSettings.edit().putBoolean(MainActivity.NATURAL_PLAYING_KEY, false).apply();
		mSettings.edit().putBoolean(MainActivity.WIND_PLAYING_KEY, false).apply();
		mSettings.edit().putBoolean(MainActivity.WAVES_PLAYING_KEY, false).apply();
		mSettings.edit().putBoolean(MainActivity.GUITAR_PLAYING_KEY, false).apply();
		mSettings.edit().putBoolean(MainActivity.MEDITATION_PLAYING_KEY, false).apply();
		onSoundBecomeNoisy();
	}
	 
	
	private void initPlayer (MediaPlayer player, int fileId){
		
		if (fileId == R.raw.rain){
			if(isDebug)
			Log.v(LOG_TAG, "before create rain player");
			rainMediaPlayer = MediaPlayer.create(getApplicationContext(), fileId);
			rainMediaPlayer.setOnErrorListener(this);
			
			
			//rainMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
			rainMediaPlayer.setLooping(true);
			rainMediaPlayer.start();
			try {
				float volume = mSettings.getInt(MainActivity.RAIN_VOLUME_KEY,
						50);
				rainMediaPlayer.setVolume(volume / (float) 100, volume
						/ (float) 100);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		else if (fileId == R.raw.natural){
			if(isDebug)
			Log.v(LOG_TAG, "before create natural player");
			naturalMediaPlayer = MediaPlayer.create(getApplicationContext(), fileId);
			//naturalMediaPlayer.setOnErrorListener(this);
			
			//naturalMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
			naturalMediaPlayer.setLooping(true);
			naturalMediaPlayer.start(); 
			
			try {
				float volume = mSettings.getInt(MainActivity.NATUAL_VOLUME_KEY,
						50);
				naturalMediaPlayer.setVolume(volume / (float) 100, volume
						/ (float) 100);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(isDebug)
			Log.v(LOG_TAG, "after creating natural player");
			if(isDebug)
			Log.v(LOG_TAG, "natural player == null: "+ (naturalMediaPlayer == null));
		}
		else if (fileId == R.raw.wind){
			if(isDebug)
			Log.v(LOG_TAG, "before create wind player");
			windMediaPlayer = MediaPlayer.create(getApplicationContext(), fileId);
			windMediaPlayer.setOnErrorListener(this);
			
			//windMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
			windMediaPlayer.setLooping(true); 
			windMediaPlayer.start(); 
			float volume = mSettings.getInt(MainActivity.WIND_VOLUME_KEY, 50);
			windMediaPlayer.setVolume(volume/ (float)100, volume/ (float)100);
		}
		else if (fileId == R.raw.waves){
			
			wavesMediaPlayer = MediaPlayer.create(getApplicationContext(), fileId);
			wavesMediaPlayer.setOnErrorListener(this);
			
			//wavesMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
			wavesMediaPlayer.setLooping(true);
			wavesMediaPlayer.start(); 
			float volume = mSettings.getInt(MainActivity.WAVES_VOLUME_KEY, 50);
			wavesMediaPlayer.setVolume(volume/ (float)100, volume/ (float)100);
		}
		else if (fileId == R.raw.guitar){
			
			guitarMediaPlayer = MediaPlayer.create(getApplicationContext(), fileId);
			guitarMediaPlayer.setOnErrorListener(this);
			
			//guitarMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
			guitarMediaPlayer.setLooping(true);
			guitarMediaPlayer.start(); 
			float volume = mSettings.getInt(MainActivity.GUITAR_VOLUME_KEY, 50);
			guitarMediaPlayer.setVolume(volume/ (float)100, volume/ (float)100);
		}
		else if (fileId == R.raw.meditation){
			
			meditaionMediaPlayer = MediaPlayer.create(getApplicationContext(), fileId);
			meditaionMediaPlayer.setOnErrorListener(this);
			
			//meditaionMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
			//meditaionMediaPlayer.setLooping(true);
			try {
				meditaionMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
					
					@Override
					public void onCompletion(MediaPlayer mp) {
						isMeditaionPlaying = false;
						
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
				meditaionMediaPlayer = MediaPlayer.create(getApplicationContext(), fileId);
			}
			
			meditaionMediaPlayer.start();
			float volume = mSettings.getInt(MainActivity.MEDITATION_VOLUME_KEY, 50);
			meditaionMediaPlayer.setVolume(volume/ (float)100, volume/ (float)100);
		}
		
	}
	
	
	private void releasePlayer (MediaPlayer player){
		player.release();
		player = null;
	}
	
	private boolean isPlaying (){
		return (isRainPlaying) 
				|| (isNaturalPlaying)
				||(isGuitarPlaying)
				|| (isWavesPlaying)
				|| (isWindPlaying)
				|| (isMeditaionPlaying);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return (null);
	}

	private void play(MediaPlayer player, int fileId) {
		if(isDebug)
		Log.w(getClass().getName(), "Got to play()!");
		isPlaying = true;

		Notification note = new Notification(R.drawable.ic_stat_dreamstimecomp_20573721,
				getString(R.string.notification_message), System.currentTimeMillis());
		Intent i = new Intent(this, MainActivity.class);
		//note.largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.sleepwell_luncher_icon);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);

		note.setLatestEventInfo(this, getString(R.string.app_name),
				getString(R.string.notification_message), pi);
		note.flags |= Notification.FLAG_NO_CLEAR;

		startForeground(1337, note);
		initPlayer(player, fileId);
		
	}

	private void stop(MediaPlayer player) {
		if(isDebug)
		Log.w(getClass().getName(), "Got to stop()!");

		if (player != null){
			if(isDebug)
			Log.v(LOG_TAG, "beginning of stop method");
			try {
				if (player.isPlaying())
				player.stop();
				Log.v(LOG_TAG, "after we call stop()");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
			try{
				player.reset();
				releasePlayer(player);
				if(isDebug)
				Log.v(LOG_TAG, "after we call release");
			}catch (Exception e){
				e.printStackTrace();
			}
			
		try {
			if(isDebug)
			Log.v(LOG_TAG, "isplaying: " + isPlaying());
			if (!isPlaying())
				stopForeground(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) { 
		
		return false;
	}
	
	
	public void onSoundBecomeNoisy(){
		if (isRainPlaying){
			try{
			stop(rainMediaPlayer);
			}catch (Exception e){
				e.printStackTrace();
			}
			isRainPlaying = false;
			mSettings.edit().putBoolean(MainActivity.RAIN_PLAYING_KEY, false).apply();
			
		}
		if (isGuitarPlaying){
			try{
			stop(guitarMediaPlayer);
			}catch (Exception e){
				e.printStackTrace();
			}
			isGuitarPlaying = false;
			mSettings.edit().putBoolean(MainActivity.GUITAR_PLAYING_KEY, false).apply();
		}
		if (isMeditaionPlaying) {
			try{
			stop(meditaionMediaPlayer);
			}catch (Exception e){
				e.printStackTrace();
			}
			isMeditaionPlaying = false;
			mSettings.edit().putBoolean(MainActivity.MEDITATION_PLAYING_KEY, false).apply();
		}
		if (isNaturalPlaying){
			try{
			stop(naturalMediaPlayer);
			}catch (Exception e){
				e.printStackTrace();
			}
			isNaturalPlaying = false;
			mSettings.edit().putBoolean(MainActivity.NATURAL_PLAYING_KEY, false).apply();
		}
		if (isWavesPlaying){
			try{
			stop(wavesMediaPlayer);
			}catch (Exception e){
				e.printStackTrace();
			}
			isWavesPlaying = false;
			mSettings.edit().putBoolean(MainActivity.WAVES_PLAYING_KEY, false).apply();
		}
		if (isWindPlaying) {
			try{
			stop(windMediaPlayer);
			}catch (Exception e){
				e.printStackTrace();
			}
			isWindPlaying = false;
			mSettings.edit().putBoolean(MainActivity.WIND_PLAYING_KEY, false).apply();
		}
		stopForeground(true);
	}
	
	public void onKeyCodePlay (){
		if (isRainPlaying || isGuitarPlaying || isMeditaionPlaying 
				|| isNaturalPlaying || isWavesPlaying || isWindPlaying){
			lastStates = new ArrayList<MediaPlayer>();
		
			if (isRainPlaying){
				stop(rainMediaPlayer);
				lastStates.add(rainMediaPlayer);
				mSettings.edit().putBoolean(MainActivity.RAIN_PLAYING_KEY, false).apply();
			}
			if (isGuitarPlaying){
				stop(guitarMediaPlayer);
				lastStates.add(guitarMediaPlayer);
				mSettings.edit().putBoolean(MainActivity.GUITAR_PLAYING_KEY, false).apply();
			}
			if (isMeditaionPlaying) {
				stop(meditaionMediaPlayer);
				lastStates.add(meditaionMediaPlayer);
				mSettings.edit().putBoolean(MainActivity.MEDITATION_PLAYING_KEY, false).apply();
			}
			if (isNaturalPlaying){
				stop(naturalMediaPlayer);
				lastStates.add(naturalMediaPlayer);
				mSettings.edit().putBoolean(MainActivity.NATURAL_PLAYING_KEY, false).apply();
			}
			if (isWavesPlaying){
				stop(wavesMediaPlayer);
				lastStates.add(wavesMediaPlayer);
				mSettings.edit().putBoolean(MainActivity.WAVES_PLAYING_KEY, false).apply();
			}
			if (isWindPlaying) {
				stop(windMediaPlayer);
				lastStates.add(windMediaPlayer);
				mSettings.edit().putBoolean(MainActivity.WIND_PLAYING_KEY, false).apply();
			}
		}
		
		if (lastStates != null && !isRainPlaying && !isGuitarPlaying && !isMeditaionPlaying 
				&& !isNaturalPlaying && !isWavesPlaying && !isWindPlaying){
			for (MediaPlayer player : lastStates){
				play(player, getFileId(player));
			}
			lastStates = null;
		}
	}
	
    public class LocalBinder extends Binder {
        SoundPlayerService getService() {
            return SoundPlayerService.this;
        }
    }
    
    
   

    
    public int getFileId (MediaPlayer player){
    	if (player == rainMediaPlayer)
    		return R.raw.rain;
    	else if (player == naturalMediaPlayer)
    		return R.raw.natural;
    	else if (player == wavesMediaPlayer)
    		return R.raw.waves;
    	else if (player == windMediaPlayer)
    		return R.raw.wind;
    	else if (player == guitarMediaPlayer)
    		return R.raw.guitar;
    	else if (player == meditaionMediaPlayer)
    		return R.raw.meditation;
    	else return 0;
    }
    
    
    public void onTimerChanged (long timePoint){
    	
    	timerHandler.removeCallbacks(timerStopServiceRunnable);
    	timeStopPoint = timePoint;
    	long currentTime = new Date().getTime();
    	timerHandler.postDelayed(timerStopServiceRunnable, timeStopPoint - currentTime);
    	
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    /*
     * make MusicIntentRecevier -> inner class
     * */
    public class MusicServiceIntentReceiver extends android.content.BroadcastReceiver {
    	public static final String ACTION_AUDIO_BECOMING_NOISY = "dfasdfasdfasdf";

    	
    		public MusicServiceIntentReceiver (){
    			super();
    		}
    		
    		public MusicServiceIntentReceiver (SoundPlayerService service){
    		}
    		
    	   @Override
    	   public void onReceive(Context ctx, Intent intent) {
    		   
    	      if (intent.getAction().equals(
    	                    MusicIntentReceiver.ACTION_AUDIO_BECOMING_NOISY) ){
    	    	  onSoundBecomeNoisy();
    	      }
    	      
    	      else if (MusicIntentReceiver.ACTION_KEY_CODE_PLAY.equals(intent.getAction())) {
    	            KeyEvent event = (KeyEvent)intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
    	            if (KeyEvent.KEYCODE_MEDIA_PLAY == event.getKeyCode()) {
    	            	onKeyCodePlay();
    	            }
    	        }
    	      
    	   }
    	   
    	   
    	   

    	}
    
    
    
    

}