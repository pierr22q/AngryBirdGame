package angryflappybird;


import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.FontPosture; 
import javafx.scene.text.FontWeight; 
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.FileInputStream;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.InputMap;

// The Application layer
/**
 * AngryFlappyBird is a game that is a combination of
 * Flappy bird and Angry bird. The theme is space. There are
 * methods to initialize and move pipes, meteor, fuel, and rocket.
 * 
 * @author Nikki Cheng, Kareena Joshipura, Qianna Pierre
 *
 */
public class AngryFlappyBird extends Application {
    
    private Defines DEF = new Defines();
    
    // time related attributes
    private long clickTime, startTime, elapsedTime;   
    private AnimationTimer timer;
    
    // game components
    private Sprite blob;
    private Sprite meteor;
    private ArrayList<Sprite> floors;
    private ArrayList<Sprite> pipes;
    private ArrayList<Sprite> fuels;
    private Sprite bottomPipe;
    private Sprite topPipe;
    
    // score labels
    int score = 0;
    int increase;
    String scoreString = String.valueOf(score);
    Text scoreLabel = new Text();
    
    // lives label
    int lives = 3;
    String livesString = String.valueOf(lives);
    Text livesLabel = new Text();

    // game flags
    private boolean CLICKED, GAME_START, GAME_OVER;
    
    // scene graphs
    private Group gameScene;     // the left half of the scene
    private VBox gameControl;    // the right half of the GUI (control)
    private GraphicsContext gc;     
    
    // Three levels of difficulties. These go in the menu.
    private static final String EASY = "Easy";
    private static final String MEDIUM = "Medium";
    private static final String HARD = "Hard";
   
    // game descriptions
    Text gDescription1 = new Text ("Bonus points from fuel");
    Text gDescription2 = new Text ("Avoid meteors");
    Text gDescription3 = new Text ("Let you snooze");
    
    // Images for game description
    ImageView fuelPic = DEF.IMVIEW.get("fuel");
    ImageView meteorPic = DEF.IMVIEW.get("meteor");
    ImageView gfuelPic = DEF.IMVIEW.get("gfuel");
    
    // The menu to select difficulty levels.
    private ComboBox <String> levelMenu = new  ComboBox<>();
    

