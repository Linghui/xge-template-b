package com.gol.screens.logo;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.gol.CommonRes;
import com.gol.GameStarter;
import com.gol.xge.resolver.InAndExternalFileHandleResolver;
import com.gol.xge.rpg.scense.CoreScreen;
import com.gol.xge.rpg.scense.TacticsScreen;
import com.gol.xge.rpg.ui.NumericBar;

public class LogoScreen extends TacticsScreen {

    private String TAG = "LoginScreen";

    AssetManager manager = new AssetManager(
            new InAndExternalFileHandleResolver());
    Skin skin = null;

    private boolean srcLoadDone = false;

    public LogoScreen(Game game, AssetManager manager) {
        super(game, 800, 480);
        this.manager = manager;
    }

    @Override
    public void show() {
        super.show();
        manager.load(CommonRes.logoBack, Texture.class);
        manager.load(CommonRes.uiSkin, Skin.class);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        if (!manager.update()) {

        }
        if (manager.isLoaded(CommonRes.logoBack) && this.srcLoadDone == false) {
            this.srcLoadDone = true;
            init();
            selfInit();
            return;
        }

        selfRender(delta);

    }

    private void selfRender(float delta) {
        // TODO: do your own special render here
    }

    private void init() {

        Image background = new Image(manager.get(
                CommonRes.logoBack, Texture.class)
                );
        background.setX( (CoreScreen.width - background.getWidth() )/2 );
        background.setY( (CoreScreen.height - background.getHeight() )/2 );
        background.setColor(1, 1, 1, 0);
        this.addActorTop(background);
        
        background.addAction(
                Actions.sequence(
                        Actions.alpha(1, 0.2f),
                        Actions.delay(3f),
                        Actions.alpha(0, 1f),
                        new Action(){

                            @Override
                            public boolean act(float delta) {
                                gameInit();
                                return true;
                            }
                            
                        }
                        )
                );
        
        initCam();

        this.setCamFixed(true);

    }

    protected void gameInit() {
        
        game.setScreen(GameStarter.getInstance().getMainScreen());    
        this.dispose();
    }

    private void selfInit() {
        
    }

    @Override
    public void dispose() {
        super.dispose();
        manager.unload(CommonRes.logoBack);
    }

}
