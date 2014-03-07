package com.gol.screens.main;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.JsonValue;
import com.gol.CommonRes;
import com.gol.GameStarter;
import com.gol.xge.rpg.scense.CoreScreen;
import com.gol.xge.rpg.scense.TacticsScreen;
import com.gol.xge.rpg.ui.AnimationActor;
import com.gol.xge.rpg.ui.NumericBar;
import com.gol.xge.util.XGECommon;

public class MainScreen extends TacticsScreen {

	private String TAG = this.getClass().getSimpleName();

	private AssetManager manager = null;
	Skin skin = null;

	private boolean srcLoadDone = false;
	private NumericBar loadingBar = null;

	private Label fps;

	private List<String> resToUnload = null;

	private AnimationActor actor;

	private TextButton btn;

	public MainScreen(Game game, AssetManager manager) {
		super(game, GameStarter.width, GameStarter.height);
		this.manager = manager;

		resToUnload = new ArrayList<String>();

		manager.load(CommonRes.uiSkin, Skin.class);
		resToUnload.add(CommonRes.uiSkin);

		manager.load(CommonRes.resBase + "actions/50001/50001.atlas",
				TextureAtlas.class);
		resToUnload.add(CommonRes.resBase + "actions/50001/50001.atlas");

		Gdx.app.log(TAG, "loader " + manager.getLoader(JsonValue.class));
		manager.load(CommonRes.resBase + "actions/50001/50001.json",
				JsonValue.class);
		resToUnload.add(CommonRes.resBase + "actions/50001/50001.json");
	}

	@Override
	public void show() {
		super.show();

		Gdx.app.log(TAG, "show");
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		if (!manager.update()) {
			// Gdx.app.log(TAG, "progress " + manager.getProgress());
			if (manager.isLoaded(CommonRes.uiSkin) && loadingBar == null) {
				skin = manager.get(CommonRes.uiSkin, Skin.class);

				loadingBar = new NumericBar(skin.getDrawable("baidian"),
						skin.getDrawable("selection"), 0,
						manager.getQueuedAssets(), 19, 10);
				loadingBar.setLabel(skin.get(LabelStyle.class), 40, true);
				loadingBar
						.setX((this.getRootWidth() - loadingBar.getWidth()) / 2);
				loadingBar
						.setY((this.getRootHeight() - loadingBar.getHeight()) / 2);
				this.addActorBackground(loadingBar);
				loadingBar.addTips("正在载入资源", -40);
			}

			if (loadingBar != null) {
				int status = (int) (manager.getProgress() * 10);
				// Gdx.app.log(TAG, "status " + status);
				loadingBar.setStatusNum(status);

			}
		}

		if (this.srcLoadDone == false && manager.update()) {
			if (loadingBar != null) {
				loadingBar.remove();
			}
			this.srcLoadDone = true;
			init();
			selfInit();
			return;
		}

		if (manager.update()) {
			selfRender(delta);
		}
	}

	private void selfRender(float delta) {
		// for table debug, remember to remove it when deploy
		// renderTableDebug();

		// fps debug
		if (fps != null) {
			fps.setText("fps:" + Gdx.graphics.getFramesPerSecond() + " x:"
					+ Gdx.input.getX() + " y:"
					+ (CoreScreen.height - Gdx.input.getY()));
		}
	}

	private void init() {

		Gdx.app.log(TAG, "init");
		if (skin == null) {
			skin = manager.get(CommonRes.uiSkin, Skin.class);
		}

		initCam();

		// this.setCover(skin.getDrawable("zhebian"));

		fps = new Label("fps:99", skin);
		this.addActorTop(fps);

	}

	private void selfInit() {
		actor = new AnimationActor(XGECommon.readAnimationGroup(manager
				.get(CommonRes.resBase + "actions/50001/50001.json",
						JsonValue.class), manager.get(CommonRes.resBase
				+ "actions/50001/50001.atlas", TextureAtlas.class)));
		actor.setX((width - actor.getWidth()) / 2);
		actor.setY((height - actor.getHeight()) / 2);
		actor.setAction("run", true);
		this.addActorTop(actor);

		btn = new TextButton("change action", skin);
		btn.setX(width / 2);
		btn.setY((height - btn.getHeight()) / 2);
		btn.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				changeAction();
			}
		});
		this.addActorTop(btn);
	}

	String[] actionNameList = { "run", "stand", "attack", "beat" };
	int actionIndex = 0;

	protected void changeAction() {
		actionIndex++;
		actionIndex = actionIndex % 4;
		actor.setAction(actionNameList[actionIndex], true);
		btn.setText(actionNameList[actionIndex]);
	}

	// for back key input
	@Override
	protected void callBack() {
		Gdx.app.log(TAG, "callBack key");

		// exitGame();
	}

	public void exitGame() {
		this.dispose();
		GameStarter.getInstance().dispose();
		Gdx.app.exit();
	}

	@Override
	public void dispose() {
		Gdx.app.log(TAG, "dispose");
		super.dispose();

		for (String name : this.resToUnload) {
			if (manager.isLoaded(name)) {
				manager.unload(name);
			}
		}
	}
}