    /**
     * The mandatory main method
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }

       
    // the start method sets the Stage layer
    @Override
    public void start(Stage primaryStage) throws Exception {
    
        // initialize scene graphs and UIs
        resetGameControl();    // resets the gameControl
        resetGameScene(true);  // resets the gameScene
        
        HBox root = new HBox();
        HBox.setMargin(gameScene, new Insets(0,0,0,15));
        root.getChildren().add(gameScene);
        root.getChildren().add(gameControl);
        
        // create menu for difficulty selection
        createMenu();
        
        // add scene graphs to scene
        Scene scene = new Scene(root, DEF.APP_WIDTH, DEF.APP_HEIGHT);
        
        // finalize and show the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle(DEF.STAGE_TITLE);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    
    // the getContent method sets the Scene layer
    private void resetGameControl() {
        
        DEF.startButton.setOnMouseClicked(this::mouseClickHandler);

        gameControl = new VBox();
        gameControl.getChildren().addAll(DEF.startButton,levelMenu, fuelPic, gDescription1, meteorPic, gDescription2, gfuelPic, gDescription3);
    }
    
    // create a menu for difficulty levels
    private void createMenu() {
        levelMenu.getItems().addAll(EASY, MEDIUM, HARD);
        levelMenu.setOnAction(this::itemStateChanged);
    }
    
    // pick the difficulty
    private void selectLevel(String selected) {
        if (selected.equals(EASY)) {
            Easy();
        }else if(selected.equals(MEDIUM)) {
            Medium();
        }else if(selected.equals(HARD)) {
            Hard();
        }
    }
    
    // The 3 difficulty levels, changes to the scene shift 
    private void Easy() {
        DEF.SCENE_SHIFT_INCR = -0.4;
    }
    private void Medium() {
        DEF.SCENE_SHIFT_INCR = -0.5;
    }
    private void Hard() {
        DEF.SCENE_SHIFT_INCR = -0.7;
    }
    
    // listen for which difficulty is selected
    private void itemStateChanged(ActionEvent evt) {
        String selected = ((ComboBox)evt.getSource()).getSelectionModel().getSelectedItem().toString();
        selectLevel(selected);
    }
  
    // listen for mouse click and respond accordingly
    private void mouseClickHandler(MouseEvent e) {
        if (GAME_OVER) {
            resetGameScene(false);
        }
        else if (GAME_START){
            clickTime = System.nanoTime();   
        }
        GAME_START = true;
        CLICKED = true;
    }
    
    private void resetGameScene(boolean firstEntry) {
        
        // reset variables
        CLICKED = false;
        GAME_OVER = false;
        GAME_START = false;
        pipes = new ArrayList<>();
        floors = new ArrayList<>();
        fuels = new ArrayList<>();
        
        if(firstEntry) {
            // create two canvases
            Canvas canvas = new Canvas(DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
            gc = canvas.getGraphicsContext2D();
            
            // set font name, size, weight, and font color 
            scoreLabel.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 50)); 
            scoreLabel.setFill(Color.ANTIQUEWHITE);
            
            // set text and text position 
            scoreLabel.setText(Integer.toString(score));
            scoreLabel.setX(175);
            scoreLabel.setY(50);
            
            // set font name, size, weight,stroke, stroke width and font color 
            livesLabel.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 20));           
            livesLabel.setFill(Color.BLUE);
            livesLabel.setStrokeWidth(1); 
            livesLabel.setStroke(Color.BLACK);        

            // set text and text position 
            livesLabel.setText(Integer.toString(lives) + " Lives Left");
            livesLabel.setX(265);
            livesLabel.setY(550);
            
            // create a background
            ImageView background = DEF.IMVIEW.get("background");

            // create the game scene
            gameScene = new Group();
            gameScene.getChildren().addAll(background,canvas,scoreLabel,livesLabel);       
           
        }
        
        // initialize floor
        for(int i=0; i<DEF.FLOOR_COUNT; i++) {
            
            int posX = i * DEF.FLOOR_WIDTH;
            int posY = DEF.SCENE_HEIGHT - DEF.FLOOR_HEIGHT;
            
            Sprite floor = new Sprite(posX, posY, DEF.IMAGE.get("floor"));
            floor.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
            floor.render(gc);
            
            floors.add(floor);
        }
        
        // initialize pipes
        for(int i=0; i<DEF.PIPE_COUNT; i++) {
            
            Sprite topPipe = new Sprite(400, 0, DEF.IMAGE.get("topPipe"));
            Sprite bottomPipe = new Sprite(400, 400, DEF.IMAGE.get("bottomPipe"));

            topPipe.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
            bottomPipe.setVelocity(DEF.SCENE_SHIFT_INCR,0);
            
            topPipe.render(gc);
            bottomPipe.render(gc);
            
            pipes.add(topPipe);
            pipes.add(bottomPipe);
        }
            
        // initialize meteor     
        meteor = new Sprite(DEF.METEOR_POS_X, DEF.METEOR_POS_Y,DEF.IMAGE.get("meteor"));
        meteor.render(gc);
        
        // initialize fuel
        for (int i=0; i<DEF.FUEL_COUNT; i++) {
            
            int posX = (i+1) *300;
            int posY = DEF.SCENE_HEIGHT - DEF.FLOOR_HEIGHT -DEF.PIPE_HEIGHT+110; //-70
            
            Sprite fuel = new Sprite(posX, posY, DEF.IMAGE.get("fuel"));
            fuel.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
            
            fuels.add(fuel);
        }

        // initialize blob
        blob = new Sprite(DEF.BLOB_POS_X, DEF.BLOB_POS_Y,DEF.IMAGE.get("rocket0"));
        blob.render(gc);
        
        // initialize timer
        startTime = System.nanoTime();
        timer = new MyTimer();
        timer.start();
    }

    
    // timer stuff
    class MyTimer extends AnimationTimer {
        
        int counter = 0;
        
         @Override
         public void handle(long now) {          
             // time keeping
             elapsedTime = now - startTime;
             startTime = now;
             
             // clear current scene
             gc.clearRect(0, 0, DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);

             if (GAME_START) {
                 // step1: update floor
                 moveFloor();
                 // step2: move Pipe
                 for(int i = 0; i < 3 ; i++) {
                    movePipe();
                    movePipe();
                 }
                 // step3: update blob
                 moveBlob();
                 // step4: update meteor
                 moveMeteor();
                 // step5: update fuel
                 moveFuel();
                 // check for collision
                 checkCollision();  
             }
         }
         
         
         // step1: update floor
         private void moveFloor() {
            
            for(int i=0; i<DEF.FLOOR_COUNT; i++) {
                if (floors.get(i).getPositionX() <= -DEF.FLOOR_WIDTH) {
                    double nextX = floors.get((i+1)%DEF.FLOOR_COUNT).getPositionX() + DEF.FLOOR_WIDTH;
                    double nextY = DEF.SCENE_HEIGHT - DEF.FLOOR_HEIGHT;
                    floors.get(i).setPositionXY(nextX, nextY);
                }
                floors.get(i).render(gc);
                floors.get(i).update(DEF.SCENE_SHIFT_TIME);
            }
         }
         
         // step2: move Pipe
         private void movePipe() {
            
             for(increase=0; increase < 2; increase++) {
                if (pipes.get(increase).getPositionX() <= -DEF.PIPE_WIDTH) {
                        if (increase == 0) {
                            
                            double nextX  = 400;
                            double nextY = new Random().nextInt(40 + 1)  - 50; 
                            
                            pipes.get(increase).setPositionXY(nextX, nextY);
                            addScore(); 
                        } 
                        else if (increase == 1){
                            double nextX  = 400;
                            double nextY = new Random().nextInt(120 + 1) + 350;                          
                            
                            pipes.get(increase).setPositionXY(nextX, nextY);
                        }
                }
                pipes.get(increase).render(gc);
                pipes.get(increase).update(DEF.SCENE_SHIFT_TIME);
            }    
         }
         
         // step3: update blob
         private void moveBlob() {
             
            long diffTime = System.nanoTime() - clickTime;
            
            // blob flies upward with animation
            if (CLICKED && diffTime <= DEF.BLOB_DROP_TIME) {
                
                int imageIndex = Math.floorDiv(counter++, DEF.BLOB_IMG_PERIOD);
                imageIndex = Math.floorMod(imageIndex, DEF.BLOB_IMG_LEN);
                blob.setImage(DEF.IMAGE.get("rocket"+String.valueOf(imageIndex)));
                blob.setVelocity(0, DEF.BLOB_FLY_VEL);
            }
            
            // blob drops after a period of time without button click
            else {
                blob.setVelocity(0, DEF.BLOB_DROP_VEL); 
                CLICKED = false;
            }
            // code meant for sound, but doesn't work on our computer
            // if (blob.intersectsSprite(topPipe) == true) {
            //   collisionSound.play();
            // }
        
            // render blob on GUI
            blob.update(elapsedTime * DEF.NANOSEC_TO_SEC);
            blob.render(gc);
         }
         
         // step4: update meteor
         private void moveMeteor() {                    
            if(meteor.getPositionX()>=0 && meteor.getPositionX()<400 && meteor.getPositionY()<300) {
                meteor.setVelocity(DEF.SCENE_SHIFT_INCR, DEF.METEOR_DROP_VEL);
            } 
            else if (meteor.getPositionX()<0 && meteor.getPositionY()>300) {
                 meteor.setPositionXY(new Random().nextInt((30 + 1) + 260),0);
                 meteor.setVelocity(DEF.SCENE_SHIFT_INCR, DEF.METEOR_DROP_VEL);
            }
            else if (meteor.getPositionX()>0 && meteor.getPositionY()>350) {
                 meteor.setPositionXY(240,0);
                 meteor.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
            }

            meteor.update(elapsedTime*DEF.NANOSEC_TO_SEC);
            meteor.render(gc);
        }
         
         // step5: update fuel
         private void moveFuel() {
             for (Sprite fuel: fuels) {
                 fuel.setVelocity(-DEF.FUEL_VEL, 0);
                 fuel.update(elapsedTime * DEF.NANOSEC_TO_SEC);
                 fuel.render(gc);
             }
         }
         
         // increase the score count
         private void addScore() {
             if (pipes.get(increase).getPositionX() < blob.getPositionX()) {
                checkCollision(); 
             }
             
             if(!GAME_OVER) {
                score += 1;
                scoreLabel.setText(Integer.toString(score));
         }
         }
         
         // reset the number of lives
         public void resetLives() {
             if(lives == 0) {
                    lives = 3;
                    score = 0;
                    scoreLabel.setText(Integer.toString(score));
                    
                } else if (lives > 0){
                    lives -= 1;
                }
                livesLabel.setText(Integer.toString(lives) + " Lives Left");
            }
         
         // check for collision of different objects
         public void checkCollision() {
            
            // check collision  with floor
            for (Sprite floor: floors) {
                GAME_OVER = GAME_OVER || blob.intersectsSprite(floor);  
            }
            // check collision with top pipe
            for (Sprite topPipe: pipes) {
                GAME_OVER = GAME_OVER || blob.intersectsSprite(topPipe);
            }
            // check collision with bottom pipe
            for (Sprite bottomPipe: pipes) {
                GAME_OVER = GAME_OVER || blob.intersectsSprite(bottomPipe);
            }
            // check collision with meteor
            if  (blob.intersectsSprite(meteor) == true) {
                GAME_OVER = GAME_OVER;
                
            }
            
            if (GAME_OVER) {
                showHitEffect(); 
                for (Sprite floor: floors) {
                    floor.setVelocity(0, 0);
                    if (blob.intersectsSprite(floor) == true) {
                        score = 0;
                        scoreLabel.setText(Integer.toString(score));
                    }
                }
                for (Sprite topPipe:pipes) {
                    topPipe.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
                }
                
                for(Sprite bottomPipe: pipes) {
                    bottomPipe.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
                }
                resetLives() ;
                timer.stop();
            }
         }
         
         // show the effects of hitting into an object
         private void showHitEffect() {
            ParallelTransition parallelTransition = new ParallelTransition();
            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(DEF.TRANSITION_TIME), gameScene);
            fadeTransition.setToValue(0);
            fadeTransition.setCycleCount(DEF.TRANSITION_CYCLE);
            fadeTransition.setAutoReverse(true);
            parallelTransition.getChildren().add(fadeTransition);
            parallelTransition.play(); 
         }
         
    } // End of MyTimer class
  } // End of AngryFlappyBird Class
