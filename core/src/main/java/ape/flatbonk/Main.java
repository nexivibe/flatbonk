package ape.flatbonk;

import com.badlogic.gdx.Game;

import ape.flatbonk.screen.MainMenuScreen;
import ape.flatbonk.state.PlayerConfig;

public class Main extends Game {
    private PlayerConfig playerConfig;

    @Override
    public void create() {
        playerConfig = new PlayerConfig();
        setScreen(new MainMenuScreen(this));
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
