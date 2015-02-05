package org.tomato1.sleepwell;

import java.util.Calendar;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.purplebrain.adbuddiz.sdk.AdBuddiz;

public class MainActivity extends ActionBarActivity implements OnClickListener, OnSharedPreferenceChangeListener{
	public static final String APP_LINK = "https://play.google.com/store/apps/details?id=org.tomato1.sleepwell";
	public static final boolean isDebug = false;
	
	public static final String TIMER_SELECTED_ITEM = "selected_item";
	public static final String HOUR_KEY = "hour";
	public static final String MINUTE_KEY = "minute";
	public static final String NOT_FIRST_TIME_USE_APP = "frist_time_use_app";
	
	public static final String RAIN_VOLUME_KEY = "rain_volume";
	public static final String NATUAL_VOLUME_KEY = "natural_volume";
	public static final String WAVES_VOLUME_KEY = "waves_volume";
	public static final String WIND_VOLUME_KEY = "wind_volume";
	public static final String GUITAR_VOLUME_KEY = "guitar_volume";
	public static final String MEDITATION_VOLUME_KEY = "meditation_volume";
	
	public static final String RAIN_PLAYING_KEY = "rain_playing";
	public static final String NATURAL_PLAYING_KEY = "natural_playing";
	public static final String WAVES_PLAYING_KEY = "waves_playing";
	public static final String WIND_PLAYING_KEY = "wind_playing";
	public static final String GUITAR_PLAYING_KEY = "guitar_playing";
	public static final String MEDITATION_PLAYING_KEY = "meditation_playing";
	
	
	public static final int RAIN_PLAYER = 1;
	public static final int NATURAL_PLAYER = 2;
	public static final int WIND_PLAYER = 3;
	public static final int WAVES_PLAYER = 4;
	public static final int GUITAR_PLAYER = 5;
	public static final int MEDIATION_PLAYER = 6;
	
	public static final int ACTION_PLAY_PAUSE = 100;
	public static final int ACTION_VOLUME = 101;
	public static final int ACTION_TIMER = 102;
	
	 /// views
	private Spinner timerSpinner;
	private TextView timeTextView;
	Button rainPlayBtn;
	Button naturalPlayBtn;
	Button wavesPlayBtn;
	Button windPlayBtn;
	Button guitarPlayBtn;
	Button meditationPlayBtn;
	
	SeekBar rainVolumeSeekBar;
	SeekBar naturalVolumeSeekBar;
	SeekBar wavesVolumeSeekBar;
	SeekBar windVolumeSeekBar;
	SeekBar guitarVolumeSeekBar;
	SeekBar mediationSeekBar;
	private InterstitialAd mInterstitialAd;
	
	
	
	
	//data
	private boolean isRainPlaying = false;
	private boolean isNaturalPlaying = false;
	private boolean isWindPlaying = false;
	private boolean isWavesPlaying = false;
	private boolean isGuitarPlaying = false;
	private boolean isMeditaionPlaying = false;
	

	private SharedPreferences mSettings;
	private String[] listTimeValues;
	private int mHour = -1;
	private int mMinute = -1;
	private boolean isNotFirstTimeEnterActivity;
	private boolean isNotFirstTimeUseApp;
	private static int displayedTime;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		mSettings = PreferenceManager.getDefaultSharedPreferences(this);
		isRainPlaying = mSettings.getBoolean(RAIN_PLAYING_KEY, false);
		isNaturalPlaying = mSettings.getBoolean(NATURAL_PLAYING_KEY, false);
		isWindPlaying = mSettings.getBoolean(WIND_PLAYING_KEY, false);
		isWavesPlaying = mSettings.getBoolean(WAVES_PLAYING_KEY, false);
		isGuitarPlaying = mSettings.getBoolean(GUITAR_PLAYING_KEY, false);
		isMeditaionPlaying = mSettings.getBoolean(MEDITATION_PLAYING_KEY, false);
		isNotFirstTimeUseApp = mSettings.getBoolean(NOT_FIRST_TIME_USE_APP, false);
		
