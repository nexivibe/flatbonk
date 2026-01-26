package ape.flatbonk.system;

public interface GameSystem {
    void update(float delta);
    int getPriority();
}
