package com.xctx.autoui.util;

import com.xctx.autoui.R;

public class WeatherUtil {

	public static enum WEATHER_TYPE {
		CLOUD, SUN, RAIN, SNOW, FOG, RAIN_SNOW, HAIL
	}

	/**
	 * 根据天气字段获取天气类型
	 * 
	 * @param weather
	 * @return
	 */
	public static WEATHER_TYPE getTypeByStr(String weather) {
		if (weather.contains("雪")) {
			return WEATHER_TYPE.SNOW;
		} else if (weather.contains("冻雨") || weather.contains("冰雹")) {
			return WEATHER_TYPE.HAIL;
		} else if (weather.contains("雨")) {
			return WEATHER_TYPE.RAIN;
		} else if (weather.contains("雾") || weather.contains("霾")
				|| weather.contains("浮尘") || weather.contains("沙尘")
				|| weather.contains("扬沙")) {
			return WEATHER_TYPE.FOG;
		} else if (weather.contains("阴") || weather.contains("多云")) {
			return WEATHER_TYPE.CLOUD;
		} else {
			return WEATHER_TYPE.SUN;
		}
	}

	/**
	 * 天气大图标
	 * 
	 * @param type
	 * @return
	 */
	public static int getWeatherDrawable(WEATHER_TYPE type) {

		switch (type) {
		case SUN:
			return R.drawable.weather_sun_tq_6;

		case CLOUD:
			return R.drawable.weather_cloud_tq_6;

		case RAIN:
			return R.drawable.weather_rain_tq_6;

		case SNOW:
			return R.drawable.weather_snow_tq_6;

		case HAIL:
			return R.drawable.weather_hail_tq_6;

		case RAIN_SNOW:
			return R.drawable.weather_rain_snow_tq_6;

		case FOG:
			return R.drawable.weather_fog_tq_6;

		default:
			return R.drawable.weather_sun_tq_6;
		}
	}
}
