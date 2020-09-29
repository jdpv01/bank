package ui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import model.Bank;

public class ActionsController {
	private Bank bank;
	private QueueController q;
	
	@FXML
	private Label clientName;

	@FXML
	private ToggleGroup actions;

	@FXML
	private RadioButton consignment;

	@FXML
	private RadioButton withdraw;

	@FXML
	private RadioButton cardPayment;

	@FXML
	private RadioButton cancellation;

	public ActionsController() {
		bank= new Bank();
		clientName.setText("");
	}
	
	@FXML
	public void initialize() {
		consignment.setToggleGroup(actions);
		withdraw.setToggleGroup(actions);
		cardPayment.setToggleGroup(actions);
		cancellation.setToggleGroup(actions);
	}

	@FXML
	void makeAction(ActionEvent event) throws IOException {
		if(consignment.isSelected()) {
			bank.deposit(null, null);
		}else if(withdraw.isSelected()) {
			bank.withdraw(null, null);
		}else if(cancellation.isSelected()) {
			bank.cancelAccount(null, null, null);
		}else if (cardPayment.isSelected()) {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CardPayment.fxml"));
			Scene scene= new Scene(fxmlLoader.load());
			Stage stage= new Stage();
			stage.getIcons().add(new Image(Main.class.getResourceAsStream("bank-flat.png")));
			stage.setTitle("Actions");
			stage.setScene(scene);
			stage.show();
		}
	}
	
	@FXML
	void back(ActionEvent event) {

	}

	public void consignment() {

	}

	public void withdraw() {

	}

	public void cancelation() {

	}

	public void setQ(QueueController q) {
		this.q = q;
	}
}
