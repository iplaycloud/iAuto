package com.xctx.autofm.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import com.xctx.autofm.Constant;
import com.xctx.autofm.util.ProviderUtil.Name;

import android.content.Context;
import android.content.Intent;

public class SettingUtil {

	/**
	 * FM发射开关节点
	 * 
	 * 1：开 0：关
	 */
	public static File nodeFmEnable = new File(Constant.Path.NODE_FM_ENABLE);

	/**
	 * FM发射频率节点
	 * 
	 * 频率范围：支持7600~10800:使用8750-10800
	 */
	public static File nodeFmChannel = new File(Constant.Path.NODE_FM_FREQUENCY);

	/**
	 * FM发射是否打开:Config
	 * 
	 * @return
	 * @deprecated Using {@link #isFmTransmitPowerOn()} instead.
	 */
	public static boolean isFmTransmitConfigOn(Context context) {
		String strFmEnable = ProviderUtil.getValue(context,
				Name.FM_TRANSMIT_STATE, "0");
		if ("1".equals(strFmEnable)) {
			return true;
		} else {
			return false;
		}
	}

	public static void setFmTransmitConfigOn(Context context, boolean isOn) {
		ProviderUtil
				.setValue(context, Name.FM_TRANSMIT_STATE, isOn ? "1" : "0");
	}

	/**
	 * FM发射是否打开:Power
	 * 
	 * @return
	 */
	public static boolean isFmTransmitOnNode() {
		return getFileInt(nodeFmEnable) == 1;
	}

	public static void setFmTransmitPowerOn(Context context, boolean isOn) {
		SettingUtil
				.SaveFileToNode(SettingUtil.nodeFmEnable, (isOn ? "1" : "0"));
		MyLog.v("[SettingUtil]setFmTransmitPowerOn:" + isOn);
		context.sendBroadcast(new Intent(isOn ? Constant.Broadcast.FM_ON
				: Constant.Broadcast.FM_OFF));
	}

	public static int getFileInt(File file) {
		if (file.exists()) {
			try {
				InputStream inputStream = new FileInputStream(file);
				InputStreamReader inputStreamReader = new InputStreamReader(
						inputStream);
				int ch = 0;
				if ((ch = inputStreamReader.read()) != -1) {
					inputStreamReader.close();
					inputStream.close();
					return Integer.parseInt(String.valueOf((char) ch));
				} else {
					inputStreamReader.close();
					inputStream.close();
				}
			} catch (FileNotFoundException e) {
				MyLog.e("[AutoFM.SettintUtil.getFileInt] FileNotFoundException:"
						+ e.toString());
				e.printStackTrace();
			} catch (IOException e) {
				MyLog.e("[AutoFM.SettintUtil.getFileInt] IOException:"
						+ e.toString());
				e.printStackTrace();
			}
		}
		return 0;
	}

	/**
	 * 获取设置中存取的频率
	 * 
	 * @return 8750-10800
	 */
	public static int getFmFrequcenyNode(Context context) {
		int fmFreqency = Constant.FMTransmit.DEFAULT_FREQUENCY;
		String strNodeFmChannel = "";
		if (nodeFmChannel.exists()) {
			InputStreamReader read = null;
			BufferedReader bufferedReader = null;
			try {
				read = new InputStreamReader(
						new FileInputStream(nodeFmChannel), "utf-8");
				bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					strNodeFmChannel += lineTxt.toString();
				}
				read.close();
				bufferedReader.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				MyLog.e("[SettingUtil]getLCDValue: FileNotFoundException");
			} catch (IOException e) {
				e.printStackTrace();
				MyLog.e("[SettingUtil]getLCDValue: IOException");
			} finally {
				try {
					read.close();
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		ProviderUtil.setValue(context, Name.FM_TRANSMIT_FREQ, strNodeFmChannel);
		fmFreqency = Integer.parseInt(strNodeFmChannel);

		MyLog.v("[SettingUtil]getFmFrequcenyNode,fmFreqency:" + fmFreqency);
		return fmFreqency;
	}

	/**
	 * 设置FM发射频率:8750-10800
	 * 
	 * @param frequency
	 */
	public static void setFmFrequencyNode(Context context, int frequency) {
		if (frequency >= 8750 && frequency <= 10800) {
			SaveFileToNode(nodeFmChannel, String.valueOf(frequency));
			MyLog.v("[SettingUtil]setFmFrequencyNode success:" + frequency
					/ 100.0f + "MHz");
		}
	}

	/**
	 * @return 8750-10800
	 * 
	 * @deprecated
	 */
	public static int getFmFrequencyConfig(Context context) {
		String strFrequency = ProviderUtil.getValue(context,
				Name.FM_TRANSMIT_FREQ, ""
						+ Constant.FMTransmit.DEFAULT_FREQUENCY);
		return Integer.parseInt(strFrequency);
	}

	/**
	 * @param context
	 * @param frequency
	 */
	public static void setFmFrequencyConfig(Context context, int frequency) {
		if (frequency >= 8750 && frequency <= 10800) {
			ProviderUtil.setValue(context, Name.FM_TRANSMIT_FREQ, ""
					+ frequency);
			MyLog.v("[SettingUtil]setFmFrequencyConfig success:" + frequency
					/ 100.0f + "MHz");
		}
	}

	public static void SaveFileToNode(File file, String value) {
		if (file.exists()) {
			try {
				StringBuffer strbuf = new StringBuffer("");
				strbuf.append(value);
				OutputStream output = null;
				OutputStreamWriter outputWrite = null;
				PrintWriter print = null;
				try {
					output = new FileOutputStream(file);
					outputWrite = new OutputStreamWriter(output);
					print = new PrintWriter(outputWrite);
					print.print(strbuf.toString());
					print.flush();
					output.close();
					outputWrite.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					MyLog.e("SaveFileToNode:output error");
				} finally {
					output.close();
					outputWrite.close();
				}
			} catch (IOException e) {
				MyLog.e("SaveFileToNode:IO Exception");
			}
		} else {
			MyLog.e("SaveFileToNode:File:" + file + "not exists");
		}
	}

}
