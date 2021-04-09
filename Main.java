package uk.ac.soton.comp1206;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
//import javafx.fxml.FXMLLoader;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import javafx.scene.control.Slider;
import uk.ac.soton.comp1206.Block;
import uk.ac.soton.comp1206.PerlinNoiseGenerator;

import javax.security.auth.callback.LanguageCallback;


public class Main extends Application {

    private HashMap<KeyCode, Boolean> keys = new HashMap<KeyCode, Boolean>(); //List of keys and if they're pressed down or not
    private ArrayList<Node> platforms = new ArrayList<Node>();

    private Pane appRoot = new Pane();
    private Pane gameRoot = new Pane(); //Platforms, Players (SCROLLABLE)!
    private BorderPane uiRoot = new BorderPane(); //UI

    private Scene scene;


    private Point2D playerVelocity = new Point2D(0, 0);
    private boolean canJump = false;
    private boolean inAir = false;

    private int levelWidth;
    private float steps = 0;

    private double[][] perlinArray;
    private Block[][] blocksArray;
    private GridPane grid;
    private final int BLOCK_SIZE = 64;
    private final int WORLD_WIDTH = 512;
    private final int WORLD_HEIGHT = 512;

    private final int PLAYER_HEIGHT = BLOCK_SIZE - 8;
    private final int PLAYER_WIDTH = BLOCK_SIZE - 8;

    private double threshold = 0.51;

    private double biggestPerlin = 0.06969;
    private double smollestPerlin = 0.6969;

    private Node player = createEntity(BLOCK_SIZE*2, BLOCK_SIZE*3, PLAYER_WIDTH, PLAYER_HEIGHT, Color.BLUE);
    private int walkSpeed = 5;

    private SimpleFloatProperty property_playerVelocityX = new SimpleFloatProperty();
    private SimpleFloatProperty property_playerVelocityY = new SimpleFloatProperty();

