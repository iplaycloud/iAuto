package com.xctx.autofm;

import com.xctx.autofm.util.MyUncaughtExceptionHandler;

import android.app.Application;

public class MyApp extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		MyUncaughtExceptionHandler myUncaughtExceptionHandler = MyUncaughtExceptionHandler
				.getInstance();
		myUncaughtExceptionHandler.init(getApplicationContext());

	}

}
