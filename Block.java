package sample;

import javafx.scene.shape.Rectangle;

public class Block extends Rectangle {

    private int blockID, x, y;
    private double perlinValue;

    public Block(int blockID, int x, int y){
        this.blockID = blockID;
        this.x = x;
        this.y = y;
    }

    public Block(int blockID, int x, int y, double perlinValue){
        this.blockID = blockID;
        this.x = x;
        this.y = y;
        this.perlinValue = perlinValue;
    }

    public int getBlockID() {
        return blockID;
    }

    public double getPerlinValue() {
        return perlinValue;
    }

    public int getYpos() {
        return y;
    }

    public int getXpos() {
        return x;
    }
}