    @Override
    public void start(Stage primaryStage) throws Exception{
        scene = new Scene(appRoot);
        scene.setOnKeyPressed(e -> keys.put(e.getCode(), true));
        scene.setOnKeyReleased(e -> keys.put(e.getCode(), false));

        initContent();



        primaryStage.setScene(scene);
        primaryStage.show();
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                update(); //Called 60 times per second :^)
            }
        };
        timer.start();
        /*grid = new GridPane();
        grid.setAlignment(Pos.CENTER);

        StackPane layout = new StackPane();

        Button button = new Button("Click me");
        layout.getChildren().addAll(grid, button);

        window = primaryStage;
        primaryStage.setTitle("Perlin Noise Crap");
        primaryStage.setScene(new Scene(layout, 1050, 1050));

        primaryStage.show();*/
    }

    private void initContent(){
        Rectangle bg = new Rectangle(1000, 1000, Color.BEIGE);

        levelWidth = LevelData.LEVEL1[0].length() * BLOCK_SIZE;

        for (int i = 0; i < LevelData.LEVEL1.length; i++) {
            String line = LevelData.LEVEL1[i];
            for (int j = 0; j < line.length(); j++) {
                switch (line.charAt(j)) {
                    case '0':
                        break;
                    case '1':
                        Node platform = createEntity(j * BLOCK_SIZE, i * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE, Color.BROWN);
                        platforms.add(platform);
                        break;
                }
            }
        }

        //Scrolling part
        player.translateXProperty().addListener((obs, old, newValue) ->{
            int offset = newValue.intValue();

            if (offset >  1050/2 && offset < levelWidth - 1050/2){ //1050 is window size
                gameRoot.setLayoutX( -(offset - 1050/2));
            }
        });

        VBox infoVBox = new VBox();

        HBox  hBox_playerVelocityX = new HBox();
        Label label_playerVelocityX= new Label("playerVelocityX: ");
        Label label_playerVelocityX_value  = new Label("thingX");
        hBox_playerVelocityX.getChildren().addAll(label_playerVelocityX, label_playerVelocityX_value);

        HBox  hBox_playerVelocityY = new HBox();
        Label label_playerVelocityY= new Label("playerVelocityY: ");
        Label label_playerVelocityY_value  = new Label("thingY");
        hBox_playerVelocityY.getChildren().addAll(label_playerVelocityY,label_playerVelocityY_value);

        label_playerVelocityX_value.textProperty().bind(property_playerVelocityX.asString());
        label_playerVelocityY_value.textProperty().bind(property_playerVelocityY.asString());

        VBox leftBox = new VBox(2);
        leftBox.getChildren().addAll(hBox_playerVelocityX, hBox_playerVelocityY);

        uiRoot.setLeft(leftBox);

        appRoot.getChildren().addAll(bg, gameRoot, uiRoot);
    }

    private void update() {
        //KEY BINDINGS
        if (isPressed(KeyCode.E) && player.getTranslateY() >= walkSpeed){
            jumpPlayer();
        }
        if (isPressed(KeyCode.S) && player.getTranslateX() >= walkSpeed){
            movePlayerX(-walkSpeed);
        }
        if (isPressed(KeyCode.F) && player.getTranslateY() + BLOCK_SIZE <= levelWidth - 5){
            movePlayerX(walkSpeed);
        }

        if (playerVelocity.getY() < 9){ //9 = Max value for gravity
            Point2D newVelocity = new Point2D(playerVelocity.getX(), 1 );
            playerVelocity = playerVelocity.add(0, 1);
            // System.out.println(playerVelocity.getY());
            //System.out.println( (-Math.pow((steps-16),3)/400) + " | " + steps +"  ");
            System.out.println(playerVelocity.getY());

            if(!canJump) {
                steps += (float)1 / 30;
            }
            else steps = 0;

            //System.out.println(playerVelocity.getY()+22 + "\n");
            System.out.println(property_playerVelocityY.asString());
        }
        movePlayerY((int)playerVelocity.getY());


    }

    private void movePlayerX(int value) {
        boolean movingRight = value > 0;

        for (int i = 0; i < Math.abs(value); i++){
            for (Node platform : platforms){
                if (player.getBoundsInParent().intersects(platform.getBoundsInParent())){
                    if (movingRight){
                        if (player.getTranslateX() + PLAYER_WIDTH == platform.getTranslateX()){
                            return; //Hit platform, can't move
                        }
                    }
                    else { //Moving Left
                        if (player.getTranslateX() == platform.getTranslateX() + PLAYER_WIDTH){
                            return; //Hit platform, can't move
                        }
                    }
                }
            }
            player.setTranslateX(player.getTranslateX() + (movingRight ? 1 : -1));
        }
    }

    private void movePlayerY(int value) {
        boolean movingDown = value > 0;
        for (int i = 0; i < Math.abs(value); i++){
            for (Node platform : platforms){
                if (player.getBoundsInParent().intersects(platform.getBoundsInParent())){
                    if (movingDown){
                        if (player.getTranslateY() + PLAYER_HEIGHT == platform.getTranslateY()){
                            player.setTranslateY(player.getTranslateY() - 1);
                            canJump = true;
                            return;
                        }
                    }
                    else { //Moving UP
                        if (player.getTranslateY() == platform.getTranslateY() + PLAYER_HEIGHT){
                            Point2D newVelocity = new Point2D(playerVelocity.getX(), 0);
                            playerVelocity = newVelocity;
                            canJump = false;
                            return; //Hit platform, can't move
                        }
                    }
                }
            }
            player.setTranslateY(player.getTranslateY() + (movingDown ? 1 : -1));
        }

    }

    private void jumpPlayer() {
        if (canJump){
            //System.out.println("Jumping");
            playerVelocity = playerVelocity.add(0, -32);
            canJump = false;
        }
        else {
            //System.out.println("Can't jump");
        }
    }

    private Node createEntity(int x, int y, int width, int height, Color colour) {
        Rectangle entity = new Rectangle(width, height, colour);
        entity.setTranslateX(x);
        entity.setTranslateY(y);

        gameRoot.getChildren().add(entity);
        return entity;
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

    private void perlinStuff(){
        PerlinNoiseGenerator.shuffle();
        perlinArray = PerlinNoiseGenerator.getPerlinArray();
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

    public Boolean isPressed(KeyCode key){
        return keys.getOrDefault(key, false);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
