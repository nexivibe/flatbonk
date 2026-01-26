package ape.flatbonk.system;

import com.badlogic.gdx.math.MathUtils;

import ape.flatbonk.entity.Entity;
import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.entity.component.*;
import ape.flatbonk.hazard.*;
import ape.flatbonk.state.GameState;
import ape.flatbonk.util.PlayerColor;

public class HazardSystem implements GameSystem {
    private final EntityManager entityManager;
    private final GameState gameState;
    private final Hazard activeHazard;
    private float hazardTimer;

    public HazardSystem(EntityManager entityManager, GameState gameState) {
        this.entityManager = entityManager;
        this.gameState = gameState;
        this.activeHazard = createHazard(gameState.getPlayerColor());
        this.hazardTimer = 0;
    }

    private Hazard createHazard(PlayerColor color) {
        switch (color) {
            case RED:
                return new FireballHazard();
            case BLUE:
                return new IcePatchHazard();
            case GREEN:
                return new PoisonPoolHazard();
            case PURPLE:
                return new GravityWellHazard();
            default:
                return new FireballHazard();
        }
    }

    @Override
    public void update(float delta) {
        hazardTimer += delta;

        if (activeHazard != null) {
            activeHazard.update(delta, entityManager, gameState);
        }
    }

    @Override
    public int getPriority() {
        return 40;
    }
}
