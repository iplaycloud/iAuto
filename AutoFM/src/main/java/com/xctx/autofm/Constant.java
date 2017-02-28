package com.xctx.autofm;

public interface Constant {
	/** Debug：打印Log */
	public static final boolean isDebug = true;

	/** 日志Tag */
	public static final String TAG = "AZ";

	/** FM发射 */
	public static final class FMTransmit {
		/** 系统设置：FM发射开关 */
		public static final String SETTING_ENABLE = "fm_transmitter_enable";

		/** 系统设置：FM发射频率 */
		public static final String SETTING_CHANNEL = "fm_transmitter_channel";

		public static final int DEFAULT_FREQUENCY = 9600;

	}

	/** 广播 */
	public static final class Broadcast {
		/** 导航栏打开FM发射 */
		public static final String NAVIBAR_OPEN_FM = "com.tchip.FM_OPEN_SYSTEMUI";

		/** 导航栏关闭FM发射 */
		public static final String NAVIBAR_CLOSE_FM = "com.tchip.FM_CLOSE_SYSTEMUI";

		/** 语音设置FM频率 putExtra("freq", freq) */
		public static final String VOICE_SET_FM = "com.tchip.FM_SET_FREQ";

		/** FM发射开启,通知侧边栏更新 */
		public static final String FM_ON = "tchip.intent.action.FM_ON";

		/** FM发射关闭,通知侧边栏更新 */
		public static final String FM_OFF = "tchip.intent.action.FM_OFF";
	}

	/** 路径 */
	public static final class Path {

		/** 字体目录 */
		public static final String FONT = "fonts/";

		/**
		 * 音频通道：0-系统 1-蓝牙
		 */
		public static final String NODE_SWITCH_AUDIO = "/sys/bus/i2c/devices/0-007f/Spk_Choose_Num";

		/** FM开关:0-下电 1-上电 */
		public static final String NODE_FM_ENABLE = "/sys/bus/i2c/devices/2-002c/enable_qn8027";

		/** FM频率 */
		public static final String NODE_FM_FREQUENCY = "/sys/bus/i2c/devices/2-002c/setch_qn8027";
	}

	/** SharedPreferences名称 */
	public static final String SHARED_PREFERENCES_NAME = "AutoFM";

}
