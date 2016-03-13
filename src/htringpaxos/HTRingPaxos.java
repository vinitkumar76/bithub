/*
 * Copyright © 2016 Vinitkumar.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package htringpaxos;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
/**
 *
 * @author Vinitkumar
 */
public class HTRingPaxos extends Application {
    @Override
    public void start(Stage primaryStage) {
        final Image e;
        e = new Image(getClass().getResourceAsStream("vinitkumar.jpg"));
        primaryStage.getIcons().add(e);
        Label l1=new Label("Press Button to Launch HT-Paxos Agents:");
        l1.setTextFill(Color.BROWN);
        l1.setFont(Font.font(16));
        Button btn1 = new Button("Acceptor");
        btn1.setMinSize(100, 40);
        btn1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    Thread t =new Thread(new AcceptorThread());
                    t.setDaemon(true);
                    t.start();
                    btn1.setDisable(true);
                    dialog(primaryStage,"Acceptor");
                    } catch (Exception ex) {
                    Logger.getLogger(HTRingPaxos.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        btn1.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    if(event.getCode()==KeyCode.ENTER){
                        try {
                        Thread t =new Thread(new AcceptorThread());
                        t.setDaemon(true);
                        t.start();
                        btn1.setDisable(true);
                        dialog(primaryStage,"Acceptor");
                        } catch (Exception ex) {
                        Logger.getLogger(HTRingPaxos.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            });
        Button btn2 = new Button("Proposer");
        btn2.setMinSize(100, 40);
        btn2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    Thread t= new Thread(new ProposerThread());
                    t.setDaemon(true);
                    t.start();
                    btn2.setDisable(true);
                    dialog(primaryStage,"Proposer");
                } catch (Exception ex) {
                    Logger.getLogger(HTRingPaxos.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        btn2.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    if(event.getCode()==KeyCode.ENTER){
                        try {
                            Thread t= new Thread(new ProposerThread());
                            t.setDaemon(true);
                            t.start();
                            btn2.setDisable(true);
                            dialog(primaryStage,"Proposer");
                        } catch (Exception ex) {
                        Logger.getLogger(HTRingPaxos.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            });
        Button btn3 = new Button("Learner");
        btn3.setMinSize(100, 40);
        btn3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    Thread t= new Thread(new LearnerThread());
                    t.setDaemon(true);
                    t.start();
                    btn3.setDisable(true);
                    dialog(primaryStage,"Learner");
                } catch (Exception ex) {
                    Logger.getLogger(HTRingPaxos.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        btn3.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    if(event.getCode()==KeyCode.ENTER){
                        try {
                            Thread t= new Thread(new LearnerThread());
                            t.setDaemon(true);
                            t.start();
                            btn3.setDisable(true);
                            dialog(primaryStage,"Learner");
                        } catch (Exception ex) {
                        Logger.getLogger(HTRingPaxos.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            });
        BorderPane root=new BorderPane();
        root.setTop(l1);
        l1.setMinHeight(100);
        BorderPane.setAlignment(l1,Pos.BOTTOM_CENTER);
        HBox f2 = new HBox(20);
        f2.getChildren().addAll(btn1,btn2,btn3);
        f2.setAlignment(Pos.TOP_CENTER);
        root.setCenter(f2);
        Scene scene = new Scene(root, 700, 250);
        root.prefHeightProperty().bind(scene.heightProperty());
        root.prefWidthProperty().bind(scene.widthProperty());
        primaryStage.setTitle("HT-Ring Paxos");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public HTRingPaxos() {
    }
    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        launch(args);
        }

    /**
     *
     */
    protected static int a_num=-1,a_total=-1;
    String textString=null;
    synchronized void waitting() throws InterruptedException{
        wait();
    }
    synchronized void notifying() throws InterruptedException{
        notifyAll();
    }
    private void dialog(Stage s,String str){
        Stage dialog=new Stage();
        dialog.initOwner(s);
        dialog.initModality(Modality.APPLICATION_MODAL);
        Label l2=new Label(str+" Launched");
        l2.setTextFill(Color.BROWN);
        l2.setFont(Font.font(18));
        Button btn4=new Button("OK");
        btn4.setMinSize(80,30);
        final TextField text1=new TextField();
        final TextField text2=new TextField();
        //For setting Acceptor number
        if (str.equals("Acceptor")){
            text2.setDisable(true);
            btn4.setDisable(true);
            text1.setPrefColumnCount(60);
            text1.setPromptText("Enter Total Number of Acceptors");
            text1.setStyle("-fx-prompt-text-fill:derive(-fx-control-inner-background,-30%);}");
            text1.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent ae) {
                textString=text1.getText();
                a_total=Integer.parseInt(textString);
                text1.setDisable(true);
                text2.setDisable(false);
                text2.setPrefColumnCount(60);
                text2.setPromptText("Enter Acceptor Number: Range(0 to "+(a_total-1)+")");
                text2.setStyle("-fx-prompt-text-fill:derive(-fx-control-inner-background,-30%);}");
                }
            });
            text2.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent ae) {
                    textString=text2.getText();
                    a_num=Integer.parseInt(textString);
                    text2.setDisable(true);
                    btn4.setDisable(false);
                }
            });
            VBox vb=new VBox();
            vb.getChildren().add(text1);
            vb.getChildren().add(text2);
            vb.getChildren().add(btn4);
            vb.setMaxWidth(250);
            vb.setAlignment(Pos.TOP_CENTER);
            vb.setSpacing(15);
            BorderPane root = new BorderPane();
            root.setTop(l2);
            l2.setMinHeight(70);
            BorderPane.setAlignment(l2,Pos.BOTTOM_CENTER);
            root.setCenter(vb);
            BorderPane.setAlignment(vb,Pos.TOP_CENTER);
            Scene scene1 = new Scene(root, 500, 200);
            dialog.setTitle(str+" Says....");
            dialog.setScene(scene1);
            dialog.show();
        }else{
            BorderPane root = new BorderPane();
            root.setTop(l2);
            l2.setMinHeight(100);
            BorderPane.setAlignment(l2,Pos.BOTTOM_CENTER);
            root.setCenter(btn4);
            BorderPane.setAlignment(btn4,Pos.TOP_CENTER);
            Scene scene1 = new Scene(root, 400, 200);
            dialog.setTitle(str+" Says....");
            dialog.setScene(scene1);
            dialog.show();
            }
        btn4.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    dialog.close();
                }
            });
        btn4.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    if(event.getCode()==KeyCode.ENTER)
                    dialog.close();
                }
            });
    }
}