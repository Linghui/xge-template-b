package com.gol;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.JsonValue;
import com.gol.screens.logo.LogoScreen;
import com.gol.screens.main.MainScreen;
import com.gol.xge.assetsLoader.JsonLoader;
import com.gol.xge.resolver.InAndExternalFileHandleResolver;

public class GameStarter extends Game {

	private static String TAG = "GameStart";

	private static GameStarter self = null;

	private AssetManager manager = null;

	private MainScreen mainScreen = null;

	public static boolean debug;

	public static int width = 800;
	public static int height = 480;
	public static float bi = 1f;

	private Array<Disposable> disposeList = new Array<Disposable>();

	private GameStarter() {
	}

	public static GameStarter getInstance() {
		// System.out.println("GameStart getInstance");
		if (self == null) {
			self = new GameStarter();
		}

		return self;
	}

	@Override
	public void create() {

		GameStarter.debug = true;

		Gdx.app.log(TAG, "***********************************************");
		Gdx.app.log(TAG, "***********************************************");
		if (debug) {
			Gdx.app.log(TAG, "************* 测试模式 **************************");
		} else {
			Gdx.app.log(TAG, "************* 使劲玩儿吧 ************************");
		}
		Gdx.input.setCatchBackKey(true);

		Gdx.app.log(TAG, "***********************************************");
		Gdx.app.log(TAG, "***********************************************");

		InAndExternalFileHandleResolver resolver = new InAndExternalFileHandleResolver();
		manager = new AssetManager(resolver);
		manager.setLoader(JsonValue.class, new JsonLoader(resolver));

		Gdx.app.debug(TAG, "start setting logoScreen");
		this.setScreen(new LogoScreen(this, manager));

	}

	@Override
	public void render() {
		super.render();
	}

	public MainScreen getMainScreen() {
		if (mainScreen == null) {
			mainScreen = new MainScreen(this, manager);
		}
		return mainScreen;
	}

	public AssetManager getManager() {
		return manager;
	}

	public void addDisposableEle(Disposable dis) {
		disposeList.add(dis);
	}

	@Override
	public void dispose() {
		super.dispose();
		Gdx.app.log(TAG, "game disposed");
		manager.clear();
		manager.dispose();

		for (Disposable dis : this.disposeList) {
			if (dis != null) {
				dis.dispose();
			}
		}

		self = null;
	}

}
