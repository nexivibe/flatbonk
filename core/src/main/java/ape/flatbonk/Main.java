package ape.flatbonk;

import com.badlogic.gdx.Game;

import ape.flatbonk.screen.SplashScreen;
import ape.flatbonk.state.PlayerConfig;

public class Main extends Game {
    private PlayerConfig playerConfig;

    @Override
    public void create() {
        playerConfig = new PlayerConfig();
        setScreen(new SplashScreen(this));
    }

    public PlayerConfig getPlayerConfig() {
        return playerConfig;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (getScreen() != null) {
            getScreen().dispose();
        }
    }
}
