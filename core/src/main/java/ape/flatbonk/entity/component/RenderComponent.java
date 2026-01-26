package ape.flatbonk.entity.component;

import com.badlogic.gdx.graphics.Color;

import ape.flatbonk.util.ShapeType;

public class RenderComponent {
    private ShapeType shapeType;
    private Color color;
    private float size;
    private boolean visible;
    private int layer;

    public RenderComponent() {
        this.shapeType = null;
        this.color = Color.WHITE.cpy();
        this.size = 10f;
        this.visible = true;
        this.layer = 0;
    }

    public RenderComponent(ShapeType shapeType, Color color, float size) {
        this.shapeType = shapeType;
        this.color = color.cpy();
        this.size = size;
        this.visible = true;
        this.layer = 0;
    }

    public ShapeType getShapeType() {
        return shapeType;
    }

    public void setShapeType(ShapeType shapeType) {
        this.shapeType = shapeType;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color.cpy();
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }
}
