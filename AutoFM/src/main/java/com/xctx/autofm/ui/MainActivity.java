package com.xctx.autofm.ui;

import com.xctx.autofm.Constant;
import com.xctx.autofm.R;
import com.xctx.autofm.util.ClickUtil;
import com.xctx.autofm.util.MyLog;
import com.xctx.autofm.util.ProviderUtil;
import com.xctx.autofm.util.ProviderUtil.Name;
import com.xctx.autofm.util.SettingUtil;
import com.xctx.autofm.util.TypefaceUtil;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final String TAG = "AutoFM";
	private Context context;

	/** UI主线程Handler */
	private Handler mainHandler;
	/** 非UI任务线程 */
	private static final HandlerThread taskHandlerThread = new HandlerThread(
			"fm-task-thread");
	static {
		taskHandlerThread.start();
	}
	private final Handler taskHandler = new TaskHandler(
			taskHandlerThread.getLooper());

	private TextView textHint;
	private SeekBar fmSeekBar;

	private ImageButton fmFreqDecrease, fmFreqIncrease;
	private ImageButton imagePower;

	/** 频率字体 */
	private Typeface typefaceFrequency;
	private int colorFrequency;
	/** 关闭字体 */
	private Typeface typefaceClose;
	private int colorClose;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);

		context = getApplicationContext();

		typefaceFrequency = TypefaceUtil.get(this, Constant.Path.FONT
				+ "Font-Helvetica-Neue-LT-Pro-Bd.otf");
		typefaceClose = TypefaceUtil.get(this, Constant.Path.FONT
				+ "Font-Helvetica-Neue-LT-Pro.otf");
		colorFrequency = Color.parseColor("#ffffff");
		colorClose = Color.parseColor("#b20808");

		Log.i(TAG, "1");

		mainHandler = new Handler(this.getMainLooper());
		initialLayout();

		Log.i(TAG, "2");

		fmReceiver = new FMReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.Broadcast.NAVIBAR_OPEN_FM);
		filter.addAction(Constant.Broadcast.NAVIBAR_CLOSE_FM);
		filter.addAction(Constant.Broadcast.VOICE_SET_FM);
		registerReceiver(fmReceiver, filter);

		Log.i(TAG, "3");

		getContentResolver()
				.registerContentObserver(
						Uri.parse("content://com.tchip.provider.AutoProvider/state/name/"),
						true, new AutoContentObserver(new Handler()));

		Log.i(TAG, "4");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (fmReceiver != null) {
			unregisterReceiver(fmReceiver);
		}
	}

	/** ContentProvder监听 */
	public class AutoContentObserver extends ContentObserver {

		public AutoContentObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange, Uri uri) {
			String name = uri.getLastPathSegment(); // getPathSegments().get(2);
			if (name.equals("state")) { // insert

			} else { // update
				MyLog.v("[AutoFM.ContentObserver]onChange,selfChange:"
						+ selfChange + ",Name:" + name);
				if (Name.FM_TRANSMIT_STATE.equals(name)) {
					if ("1".equals(ProviderUtil.getValue(context,
							Name.FM_TRANSMIT_STATE, "0"))) {
						int nowFreq = SettingUtil.getFmFrequcenyNode(context);
						String content = "" + nowFreq / 100.0f + "MHz";
						SpannableString spannableString = new SpannableString(
								content);
						spannableString.setSpan(new RelativeSizeSpan(0.4f),
								content.length() - 3, content.length(),
								Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						textHint.setTypeface(typefaceFrequency);
						textHint.setTextColor(colorFrequency);
						textHint.setText(spannableString);
					} else {
						String hintDisable = getResources().getString(
								R.string.fmt_disable);
						SpannableString spannableString = new SpannableString(
								hintDisable);
						spannableString.setSpan(new RelativeSizeSpan(0.5f), 0,
								hintDisable.length(),
								Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						textHint.setTypeface(typefaceClose);
						textHint.setTextColor(colorClose);
						textHint.setText(spannableString);
					}
				}
			}
			super.onChange(selfChange, uri);
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
		}

	}

	class TaskHandler extends Handler {

		public int nowFrequency = Constant.FMTransmit.DEFAULT_FREQUENCY;

		public TaskHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1: // 点击FM开关按钮
				this.removeMessages(1);
				if (SettingUtil.isFmTransmitConfigOn(context)) {
					SettingUtil.setFmTransmitPowerOn(context, false);
					SettingUtil.setFmTransmitConfigOn(context, false);
				} else {
					SettingUtil.setFmTransmitPowerOn(context, true);
					SettingUtil.setFmTransmitConfigOn(context, true);
				}
				nowFrequency = SettingUtil.getFmFrequcenyNode(context);
				mainHandler.post(new Runnable() {
					@Override
					public void run() {
						setFmText("" + nowFrequency / 100.0f + "MHz");
					}
				});
				this.removeMessages(1);
				break;

			case 2: { // 降低频率
				this.removeMessages(2);
				this.removeMessages(3);
				nowFrequency = SettingUtil.getFmFrequencyConfig(context) - 10; // 当前频率
				if (nowFrequency >= 8750 && nowFrequency <= 10800) {
					SettingUtil.setFmFrequencyNode(context, nowFrequency);
					SettingUtil.setFmFrequencyConfig(context, nowFrequency);
					mainHandler.post(new Runnable() {
						@Override
						public void run() {
							fmSeekBar.setProgress(nowFrequency / 10 - 875);
						}
					});
				}
				this.removeMessages(2);
			}
				break;

			case 3: { // 增加频率
				this.removeMessages(3);
				this.removeMessages(2);
				nowFrequency = SettingUtil.getFmFrequencyConfig(context) + 10; // 当前频率
				if (nowFrequency >= 8750 && nowFrequency <= 10800) {
					SettingUtil.setFmFrequencyNode(context, nowFrequency);
					SettingUtil.setFmFrequencyConfig(context, nowFrequency);
					mainHandler.post(new Runnable() {
						@Override
						public void run() {
							fmSeekBar.setProgress(nowFrequency / 10 - 875);
						}
					});
				}
				this.removeMessages(3);
			}
				break;

			default:
				break;

			}
		}
	}

	/** 接受语音发送的消息广播 **/
	private FMReceiver fmReceiver;

	public class FMReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Constant.Broadcast.NAVIBAR_OPEN_FM)
					|| action.equals(Constant.Broadcast.NAVIBAR_CLOSE_FM)) {
				int nowFreq = SettingUtil.getFmFrequcenyNode(context);
				setFmText("" + nowFreq / 100.0f + "MHz");
			} else if (action.equals(Constant.Broadcast.VOICE_SET_FM)) {
				Bundle extras = intent.getExtras();
				if (extras != null) {
					int freq = Integer.parseInt(extras.getString("freq"));
					MyLog.v("[FMReceiver]VOICE_SET_FM:" + freq);
					fmSeekBar.setProgress(freq / 10 - 875);
				}
			}
		}
	}

	private void setFmText(String content) {
		if (SettingUtil.isFmTransmitConfigOn(context)) {
			SpannableString spannableString = new SpannableString(content);
			spannableString.setSpan(new RelativeSizeSpan(0.4f),
					content.length() - 3, content.length(),
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			textHint.setTypeface(typefaceFrequency);
			textHint.setTextColor(colorFrequency);
			textHint.setText(spannableString);
		} else {
			String hintDisable = getResources().getString(R.string.fmt_disable);
			SpannableString spannableString = new SpannableString(hintDisable);
			spannableString.setSpan(new RelativeSizeSpan(0.5f), 0,
					hintDisable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			textHint.setTypeface(typefaceClose);
			textHint.setTextColor(colorClose);
			textHint.setText(spannableString);
		}
	}

	private void initialLayout() {
		MyOnClickListener myOnClickListener = new MyOnClickListener();
		textHint = (TextView) findViewById(R.id.textHint);
		fmSeekBar = (SeekBar) findViewById(R.id.fmSeekBar);
		// 875-1080
		// 0- 205
		fmSeekBar.setMax(205);
		int nowFrequency = 1000;//SettingUtil.getFmFrequcenyNode(context); // 当前频率
		fmSeekBar.setProgress(nowFrequency / 10 - 875);
		setFmText("" + nowFrequency / 100.0f + "MHz");
		fmSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				fmSeekBar.setEnabled(false);
				new Thread(new EnableSeekBarThread()).start();
				SettingUtil.setFmFrequencyNode(context,
						(seekBar.getProgress() + 875) * 10);
				SettingUtil.setFmFrequencyConfig(context,
						(seekBar.getProgress() + 875) * 10);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				float frequency = (progress + 875.0f) / 10;
				setFmText("" + frequency + "MHz");
			}
		});

		// fm频率0.1增加减少
		fmFreqDecrease = (ImageButton) findViewById(R.id.fmFreqDecrease);
		fmFreqDecrease.setOnClickListener(myOnClickListener);
		fmFreqIncrease = (ImageButton) findViewById(R.id.fmFreqIncrease);
		fmFreqIncrease.setOnClickListener(myOnClickListener);

		imagePower = (ImageButton) findViewById(R.id.imagePower);
		imagePower.setOnClickListener(myOnClickListener);
	}

	class EnableSeekBarThread implements Runnable {

		@Override
		public void run() {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Message msgEnableSeekBar = new Message();
			msgEnableSeekBar.what = 1;
			enableSeekBarHandler.sendMessage(msgEnableSeekBar);
		}

	}

	private Handler enableSeekBarHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				this.removeMessages(1);
				fmSeekBar.setEnabled(true);
				break;

			default:
				break;
			}
		}
	};

	private class MyOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.imagePower:
				if (!ClickUtil.isQuickClick(500)) {
					Message msgClickPower = new Message();
					msgClickPower.what = 1;
					taskHandler.sendMessage(msgClickPower);
				}
				break;

			case R.id.fmFreqDecrease:
				if (!ClickUtil.isQuickClick(300)) {
					Message msgFreqDec = new Message();
					msgFreqDec.what = 2;
					taskHandler.sendMessage(msgFreqDec);
				}
				break;

			case R.id.fmFreqIncrease:
				if (!ClickUtil.isQuickClick(300)) {
					Message msgFreqInc = new Message();
					msgFreqInc.what = 3;
					taskHandler.sendMessage(msgFreqInc);
				}
				break;

			default:
				break;
			}

		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			backToVice();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	private void backToVice() {
		finish();
		overridePendingTransition(R.anim.zms_translate_down_out,
				R.anim.zms_translate_down_in);
	}
}
