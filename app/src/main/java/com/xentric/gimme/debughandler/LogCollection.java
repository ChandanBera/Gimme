package com.xentric.gimme.debughandler;

/**
 * Developer: Hari Narayan Jha
 * Date: 04.02.2015
 * Copyright by RTS
 */

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * <p>Class: LogCollection</p>
 * <p>Purpose: To implement different logs type directly</p>
 * 
 * <p><strong>Direction:</strong></p>
 * <p><code>private LogCollection logcLogCollection;</code></p>
 * <p><code>logcLogCollection = new LogCollection(MyActivity.this);</code></p>
 * <p><code>logcLogCollection.setLogVBlack("Your Message");</code></p>
 * <p><strong>Supporting: </strong>No supporting files need to implement.</p>
 * <p><strong>boolean enableLog: </strong>Change enableLog status false manually to disable all log related message from your application.</p>
 */
public class LogCollection {

	private Context context;
	private String tag = "";
	private boolean enableLog = true;
	
	public LogCollection(Context context){
		this.context = context;
		this.tag = context.getClass().getName().toString();
	}
	
	/**Calling Log.d()*/
	public void setLogDBlue(String logValue) {
		if(enableLog)
			Log.d(tag, logValue);
	}
	
	/**Calling Log.d() with tag*/
	public void setLogDBlue(String tag, String logValue) {
		if(enableLog)
			Log.d(tag, logValue);
	}
	
	/**Calling Log.e()*/
	public void setLogERed(String logValue) {
		if(enableLog)
			Log.e(tag, logValue);
	}
	
	/**Calling Log.e() with tag*/
	public void setLogERed(String tag, String logValue) {
		if(enableLog)
			Log.e(tag, logValue);
	}
	
	/**Calling Log.v()*/
	public void setLogVBlack(String logValue) {
		if(enableLog)
			Log.v(tag, logValue);
	}
	
	/**Calling Log.v() with tag*/
	public void setLogVBlack(String tag, String logValue) {
		if(enableLog)
			Log.v(tag, logValue);
	}
	
	/**Calling Log.i()*/
	public void setLogIGreen(String logValue) {
		if(enableLog)
			Log.i(tag, logValue);
	}
	
	/**Calling Log.i() with tag*/
	public void setLogIGreen(String tag, String logValue) {
		if(enableLog)
			Log.i(tag, logValue);
	}
	
	/**Calling Log.w()*/
	public void setLogWOrange(String logValue) {
		if(enableLog)
			Log.w(tag, logValue);
	}
	
	/**Calling Log.w() with tag*/
	public void setLogWOrange(String tag, String logValue) {
		if(enableLog)
			Log.w(tag, logValue);
	}
	
	/**Calling Log.wtf()*/
	public void setLogWtfRed(String logValue) {
		if(enableLog)
			Log.wtf(tag, logValue);
	}
	
	/**Calling Log.wtf() with tag*/
	public void setLogWtfRed(String tag, String logValue) {
		if(enableLog)
			Log.wtf(tag, logValue);
	}
	
	/**Calling stack trace for Exception class*/
	public void setStackTrace(Exception e){
		if(enableLog)
			e.printStackTrace();
	}
	
	/**Showing Toast Message for LENGTH_LONG*/
	public void showToastLong(String message){
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}
	
	/**Showing Toast Message for LENGTH_SHORT*/
	public void showToastShort(String message){
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}
}
