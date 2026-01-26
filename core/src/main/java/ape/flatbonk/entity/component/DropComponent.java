package ape.flatbonk.entity.component;

public class DropComponent {
    private int xpValue;
    private int moneyValue;
    private float dropChance;

    public DropComponent(int xpValue, int moneyValue) {
        this.xpValue = xpValue;
        this.moneyValue = moneyValue;
        this.dropChance = 1f;
    }

    public int getXpValue() {
        return xpValue;
    }

    public void setXpValue(int xpValue) {
        this.xpValue = xpValue;
    }

    public int getMoneyValue() {
        return moneyValue;
    }

    public void setMoneyValue(int moneyValue) {
        this.moneyValue = moneyValue;
    }

    public float getDropChance() {
        return dropChance;
    }

    public void setDropChance(float dropChance) {
        this.dropChance = dropChance;
    }
}
