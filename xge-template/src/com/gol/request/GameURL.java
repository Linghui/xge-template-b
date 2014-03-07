package com.gol.request;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;

public class GameURL {

	public static final String httpHead = "http://";

	// local sever
	public static final String DESKTOP_HOST = "localhost";
	public static final int DESKTOP_PORT = 8888;
	public static final String DESKTOP_REQUEST_HEAD = httpHead + DESKTOP_HOST
			+ ":" + DESKTOP_PORT + "/ninjia_server/index.php/";

	// internet server
	public static final String ANDROID_HOST = "www.jian-yin.com";
	public static final int ANDROID_PORT = 8080;
	public static final String ANDROID_REQUEST_HEAD = httpHead + ANDROID_HOST
			+ ":" + ANDROID_PORT + "/index.php/";

	public static String getRequestHead() {
//		if (Gdx.app.getType() == ApplicationType.Desktop) {
		if (false) {
			return DESKTOP_REQUEST_HEAD;
		} else {
			return ANDROID_REQUEST_HEAD;
		}
	}
}
