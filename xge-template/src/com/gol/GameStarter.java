package com.gol;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.JsonValue;
import com.gol.request.GameURL;
import com.gol.request.Protocol;
import com.gol.request.ProtocolListener;
import com.gol.screens.logo.LogoScreen;
import com.gol.screens.main.MainScreen;
import com.gol.xge.assetsLoader.JsonLoader;
import com.gol.xge.resolver.InAndExternalFileHandleResolver;
import com.gol.xge.rpg.scense.CoreScreen;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GameStarter extends Game implements ProtocolListener {

	private static String TAG = "GameStart";

	private static GameStarter self = null;

	private AssetManager manager = null;

	private MainScreen mainScreen = null;

	public static boolean debug;

	public static int width = 800;
	public static int height = 480;

	private int waitingType = 0;
	private float waitingTimer = 0;
	private boolean isLock;
	private final static float DEFAULT_TIME_OUT = 10f;
	private static float waitingTimeOut = DEFAULT_TIME_OUT;

	Vector<Protocol> respQueue = new Vector<Protocol>();

	Gson gson = new Gson();

	private Array<Disposable> disposeList = new Array<Disposable>();

	private Skin skin;

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

	private void initSkin() {
		if (skin == null) {
			skin = manager.get(CommonRes.uiSkin, Skin.class);
		}
	}

	@Override
	public void render() {
		super.render();

		if (waitingType != 0) {
			waitingTimer += Gdx.graphics.getDeltaTime();

			if (this.isLock == false) {
				lockAndLoading();
			}

			if (waitingTimer >= waitingTimeOut) {
				this.unlockAndLoadDone();
			}
		}
		if (!respQueue.isEmpty()) {
			Protocol obj = respQueue.get(0);
			if (obj.getP() == this.waitingType) {
				unlockAndLoadDone();
			}

			if (obj != null && this.getScreen() instanceof ProtocolListener) {

				// make sure there is someone is going to process the throw it.
				respQueue.remove(0);
				Gdx.app.log(TAG, "response P " + obj.getP());
				Gdx.app.log(TAG, "response T " + obj.getT());
				if (!((ProtocolListener) this.getScreen()).process(obj)) {
					this.process(obj);
				}
			}

		}

	}

	@Override
	public boolean process(Protocol p) {

		switch (p.getP()) {
		case 1:

			break;
		default:
			return false;
		}
		return true;
	}

	private Label loadingLabel = null;

	private void lockAndLoading() {
		Gdx.app.log(TAG, "lockAndLoading");
		this.isLock = true;
		// in debug mode, never lock and load
		CoreScreen screen = (CoreScreen) this.getScreen();

		if (!screen.isEventLock()) {
			screen.disableEvent();
		}

		// TODO: write your own fancy loading here.
		if (loadingLabel == null) {
			initSkin();
			loadingLabel = new Label("表着急，加载中呢。。。", skin);
			loadingLabel.setX((width - loadingLabel.getWidth()) / 2);
			loadingLabel.setY((height - loadingLabel.getHeight()) / 2);
			screen.addActorTop(loadingLabel);

		}

		loadingLabel.setColor(1, 1, 1, 0);

		float delay = 1f;

		// rolling the image and remember do something if it times out.
		final Action sqaction = Actions
				.sequence(
						Actions.delay(delay),
						Actions.parallel(Actions.fadeIn(0.0f), Actions
								.rotateTo(180 * waitingTimeOut, waitingTimeOut)),
						new Action() {
							@Override
							public boolean act(float delta) {
								return true;
							}

						});
		loadingLabel.addAction(sqaction);
		loadingLabel.addAction(Actions.sequence(Actions.delay(delay), Actions
				.parallel(
						Actions.scaleTo(1, 1, 0.3f, Interpolation.elasticOut),
						Actions.fadeIn(0.0f))));

	}

	public void unlockAndLoadDone() {
		Gdx.app.log(TAG, "unlockAndLoadDone");
		this.isLock = false;
		this.waitingType = 0;
		waitingTimer = 0f;
		CoreScreen screen = (CoreScreen) this.getScreen();
		// if( screen.isEventLock() ){
		screen.enableEvent();
		// }
		if (loadingLabel != null) {
			loadingLabel.remove();
			loadingLabel = null;
		}
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

	public void sendRequest(String url, HashMap<String, String> para) {
		StringBuffer urlWithPara = new StringBuffer(GameURL.getRequestHead()
				+ url + "?");

		if (para == null) {
			para = new HashMap<String, String>();
		}

		Iterator<Entry<String, String>> iter = para.entrySet().iterator();

		while (iter.hasNext()) {
			Entry<String, String> entry = iter.next();
			urlWithPara.append(entry.getKey());
			urlWithPara.append("=");
			String encodeStr = null;
			try {
				encodeStr = URLEncoder.encode(entry.getValue(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			urlWithPara.append(encodeStr);
			urlWithPara.append("&");
		}

		HttpRequest httpRequest = new HttpRequest(HttpMethods.GET);
		httpRequest.setUrl(urlWithPara.toString());
		Gdx.app.log(TAG, "url " + urlWithPara.toString());

		Gdx.net.sendHttpRequest(httpRequest, new HttpResponseListener() {

			@Override
			public void handleHttpResponse(HttpResponse httpResponse) {
				if (httpResponse.getStatus().getStatusCode() == HttpStatus.SC_OK) {
					String response = httpResponse.getResultAsString();
					Gdx.app.log(TAG, "response " + response);
					List<Protocol> ps = gson.fromJson(response,
							new TypeToken<List<Protocol>>() {
							}.getType());

					respQueue.addAll(ps);

				} else {
					Gdx.app.log(TAG, "http response error "
							+ httpResponse.getStatus().getStatusCode());
					respQueue.add(new Protocol(-1));
				}
			}

			@Override
			public void failed(Throwable t) {
				Gdx.app.log(TAG, "http failed " + t);
			}

			@Override
			public void cancelled() {
				Gdx.app.log(TAG, "http cancelled ");
			}

		});
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
