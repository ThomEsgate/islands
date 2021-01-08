/**
 * https://stackoverflow.com/questions/9966136/javafx-periodic-background-task
 *
 * LIKE FUCKIN
 *
 * MAKE A BLOCK OBJECT SO YOU CAN STORE THAT FUCKIN OBJECT SOMEWHERE AND DO STUFF WITH IT INSTEAD OF FUCKIN GOING INTO A MASSIVE ARRAY EACH TIME!!!
 */

package org.openjfx;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.application.Application;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.*;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.lang.Math;

import java.util.Random;
import java.util.Vector;

public class Main extends Application {

    private final int WORLD_WIDTH = 256;
    private final int WORLD_HEIGHT = 128;
    private final int BLOCK_SIZE = 8;
    private final int FPS = 1;

    private GridPane grid = new GridPane();

    private int height = 0;
    private int width = 0;
    private int frameNo = 0;


    private Integer[][] blocksArray;

    @Override
    public void start(Stage primaryStage) throws Exception{
        initialiseBlocksArrayOcean();
        //grid.setGridLinesVisible(true);


        //In the start() method creates a group object by instantiating the class named Group, which belongs to the package javafx.scene
        //Group root = new Group(line);
        Group root = new Group(grid);

        //root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        //+ You can add a Scene object to the stage using the method setScene() of the class named Stage
        Scene scene = new Scene(root, WORLD_WIDTH * BLOCK_SIZE + BLOCK_SIZE * 4, WORLD_HEIGHT * BLOCK_SIZE + BLOCK_SIZE * 4); //Add the extra BLOCK_SIZEs as a buffer

        //instantiating the class named Scene which belongs to the package javafx.scene.
        primaryStage.setScene(scene);



        Timeline timeline =
                new Timeline(new KeyFrame(Duration.millis(1000/FPS), e -> doEvent(grid, scene)));
        timeline.setCycleCount(Animation.INDEFINITE); // loop forever
        timeline.play();


        //You can set the title to the stage using the setTitle() method of the Stage class
        primaryStage.setTitle("Moppreceduratl " + grid.getColumnCount() + " " + grid.getRowCount());
        //Display the contents of the scene using the method named show() of the Stage class
        primaryStage.show();

    }

    public void doEvent(GridPane g, Scene s){
        //drawSquare(height, width, grid, s);
        grid.getChildren().clear();
        width = 0;
        height = 0;

        //drawing the squares
        for (int i = 0; i < ( (WORLD_WIDTH) * (WORLD_HEIGHT)); i++) {

            drawSquare(height, width, grid, s);
            width++;
            if (width > WORLD_WIDTH - 1) {
                width = 0;
                height++;
            }
        }
        frameNo++;
        expandIslands();
        //growGrass();
        //passOver();
    }

    int MAX_EXTENSION = 3;

    private void expandIslands() {
        for (int width = 1; width < WORLD_WIDTH - 2; width++) {
            for (int height = 1; height <  WORLD_HEIGHT - 2; height++) {

                int currentBlockID = blocksArray[width][height];
                boolean isCoasting = isOnCoast(width, height);

                if ( isOnCoast(width, height) && currentBlockID != 2 ){
                    Random random = new Random();
                    if ((blocksArray[width - 1][height] == 2)){ //LEFT
                        for (int i = 0; i < random.nextInt(MAX_EXTENSION); i++) {
                            placeBlock(width - i, height, 3);
                        } }
                    if ((blocksArray[width][height + 1] == 2)){ //TOP
                        for (int i = 0; i < random.nextInt(MAX_EXTENSION); i++) {
                            placeBlock(width, height + i, 3);
                        } }
                    if ((blocksArray[width + 1][height] == 2)){ //LEFT
                        for (int i = 0; i < random.nextInt(MAX_EXTENSION); i++) {
                            placeBlock(width + i, height, 3);
                        } }
                    if ((blocksArray[width][height - 1] == 2)){ //TOP
                        for (int i = 0; i < random.nextInt(MAX_EXTENSION); i++) {
                            placeBlock(width, height - i, 3);
                        } }
                }
                if ( (currentBlockID == 3) && (! isOnCoast(width, height))) {
                    placeBlock(width, height, 4);
                }
            }
        }
    }

    private void growGrass(){
        for (int width = 1; width < WORLD_WIDTH - 2; width++) {
            for (int height = 1; height < WORLD_HEIGHT - 2; height++) {
                if (blocksArray[width][height] != 2) {
                    if ( !isOnCoast(width, height)) {
                        placeBlock(width, height, 3);
                    }
                }

            }
        }
    }

    public boolean isOnCoast(int x, int y){
        if ( (blocksArray[x+1][y] == 2) || (blocksArray[x-1][y] == 2) || (blocksArray[x][y+1] == 2) || (blocksArray[x][y-1] == 2)) {
            return true;
        }
        return false;
    }

