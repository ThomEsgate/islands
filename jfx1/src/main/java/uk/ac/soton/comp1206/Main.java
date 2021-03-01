package uk.ac.soton.comp1206;

import javafx.application.Application;
//import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Random;
import javafx.scene.control.Slider;
import uk.ac.soton.comp1206.Block;
import uk.ac.soton.comp1206.PerlinNoiseGenerator;


public class Main extends Application {

    private double[][] perlinArray;
    private Block[][] blocksArray;
    private GridPane grid;
    private final int BLOCK_SIZE = 2;
    private final int WORLD_WIDTH = 512;
    private final int WORLD_HEIGHT = 512;

    private double threshold = 0.51;

    private double biggestPerlin = 0.06969;
    private double smollestPerlin = 0.6969;


    Stage window;

    @Override
    public void start(Stage primaryStage) throws Exception{
        grid = new GridPane();
        grid.setAlignment(Pos.CENTER);

        PerlinNoiseGenerator.shuffle();
        perlinArray = PerlinNoiseGenerator.getPerlinArray();
        perlinArray = PerlinNoiseGenerator.getPerlinArray();

        StackPane layout = new StackPane();

        //Button button = new Button("Click me");
        layout.getChildren().add(grid);

        window = primaryStage;
        primaryStage.setTitle("Perlin Noise Crap");
        primaryStage.setScene(new Scene(layout, 1050, 1050));

        initialiseBlocksArray();

        doEvent();

        primaryStage.show();
    }

    public void initialiseBlocksArray() {
        blocksArray = new Block[WORLD_WIDTH][WORLD_HEIGHT];
        for (int width = 0; width < WORLD_WIDTH; width++) {
            for (int height = 0; height < WORLD_HEIGHT; height++){

                double perlinValue = (perlinArray[width][height]);
                Block block = new Block(perlinToID(perlinValue), width, height, perlinValue);
                block.setX(width);
                block.setY(height);

                blocksArray[width][height] = block;
            }
        }
    }

    public int perlinToID(double perlinValue){
        //System.out.println("perlinToID perlinValue: " + perlinValue);
        if (perlinValue > 0.4){
            return 0;
        }
        if (perlinValue > 0.15){
            return 0;
        }
        if (perlinValue > 0){
            return 0;
        }
        if (perlinValue > -0.1){
            return 0;
        }
        else if (perlinValue > -0.3){
            return 3;
        }

        else return 4;
    }

    public void doFrame(){
        int width = 0;
        int height = 0;

        //drawing the squares
        //drawSquare(height, width, grid, s);
        for (int i = 0; i < ( (WORLD_WIDTH) * (WORLD_HEIGHT)); i++) {

            drawSquare(height, width, grid);
            width++;
            if (width > WORLD_WIDTH - 1) {
                width = 0;
                height++;
            }
        }

        //frameNo++;
        //expandIslands();
        //growGrass();
        //passOver();
    }

    public void doEvent(){
        long startTime = System.nanoTime();

        doFrame();

        long endTime = System.nanoTime();
        System.out.println("doEvent() done in:" + (endTime - startTime) / 1000000 + "ms");
        System.out.println(biggestPerlin + " " + smollestPerlin);
    }

    public void drawSquare(int height, int width, GridPane grid){

        //each block at x,y coords of width,height has a unique ID, which determines the colour
        Block block = blocksArray[width][height];
        block.setWidth(BLOCK_SIZE);
        block.setHeight(BLOCK_SIZE);
        int blockID = block.getBlockID();

        double perlinValue = block.getPerlinValue();
        double perlinValueToColour = (perlinValue + 1)/2;

        if (perlinValue > biggestPerlin){
            biggestPerlin = perlinValue;
        }
        if (perlinValue < smollestPerlin){
            smollestPerlin = perlinValue;
        }

        if (true){
            block.setFill(Color.color(perlinValueToColour * 0.7, perlinValueToColour * 0.7, perlinValueToColour * 0.7));
        }
        else
            block.setFill(Color.color(perlinValueToColour * 0.4, perlinValueToColour * 0.4, perlinValueToColour * 0.4));


//        if (Math.floor(perlinValue * 100) == 2) {
//            block.setFill(Color.color(0.7, 0.4, 0.8));
//        }
//        else {
//            if (perlinValueToColour > 1){
//                perlinValueToColour = 1;
//            }
//            else if (perlinValueToColour < 0){
//                perlinValueToColour = 0;
//            }
//            block.setFill(Color.color(perlinValueToColour, perlinValueToColour, perlinValueToColour));
//        }
        /*
        //Checks to see if out of bounds or something

        if (height == WORLD_HEIGHT) { //1 block outside the border
            rec.setFill(Color.color(1, 0.33, 0.3));
        }
        else if (width == WORLD_WIDTH) {//1 block outside the border
            rec.setFill(Color.color(0.3, 0.33, 1));
        }// outside the border
        else if ( (width > WORLD_WIDTH)||(height > WORLD_HEIGHT) ){
            rec.setFill(Color.color(0.2, 0.3, 0.2));
        }
        */

        /*
            switch (blockID) {
                case 0:
                    block.setFill(Color.color(0.9, 0.9, 0.9));
                    break;
                case 1:
                    block.setFill(Color.color(0.3, 0.3, 0.2));
                    break;
                case 4:
                    block.setFill(Color.color(0.2, 0.7, 1));
                    break;
                case 3:
                    block.setFill(Color.color(1, 1, 0.2));
                    break;
                case 2:
                    block.setFill(Color.color(0.2, 0.8, 0.1));
                    break;

                default:
                    block.setFill(Color.color(0.1, 0.5, 0.8));
                    break;
            }
*/
        GridPane.setRowIndex(block, height);
        GridPane.setColumnIndex(block, width);

        grid.getChildren().add(block);

        //Setting color to the scene
        //scene.setFill(Color.color(0.9, 0.115, 0.263));
    }

    public static void main(String[] args) {

        launch(args);
    }
}
