package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Controller extends Application {

    BufferedReader reader;
    PrintWriter writer;
    Socket sock;

    // Getting system username
    final String userName  = System.getProperty("user.name");

    @FXML
    Label user;

    @FXML
    TextField outgoing;

    @FXML
    TextArea incoming;

    @FXML
    Button sendButton;
    @FXML
    Button minimizeIcon;

    // initialize() - life cycle method
    public void initialize(){
        // Setting Username
        user.setText(userName);

        // Setting up network
        setUpNetworking();

        // Starting the Thread
        Thread readerThread = new Thread(new IncomingReader());
        readerThread.start();

        // Handling ENTER Key
        outgoing.setOnKeyPressed(key -> {
            if (key.getCode().equals(KeyCode.ENTER))
            {
                send();
            }
        });

        minimizeIcon.setOnMouseClicked( event -> {
            Stage obj = (Stage) minimizeIcon.getScene().getWindow();
            obj.setIconified(true);
        });

    }

    public void send(){

        if(outgoing.getText().trim().length() > 0){
            try{
                writer.println(userName + ": " + outgoing.getText());
                writer.flush();
            }catch(Exception e){
                e.printStackTrace();
            }

            outgoing.setText("");

        }
        outgoing.requestFocus();
    }

    private void setUpNetworking(){
        try{
            sock = new Socket("Server Name is supposed to be here", "Port number is here");
            InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
            reader = new BufferedReader(streamReader);
            writer = new PrintWriter(sock.getOutputStream());
            System.out.println("Networking established");
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public class IncomingReader implements Runnable{
        @Override
        public void run() {
            String message;
            try{
                while((message = reader.readLine()) != null){
                    System.out.println("read " + message);
                    incoming.appendText(message+ "\n");
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    // Close Application
    public void closeApp(){
        System.exit(0);
        Platform.exit();

    }

    // stop() - life cycle method
    public void stop(){
        closeApp();
    }

    // Application interface stuff
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        root.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("Chat Client");
        primaryStage.setScene(new Scene(root, 400, 500));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
