package ui;

import java.io.IOException;

import heap.IHeap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import model.Bank;
import model.Client;
import queue.IQueue;

public class QueueController {
	private Bank bank;
	
    @FXML
    private ListView<String> priorityQueue;

    @FXML
    private ListView<String> normalQueue;
    
    public QueueController() {
    	bank= new Bank();
	}
    
    @FXML
    public void initialize() {
    	bank.data();
    	getPriorityQueue();
    	getNormalQueue();
    }
    
    @FXML
    void attendClient(ActionEvent event) throws IOException {
    	FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AttendClient.fxml"));
    	Scene scene= new Scene(fxmlLoader.load());
    	Stage stage= new Stage();
    	stage.getIcons().add(new Image(Main.class.getResourceAsStream("bank-flat.png")));
		stage.setTitle("Attend Client");
		stage.setResizable(false);
		stage.setScene(scene);
		stage.show();
    }

    @FXML
    void back(ActionEvent event) {
    }
    
    public void getSelectedClient() {
    	priorityQueue.getSelectionModel().getSelectedIndex();
    	normalQueue.getSelectionModel().getSelectedIndex();	
    	
    }
    
    public void getNormalQueue() {
    	for (Client client : bank.getClientQueue()) {
			normalQueue.getItems().add(client.getName());
		}
    }
    
    public void getPriorityQueue() {
    	IHeap<Client> h= new IHeap<Client>(100, true);
    	h= bank.getClientHeap();
    	while(!bank.getClientHeap().isEmpty()) {
    		priorityQueue.getItems().add(h.extract().getName());
    	}
    }

}