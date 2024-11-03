package sma;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiEvent;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sma.agents.acheteurAgent;
import sma.agents.clientAgent;
import sma.agents.ecomAgent;
import java.util.*;
public class clientContainer extends Application {
	
	   private clientAgent agentinterface;
	   public List<String> argumentsList = new ArrayList<>();

	    public clientAgent getAgentinterface() {
		return agentinterface;
	}

	public void setAgentinterface(clientAgent agentinterface) {
		this.agentinterface = agentinterface;
	}

		ObservableList<String> observableList = null;

	    private static String[] products = {
	        "{nomDeProduit=Lipstick,  quantite=4, startDate=05/01/2024, endDate=07/01/2024, prix=500}",
	        "{nomDeProduit=Lipstick,  quantite=6, startDate=09/01/2024, endDate=13/01/2024, prix=600}",
	        "{nomDeProduit=Lipstick,  quantite=7, startDate=05/01/2024, endDate=07/01/2024, prix=700}"
	    };

	    public static void main(String[] args) {
	        launch(clientContainer.class);
	    }

	    public void startContainer() {
	        try {
	        	
	            Runtime runtime = Runtime.instance();
	            ProfileImpl profileImpl = new ProfileImpl();
	            profileImpl.setParameter(ProfileImpl.MAIN_HOST, "localhost");
	            AgentContainer agentContainer = runtime.createAgentContainer(profileImpl);
	            int i = 0;
	            for (String argument : products) {
	                AgentController controller = agentContainer.createNewAgent("ecom[" + i++ + "]", ecomAgent.class.getName(), 
	                		new Object[]{
	                    argument
	                });
	                controller.start();
	            }
	            AgentController acheteurAgentController = agentContainer.createNewAgent("acheteur", acheteurAgent.class.getName(), new Object[]{});
	            acheteurAgentController.start();
	            AgentController clientAgentController = agentContainer.createNewAgent("client", clientAgent.class.getName(), new Object[]{
	                this
	            });
	            argumentsList.add("{nomDeProduit=Lipstick,  quantite=4, startDate=05/01/2024, endDate=07/01/2024, prix=500}");
	            argumentsList.add("{nomDeProduit=Lipstick,  quantite=6, startDate=09/01/2024, endDate=13/01/2024, prix=600}");
	            argumentsList.add("{nomDeProduit=Lipstick,  quantite=7, startDate=05/01/2024, endDate=07/01/2024, prix=700}");

	            clientAgentController.start();
	            
	            System.out.println("Agents Container Started!");
	        } catch (ControllerException e) {
	            e.printStackTrace();
	        }
	    }

	    

	    
	    public void show(String message) {
	        this.observableList.add(0, message);
	        
	    }

		@Override
		public void start(Stage arg0) throws Exception {
			  startContainer();
		        arg0.setTitle("Agent Interface");
		        BorderPane borderpane = new BorderPane();
		        VBox vbox = new VBox();
		        HBox hbox1 = new HBox();
		        hbox1.setPadding(new Insets(10, 0, 10, 30));
		        hbox1.setSpacing(105);
		        javafx.scene.control.Label labelproduit = new javafx.scene.control.Label("Produit :");
		        javafx.scene.control.TextField textproduit = new javafx.scene.control.TextField();
		        hbox1.getChildren().add(labelproduit);
		        hbox1.getChildren().add(textproduit);
		        HBox hbox2 = new HBox();
		        hbox2.setPadding(new Insets(0, 0, 10, 30));
		        hbox2.setSpacing(100);
		        javafx.scene.control.Label labelprice = new javafx.scene.control.Label("Prix :");
		        javafx.scene.control.TextField textprice = new javafx.scene.control.TextField();
		        hbox2.getChildren().add(labelprice);
		        hbox2.getChildren().add(textprice);
		        HBox hbox4 = new HBox();
		        hbox4.setPadding(new Insets(0, 0, 10, 30));
		        hbox4.setSpacing(7);
		        javafx.scene.control.Label labelqt = new javafx.scene.control.Label("Quantite :");
		        javafx.scene.control.TextField textqt = new javafx.scene.control.TextField();
		        hbox4.getChildren().add(labelqt);
		        hbox4.getChildren().add(textqt);
		        HBox hbox5 = new HBox();
		        hbox5.setPadding(new Insets(0, 0, 10, 30));
		        hbox5.setSpacing(45);
		        javafx.scene.control.Label labeldated = new javafx.scene.control.Label("Date de debut :");
		        javafx.scene.control.TextField textdated = new javafx.scene.control.TextField();
		        hbox5.getChildren().add(labeldated);
		        hbox5.getChildren().add(textdated);
		        HBox hbox6 = new HBox();
		        hbox6.setPadding(new Insets(0, 0, 10, 30));
		        hbox6.setSpacing(60);
		        javafx.scene.control.Label labeldatef = new javafx.scene.control.Label("Date de fin :");
		        javafx.scene.control.TextField textdatef = new javafx.scene.control.TextField();
		        hbox6.getChildren().add(labeldatef);
		        hbox6.getChildren().add(textdatef);
		        HBox hbox7 = new HBox();
		        hbox7.setPadding(new Insets(0, 0, 0, 160));
		        hbox7.setSpacing(40);
		        Button btn = new Button("Acheter");
		        hbox7.getChildren().add(btn);
		        GridPane gridPane = new GridPane();
		        observableList = FXCollections.observableArrayList();
		        ListView<String> listView = new ListView<String>(observableList);
		        gridPane.add(listView, 0, 0);
		        gridPane.setPadding(new Insets(10, 0, 10, 80));
		        vbox.getChildren().addAll(hbox1, hbox2, hbox4, hbox5, hbox6, hbox7, gridPane);
		        borderpane.setCenter(vbox);
		        Scene scene = new Scene(borderpane, 400, 600);
		        arg0.setScene(scene);
		        arg0.show();

		        btn.setOnAction(new EventHandler<ActionEvent>() {
		          

					@Override
					public void handle(ActionEvent arg0) {
						String produit = textproduit.getText();
		                String price = textprice.getText();
		                String qnt = textqt.getText();
		                String datad = textdated.getText();
		                String datef = textdatef.getText();
		                GuiEvent guiEvent = new GuiEvent(this, 1);
		                guiEvent.addParameter(produit);
		                guiEvent.addParameter(price);
		                guiEvent.addParameter(qnt);
		                guiEvent.addParameter(datad);
		                guiEvent.addParameter(datef);
		                agentinterface.onGuiEvent(guiEvent);						
					}
		        });
			
		}
		 public void updateQuantity(int ecomIndex, int newQuantity) {
		        if (ecomIndex >= 0 && ecomIndex < argumentsList.size()) {
		            String argument = argumentsList.get(ecomIndex);
		          
		            argument = argument.replace("quantite=" + getCurrentQuantity(ecomIndex), "quantite=" + newQuantity);
		            argumentsList.set(ecomIndex, argument);
		        }
		    }

		    private int getCurrentQuantity(int ecomIndex) {
		       
		        String argument = argumentsList.get(ecomIndex);
		        int index = argument.indexOf("quantite=");
		        int endIndex = argument.indexOf(",", index);
		        if (index != -1 && endIndex != -1) {
		            String quantityStr = argument.substring(index + 9, endIndex);
		            return Integer.parseInt(quantityStr);
		        }
		        return -1;
		    }
}