		// find
		findViewById(R.id.meditation_item_name5).setOnClickListener(this);
		findViewById(R.id.rain_item_name).setOnClickListener(this);
		findViewById(R.id.bird_song_item_name2).setOnClickListener(this);
		findViewById(R.id.guitar_piano_item_name5).setOnClickListener(this);
		findViewById(R.id.wind_item_name3).setOnClickListener(this);
		findViewById(R.id.waves_item_name4).setOnClickListener(this);
		
		// handle time picker
		mHour = mSettings.getInt(HOUR_KEY, -1);
		mMinute = mSettings.getInt(MINUTE_KEY, -1);
		timeTextView = (TextView) findViewById(R.id.timer_textview);
		if (mHour == -1 && mMinute == -1){
			Calendar c = Calendar.getInstance();
			c.add(Calendar.MINUTE, 30);
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);
            timeTextView.setText(mHour + " : " + String.format("%02d", mMinute));
		}else {
			timeTextView.setText(mHour + " : " + String.format("%02d", mMinute));
		}
		timeTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// Process to get Current Time
				if (mHour == -1 && mMinute == -1){
		            final Calendar c = Calendar.getInstance();
		            mHour = c.get(Calendar.HOUR_OF_DAY);
		            mMinute = c.get(Calendar.MINUTE);
				}
				
				// Launch Time Picker Dialog
	            TimePickerDialog tpd = new TimePickerDialog(MainActivity.this,
	                    new TimePickerDialog.OnTimeSetListener() {
	 
	                        @Override
	                        public void onTimeSet(TimePicker view, int hourOfDay,
	                                int minute) {
	                            // Display Selected time in textbox
	                            timeTextView.setText(hourOfDay + " : " + String.format("%02d", minute));
	                            mHour = hourOfDay;
	                            mMinute = minute;
	                            
	                            
	                            Editor editor = mSettings.edit();
	            				editor.putInt(HOUR_KEY, mHour);
	            				editor.putInt(MINUTE_KEY, mMinute);
	            				editor.apply();
	            				Intent intent = new Intent(MainActivity.this, SoundPlayerService.class);
	            				intent.putExtra(SoundPlayerService.ACTION_TYPE, ACTION_TIMER);
	            				intent.putExtra(SoundPlayerService.STOP_POINT_PLAY, getTimeLong(mHour, mMinute));
	            				startService(intent);
	                        }
	                        
	                    }, mHour, mMinute, true);
	            tpd.show();
				
			}
		});
		
		/*listTimeValues = getResources().getStringArray(R.array.times_in_minutes);
		timerSpinner = (Spinner) findViewById(R.id.time_picker_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.times_string, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		timerSpinner.setAdapter(adapter);
		timerSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				Editor editor = mSettings.edit();
				editor.putInt(TIMER_SELECTED_ITEM, position);
				editor.apply();
				Intent intent = new Intent(MainActivity.this, SoundPlayerService.class);
				intent.putExtra(SoundPlayerService.DURATION_PLAY, listTimeValues[position]);
				startService(intent);
				// notify the service
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
				
			}
		});
		
		timerSpinner.setSelection(mSettings.getInt(TIMER_SELECTED_ITEM, 0));*/
		
		// rain
		rainPlayBtn = (Button) findViewById(R.id.rain_play_button);
		rainPlayBtn.setOnClickListener(this);
		if (isRainPlaying)
			rainPlayBtn.setBackgroundResource(R.drawable.pause_btn);
		else
			rainPlayBtn.setBackgroundResource(R.drawable.play_btn);
		rainVolumeSeekBar = (SeekBar) findViewById(R.id.rain_item_seek_bar);
		initControls(rainVolumeSeekBar);
		rainVolumeSeekBar.setProgress(mSettings.getInt(RAIN_VOLUME_KEY, 50));
		
		
		
		
		// natural
		naturalPlayBtn = (Button) findViewById(R.id.bird_song_play_button2);
		naturalPlayBtn.setOnClickListener(this);
		if (isNaturalPlaying)
			naturalPlayBtn.setBackgroundResource(R.drawable.pause_btn);
		else
			naturalPlayBtn.setBackgroundResource(R.drawable.play_btn);
		naturalVolumeSeekBar = (SeekBar)findViewById(R.id.bird_song_item_seek_bar2);
		initControls(naturalVolumeSeekBar);
		naturalVolumeSeekBar.setProgress(mSettings.getInt(NATUAL_VOLUME_KEY, 50));
		
		
		// waves
		wavesPlayBtn = (Button) findViewById(R.id.waves_play_button4);
		wavesPlayBtn.setOnClickListener(this);
		if (isWavesPlaying)
			wavesPlayBtn.setBackgroundResource(R.drawable.pause_btn);
		else
			wavesPlayBtn.setBackgroundResource(R.drawable.play_btn);
		wavesVolumeSeekBar = (SeekBar)findViewById(R.id.waves_item_seek_bar4);
		initControls(wavesVolumeSeekBar);
		wavesVolumeSeekBar.setProgress(mSettings.getInt(WAVES_VOLUME_KEY, 50));
		
		// wind
		windPlayBtn = (Button) findViewById(R.id.wind_play_button3);
		windPlayBtn.setOnClickListener(this);
		if (isWindPlaying)
			windPlayBtn.setBackgroundResource(R.drawable.pause_btn);
		else
			windPlayBtn.setBackgroundResource(R.drawable.play_btn);
		windVolumeSeekBar = (SeekBar)findViewById(R.id.wind_item_seek_bar3);
		initControls(windVolumeSeekBar);
		windVolumeSeekBar.setProgress(mSettings.getInt(WIND_VOLUME_KEY, 50));
		
		
		//guitar 
		guitarPlayBtn = (Button) findViewById(R.id.guitar_piano_play_button5);
		guitarPlayBtn.setOnClickListener(this);
		if (isGuitarPlaying)
			guitarPlayBtn.setBackgroundResource(R.drawable.pause_btn);
		else
			guitarPlayBtn.setBackgroundResource(R.drawable.play_btn);
		guitarVolumeSeekBar = (SeekBar)findViewById(R.id.guitar_piano_item_seek_bar5);
		initControls(guitarVolumeSeekBar);
		guitarVolumeSeekBar.setProgress(mSettings.getInt(GUITAR_VOLUME_KEY, 50));
		
		
		
		// meditation
		meditationPlayBtn = (Button) findViewById(R.id.meditation_play_button5);
		meditationPlayBtn.setOnClickListener(this);
		if (isMeditaionPlaying)
			meditationPlayBtn.setBackgroundResource(R.drawable.pause_btn);
		else
			meditationPlayBtn.setBackgroundResource(R.drawable.play_btn);
		mediationSeekBar = (SeekBar)findViewById(R.id.meditation_item_seek_bar5);
		initControls(mediationSeekBar);
		mediationSeekBar.setProgress(mSettings.getInt(MEDITATION_VOLUME_KEY, 50));
		
		// init ads
		//initAd();
		AdBuddiz.setPublisherKey("f6809dd6-167e-4569-aa89-43c8dbe67583");
		AdBuddiz.cacheAds(this); // this = current Activity
			if(isDebug)
				Log.v("MAINACT", "onCreate");
			
			//show instruction here
			if(!isNotFirstTimeUseApp){
				showLongerToastMessage(R.string.first_instruction);
				showLongerToastMessage(R.string.second_instruction);
				showLongerToastMessage(R.string.third_instruction);
				showLongerToastMessage(R.string.final_instruction);
			}
	}
	
	
	
	
	@Override
	protected void onResume() {
		
		//}
		
		mSettings.registerOnSharedPreferenceChangeListener(this);
		
		if (isNotFirstTimeEnterActivity && displayedTime++ % 10 == 1){ //TODO change to 0 later 
			AdBuddiz.showAd(this); 
			//displayedTime++;
			
			//initAd();
		}
		isNotFirstTimeEnterActivity = true;
		if(isDebug)
		Log.v("MAINACT", "onresume, displaytime: " + displayedTime);
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		isNotFirstTimeUseApp = true;
		mSettings.edit().putBoolean(NOT_FIRST_TIME_USE_APP, true).commit();
		mSettings.unregisterOnSharedPreferenceChangeListener(this);
		super.onPause();
		if(isDebug)
		Log.v("MAINACT", "on pause");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	

	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_exit) {
			displayAd();
			Intent intent = new Intent(MainActivity.this,
					SoundPlayerService.class);
			stopService(intent);
			finish();
			return true;
		} else if (id == R.id.action_share) {
			String message = APP_LINK;
			Intent share = new Intent(Intent.ACTION_SEND);
			share.setType("text/plain");
			share.putExtra(Intent.EXTRA_TEXT, message);

			startActivity(Intent.createChooser(share, "Share using: "));
			return true;
		}
		if (id == R.id.action_rate) {
			String appPackageName = getPackageName();
			try {
				startActivity(new Intent(Intent.ACTION_VIEW,
						Uri.parse("market://details?id=" + appPackageName)));
			} catch (Exception e) {
				
				startActivity(new Intent(
						Intent.ACTION_VIEW,
						Uri.parse("http://play.google.com/store/apps/details?id="
								+ appPackageName)));
			}
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public void onClick(View v) {
		
		Editor editor = mSettings.edit();
		switch (v.getId()) {
		case R.id.rain_item_name:
		case R.id.rain_play_button:
			//displayAd();
			
			// 
			if (isRainPlaying){
				
				isRainPlaying = false;
				
				// this click should be pausing
				rainPlayBtn.setBackgroundResource(R.drawable.play_btn);
				
			}
			else {
				isRainPlaying = true;
				rainPlayBtn.setBackgroundResource(R.drawable.pause_btn);
			}
			editor.putBoolean(RAIN_PLAYING_KEY, isRainPlaying);
			editor.apply();
			Intent intent = new Intent(MainActivity.this, SoundPlayerService.class);
			intent.putExtra(SoundPlayerService.PLAYER_ID, RAIN_PLAYER);
			intent.putExtra(SoundPlayerService.ACTION_TYPE, ACTION_PLAY_PAUSE);
			startService(intent);
			break;
		case R.id.bird_song_item_name2:
		case R.id.bird_song_play_button2:
			if (isNaturalPlaying){
				isNaturalPlaying = false;
				// this click should be pausing
				naturalPlayBtn.setBackgroundResource(R.drawable.play_btn);
			}
			else {
				isNaturalPlaying = true;
				naturalPlayBtn.setBackgroundResource(R.drawable.pause_btn);
			}
			editor.putBoolean(NATURAL_PLAYING_KEY, isNaturalPlaying);
			editor.apply();
			Intent intent2 = new Intent(MainActivity.this, SoundPlayerService.class);
			intent2.putExtra(SoundPlayerService.PLAYER_ID, NATURAL_PLAYER);
			intent2.putExtra(SoundPlayerService.ACTION_TYPE, ACTION_PLAY_PAUSE);
			startService(intent2);
			
			break;
			
		case R.id.wind_item_name3:
		case R.id.wind_play_button3:
			if (isWindPlaying){
				isWindPlaying = false;
				// this click should be pausing
				windPlayBtn.setBackgroundResource(R.drawable.play_btn);
			}
			else {
				isWindPlaying = true;
				windPlayBtn.setBackgroundResource(R.drawable.pause_btn);
			}
			editor.putBoolean(WIND_PLAYING_KEY, isWindPlaying);
			editor.apply();
			Intent intent3 = new Intent(MainActivity.this, SoundPlayerService.class);
			intent3.putExtra(SoundPlayerService.PLAYER_ID, WIND_PLAYER);
			intent3.putExtra(SoundPlayerService.ACTION_TYPE, ACTION_PLAY_PAUSE);
			startService(intent3);
			break;
			
		case R.id.waves_item_name4:
		case R.id.waves_play_button4:
			if (isWavesPlaying){
				isWavesPlaying = false;
				// this click should be pausing
				wavesPlayBtn.setBackgroundResource(R.drawable.play_btn);
			}
			else {
				isWavesPlaying = true;
				wavesPlayBtn.setBackgroundResource(R.drawable.pause_btn);
			}
			editor.putBoolean(WAVES_PLAYING_KEY, isWavesPlaying);
			editor.apply();
			Intent intent4 = new Intent(MainActivity.this, SoundPlayerService.class);
			intent4.putExtra(SoundPlayerService.PLAYER_ID, WAVES_PLAYER);
			intent4.putExtra(SoundPlayerService.ACTION_TYPE, ACTION_PLAY_PAUSE);
			startService(intent4);
			break;	
			
		case R.id.guitar_piano_item_name5:
		case R.id.guitar_piano_play_button5:
			if (isGuitarPlaying){
				isGuitarPlaying = false;
				// this click should be pausing
				guitarPlayBtn.setBackgroundResource(R.drawable.play_btn);
			}
			else {
				isGuitarPlaying = true;
				guitarPlayBtn.setBackgroundResource(R.drawable.pause_btn);
			}
			editor.putBoolean(GUITAR_PLAYING_KEY, isGuitarPlaying);
			editor.apply();
			Intent intent5 = new Intent(MainActivity.this, SoundPlayerService.class);
			intent5.putExtra(SoundPlayerService.PLAYER_ID, GUITAR_PLAYER);
			intent5.putExtra(SoundPlayerService.ACTION_TYPE, ACTION_PLAY_PAUSE);
			startService(intent5);
			break;		
			
		

		case R.id.meditation_item_name5:
		case R.id.meditation_play_button5:
			if (isMeditaionPlaying){
				isMeditaionPlaying = false;
				// this click should be pausing
				meditationPlayBtn.setBackgroundResource(R.drawable.play_btn);
			}
			else {
				isMeditaionPlaying = true;
				meditationPlayBtn.setBackgroundResource(R.drawable.pause_btn);
			}
			editor.putBoolean(MEDITATION_PLAYING_KEY, isMeditaionPlaying);
			editor.apply();
			Intent intent6 = new Intent(MainActivity.this, SoundPlayerService.class);
			intent6.putExtra(SoundPlayerService.PLAYER_ID, MEDIATION_PLAYER);
			intent6.putExtra(SoundPlayerService.ACTION_TYPE, ACTION_PLAY_PAUSE);
			startService(intent6);
			break;		

			
			
		default:
			break;
		}
		
	}
	
	
	
	private void initControls( final SeekBar volumeSeekbar)
    {
		
        try
        {
            volumeSeekbar.setMax(100);
            //volumeSeekbar.setProgress(audioManager
              //      .getStreamVolume(AudioManager.STREAM_MUSIC));   


            volumeSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() 
            {
                @Override
                public void onStopTrackingTouch(SeekBar arg0) 
                {
                }

                @Override
                public void onStartTrackingTouch(SeekBar arg0) 
                {
                }

                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) 
                {
                    /*audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            progress, 0);*/
                	Editor editor = mSettings.edit();
                	
                	int player = getPlayerBySeekBar(volumeSeekbar);
                	String key = getVoulumeKeyByPlayer(player);
                	editor.putInt(key, progress);
                	editor.apply();
                	
                	Intent intent2 = new Intent(MainActivity.this, SoundPlayerService.class);
        			intent2.putExtra(SoundPlayerService.PLAYER_ID, player);
        			intent2.putExtra(SoundPlayerService.ACTION_TYPE, ACTION_VOLUME);
        			intent2.putExtra(SoundPlayerService.VOULUME, progress);
        			startService(intent2);
                }
            });
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
	
	public int getPlayerBySeekBar(SeekBar bar){
		switch (bar.getId()) {
		case R.id.rain_item_seek_bar:
			return RAIN_PLAYER;
		case R.id.bird_song_item_seek_bar2:
			return NATURAL_PLAYER;
		case R.id.waves_item_seek_bar4:
			return WAVES_PLAYER;
		case R.id.wind_item_seek_bar3:
			return WIND_PLAYER;
		case R.id.guitar_piano_item_seek_bar5:
			return GUITAR_PLAYER;
		case R.id.meditation_item_seek_bar5:
			return MEDIATION_PLAYER;
		default:
			return 0;
		}
	}
	
	public String getVoulumeKeyByPlayer (int player){
		switch (player) {
		case RAIN_PLAYER:
			return RAIN_VOLUME_KEY;
		case NATURAL_PLAYER :
			return NATUAL_VOLUME_KEY;
		case GUITAR_PLAYER:
			return GUITAR_VOLUME_KEY;
		case WAVES_PLAYER:
			return WAVES_VOLUME_KEY;
		case WIND_PLAYER:
			return WIND_VOLUME_KEY;
		case MEDIATION_PLAYER:
			return MEDITATION_VOLUME_KEY;
		default:
			return "key";
		}
	}
	
	public String getPlayingKeyByPlayer (int player){
		switch (player) {
		case RAIN_PLAYER:
			return RAIN_PLAYING_KEY;
		case NATURAL_PLAYER :
			return NATURAL_PLAYING_KEY;
		case GUITAR_PLAYER:
			return GUITAR_PLAYING_KEY;
		case WAVES_PLAYER:
			return WAVES_PLAYING_KEY;
		case WIND_PLAYER:
			return WIND_PLAYING_KEY;
		case MEDIATION_PLAYER:
			return MEDITATION_PLAYING_KEY;
		default:
			return "key";
		}
	}
	
	public static long getTimeLong (int hourOfDay, int minute){
		 Calendar c = Calendar.getInstance();
         int currentHour = c.get(Calendar.HOUR_OF_DAY);
         int currentMinute = c.get(Calendar.MINUTE);
		
         if (hourOfDay > currentHour){//still same day
        	 c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        	 c.set(Calendar.MINUTE, minute);
        	 return c.getTimeInMillis();
         }
         else if (hourOfDay == currentHour){
        	 if (minute > currentMinute){ //still same day
            	 c.set(Calendar.MINUTE, minute);
            	 return c.getTimeInMillis();
        	 }
         }
         else{//set for tomorrow
        	 c.add(Calendar.DAY_OF_YEAR, 1);
        	 c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        	 c.set(Calendar.MINUTE, minute);
        	 return c.getTimeInMillis();
         }
         
		return 0;
	}





	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if(isDebug)
		Log.v("MAIN", "call onsharedPreference changed");
		isRainPlaying = mSettings.getBoolean(RAIN_PLAYING_KEY, false);
		isNaturalPlaying = mSettings.getBoolean(NATURAL_PLAYING_KEY, false);
		isWindPlaying = mSettings.getBoolean(WIND_PLAYING_KEY, false);
		isWavesPlaying = mSettings.getBoolean(WAVES_PLAYING_KEY, false);
		isGuitarPlaying = mSettings.getBoolean(GUITAR_PLAYING_KEY, false);
		isMeditaionPlaying = mSettings.getBoolean(MEDITATION_PLAYING_KEY, false);
		
		if (isRainPlaying)
			rainPlayBtn.setBackgroundResource(R.drawable.pause_btn);
		else
			rainPlayBtn.setBackgroundResource(R.drawable.play_btn);
		
		
		if (isNaturalPlaying)
			naturalPlayBtn.setBackgroundResource(R.drawable.pause_btn);
		else
			naturalPlayBtn.setBackgroundResource(R.drawable.play_btn);
		
		if (isWavesPlaying)
			wavesPlayBtn.setBackgroundResource(R.drawable.pause_btn);
		else
			wavesPlayBtn.setBackgroundResource(R.drawable.play_btn);
		
		if (isWindPlaying)
			windPlayBtn.setBackgroundResource(R.drawable.pause_btn);
		else
			windPlayBtn.setBackgroundResource(R.drawable.play_btn);
		
		if (isGuitarPlaying)
			guitarPlayBtn.setBackgroundResource(R.drawable.pause_btn);
		else
			guitarPlayBtn.setBackgroundResource(R.drawable.play_btn);
		
		if (isMeditaionPlaying)
			meditationPlayBtn.setBackgroundResource(R.drawable.pause_btn);
		else
			meditationPlayBtn.setBackgroundResource(R.drawable.play_btn);
		
		
		
		// handle time picker
		mHour = mSettings.getInt(HOUR_KEY, -1);
		mMinute = mSettings.getInt(MINUTE_KEY, -1);
		if (mHour == -1 && mMinute == -1){
			Calendar c = Calendar.getInstance();
			c.add(Calendar.MINUTE, 20);
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);
            timeTextView.setText(mHour + " : " + String.format("%02d", mMinute));
		}else {
			timeTextView.setText(mHour + " : " + String.format("%02d", mMinute));
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
//	11-18 11:04:41.482: I/Ads(27239): Use AdRequest.Builder.addTestDevice("A69477166D3F551D22E28A685E73D963") to get test ads on this device.

	 /**
     * This class makes the ad request and loads the ad.
     */
    public static class AdFragment extends Fragment {

        private AdView mAdView;

        public AdFragment() {
        }

        @Override
        public void onActivityCreated(Bundle bundle) {
            super.onActivityCreated(bundle);

            // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
            // values/strings.xml.
            mAdView = (AdView) getView().findViewById(R.id.adView);

            // Create an ad request. Check logcat output for the hashed device ID to
            // get test ads on a physical device. e.g.
            // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
            AdRequest adRequest = new AdRequest.Builder()
                    //.addTestDevice("A69477166D3F551D22E28A685E73D963")
                    .build();

            // Start loading the ad in the background.
            mAdView.loadAd(adRequest);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_ads, container, false);
        }

        /** Called when leaving the activity */
        @Override
        public void onPause() {
            if (mAdView != null) {
                mAdView.pause();
            }
            super.onPause();
        }

        /** Called when returning to the activity */
        @Override
        public void onResume() {
            super.onResume();
            if (mAdView != null) {
                mAdView.resume();
            }
        }

        /** Called before the activity is destroyed */
        @Override
        public void onDestroy() {
            if (mAdView != null) {
                mAdView.destroy();
            }
            super.onDestroy();
        }

    }
	
    
    
    
    //========================= intersec
    
    private void initAd() {
        // Create the InterstitialAd and set the adUnitId.
        mInterstitialAd = new InterstitialAd(MainActivity.this);
        // Defined in values/strings.xml
        mInterstitialAd.setAdUnitId(getString(R.string.full_screen_id));
        
        // Create ad request.
        AdRequest adRequest = new AdRequest.Builder()
        //.addTestDevice("A69477166D3F551D22E28A685E73D963")
        .build();
//        11-18 11:29:41.993: I/Ads(5130): Use AdRequest.Builder.addTestDevice("A69477166D3F551D22E28A685E73D963") to get test ads on this device.
        
        // Begin loading your interstitial.
        mInterstitialAd.loadAd(adRequest);
        if(isDebug)
        Log.v("MainAc", "finishing init ads");
    }
    
    private void displayAd() {
        // Show the ad if it's ready. Otherwise toast and restart the game. 
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
        	if(isDebug)
            Log.v("Mainactivity", "ads has not loadded yet");
        }
    }
    
    private void showLongerToastMessage (int messageId){
    	final Toast tag = Toast.makeText(MainActivity.this, messageId ,Toast.LENGTH_LONG);

    	tag.show();

    	new CountDownTimer(5000, 1000)
    	{

    	    public void onTick(long millisUntilFinished) {tag.show();}
    	    public void onFinish() {tag.show();}

    	}.start();
    }
}