    public void drawSquare(int height, int width, GridPane grid, Scene scene){
        Rectangle rec = new Rectangle();

        rec.setWidth(BLOCK_SIZE);
        rec.setHeight(BLOCK_SIZE);

        //each block at x,y coords of width,height has a unique ID, which determines the colour
        int blockID = blocksArray[width][height];

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


        if (frameNo % 2 == 1) {

            switch (blockID) {
                case 0:
                    rec.setFill(Color.color(0.9, 0.9, 0.9));
                    break;
                case 1:
                    rec.setFill(Color.color(0.3, 0.3, 0.2));
                    break;
                case 2:
                    rec.setFill(Color.color(0.2, 0.7, 1));
                    break;
                case 3:
                    rec.setFill(Color.color(1, 1, 0.2));
                    break;
                case 4:
                    rec.setFill(Color.color(0.2, 0.8, 0.1));
                    break;

                default:
                    rec.setFill(Color.color(0.1, 0, 0));
                    break;
            }
        }
        else {
            switch (blockID) {
                case 0:
                    rec.setFill(Color.color(0.9, 0.9, 0.9));
                    break;
                case 1:
                    rec.setFill(Color.color(0.3, 0.3, 0.2));
                    break;
                case 2:
                    rec.setFill(Color.color(0.1, 0.2, 0.5));
                    break;
                case 3:
                    rec.setFill(Color.color(0.5, 0.5, 0.1));
                    break;
                case 4:
                    rec.setFill(Color.color(0.1, 0.3, 0.1));
                    break;


                default:
                    rec.setFill(Color.color(0.1, 0, 0));
                    break;
            }
        }

//        else {
//            rec.setFill(Color.color(Math.abs(Math.sin(frameNo/0.4)), Math.abs(Math.sin(0.6)), Math.abs(Math.sin(frameNo/0.8)) ));
//        }

        GridPane.setRowIndex(rec, height);
        GridPane.setColumnIndex(rec, width);

        grid.getChildren().addAll(rec);

        //Setting color to the scene
        //scene.setFill(Color.color(0.9, 0.115, 0.263));
    }

    /**Sets half of the blocks to be white, the bottom half to be black
     *
     */
    public void initialiseBlocksArray() {
        blocksArray = new Integer[WORLD_WIDTH][WORLD_HEIGHT];
        for (width = 0; width < WORLD_WIDTH; width++) {
            for (height = 0; height < WORLD_HEIGHT; height++){
                if (height <= WORLD_HEIGHT/2) {
                    blocksArray[width][height] = 0;
                }
                else {
                    blocksArray[width][height] = 1;
                }
            }
        }
    }

    public void initialiseBlocksArrayOcean() {
        blocksArray = new Integer[WORLD_WIDTH][WORLD_HEIGHT];
        for (width = 0; width < WORLD_WIDTH; width++) {
            for (height = 0; height < WORLD_HEIGHT; height++){
                Random random = new Random();

                if (random.nextInt(1024) == 0) {
                    blocksArray[width][height] = 3;
                }
                else {
                    blocksArray[width][height] = 2;
                }
            }
        }
    }


    void placeBlock(int x, int y, int id){
        try {
            blocksArray[x][y] = id;
        }
        catch (Exception e){
            System.err.println("ERROR PLACING BLOCK at (" + x + "," + y + "): " + e);
        }
    }

    //Supid b
    public void passOver(){
        Random random = new Random();
        Integer offsets[] = new Integer[WORLD_WIDTH];

        //Get offsets
        for (int i = 0; i < offsets.length; i++){
            offsets[i] = random.nextInt(3); //0-2
            System.out.println("Offset: " + offsets[i]);
            if (random.nextInt(3) == 0) {
                offsets[i] = -offsets[i];
            }
        }

        //Apply offsets to array
        for (int width = 0; width < WORLD_WIDTH; width++){

            int heightOfBlockBeingChecked = 0;
            while (blocksArray[width][heightOfBlockBeingChecked] == 0) { //while is just air
                heightOfBlockBeingChecked++;
            }
            //System.out.println("Offset: " + offsets[width]);

            //-ve Offset (remove blocks)
            if (offsets[width] < 0){
                //blocksArray[width][heightOfBlockBeingChecked] = 0;//Get rid of the first top block first
                for (int i = 0; i < -offsets[width]; i++){
                    if (heightOfBlockBeingChecked > WORLD_HEIGHT - 2){
                        System.out.println("Bye 2 high");
                        break;
                    }
                    //System.out.println(offsets[width]);
                    blocksArray[width][heightOfBlockBeingChecked] = 0;
                    heightOfBlockBeingChecked++;
                }
            }
            //+ve Offset (add blocks)
            else if (offsets[width] > 0){
                for (int i = 0; i < offsets[width]; i++){
                    if (heightOfBlockBeingChecked < 0){
                        break;
                    }
                    blocksArray[width][heightOfBlockBeingChecked] = 1;
                    heightOfBlockBeingChecked--;
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
