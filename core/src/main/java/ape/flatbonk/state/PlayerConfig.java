package ape.flatbonk.state;

import ape.flatbonk.util.PlayerColor;
import ape.flatbonk.util.ShapeType;

public class PlayerConfig {
    private ShapeType selectedShape;
    private PlayerColor selectedColor;

    public PlayerConfig() {
        this.selectedShape = ShapeType.CIRCLE;
        this.selectedColor = PlayerColor.RED;
    }

    public ShapeType getSelectedShape() {
        return selectedShape;
    }

    public void setSelectedShape(ShapeType selectedShape) {
        this.selectedShape = selectedShape;
    }

    public PlayerColor getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(PlayerColor selectedColor) {
        this.selectedColor = selectedColor;
    }

    public void reset() {
        this.selectedShape = ShapeType.CIRCLE;
        this.selectedColor = PlayerColor.RED;
    }
}
