package sma.agents;

import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import javafx.application.Platform;
import sma.clientContainer;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import java.util.*;
public class clientAgent extends GuiAgent {
	 private clientContainer gui;
	   private  DFAgentDescription[] result = null;
	     GuiEvent guiEventGlobal = null;

	@Override
	protected void setup() {
		 gui = (clientContainer) getArguments()[0];
	     gui.setAgentinterface(this);

	        // Enregistrement de clientAgent dans DF
	        this.registreClientinDF();

	        // commander
	        this.result = this.commander();

	        // Interaction avec l'agent acheteur
	        this.addBehaviour(new CyclicBehaviour() {
	            @Override
	            public void action() {
	                MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);

	                ACLMessage message = this.myAgent.receive(messageTemplate);

	                if (message != null) {
	                    if (message.getPerformative() == ACLMessage.INFORM) {
	                        String content = message.getContent();

	                        Platform.runLater(new Runnable() {
	                            @Override
	                            public void run() {
	                                if (content.length() == 0) {
	                                    gui.show("Pas d'offre trouve!");
	                                } else {
	                                    gui.show("Meilleur offre trouve :  " + content );
	                                    String confirmedPrice = message.getContent();
	                                }
	                            }
	                        });
	                    }
	                } else {
	                    this.block();
	                }
	            }
	        });

	        System.out.println("Agent est démarré: " + this.getAID().getName());
	}
	@Override
	protected void takeDown() {
		 try {
	            DFService.deregister(this);
	            System.out.println("Agent est detruit: " + this.getAID().getName());
	        } catch (FIPAException e) {
	            e.printStackTrace();
	        }
	}
	@Override
	protected void beforeMove() {
		
	}
	@Override
	protected void afterMove() {
		
	}

	@Override
	public void onGuiEvent(GuiEvent guiEvent) {
		 if (guiEvent.getType() == 1) {
	            Map<String, String> offreCherche = new HashMap<>();
	            offreCherche.put("produit", guiEvent.getParameter(0).toString());
	            offreCherche.put("prix", guiEvent.getParameter(1).toString());
	            offreCherche.put("quantite", guiEvent.getParameter(2).toString());
	            offreCherche.put("startDate", guiEvent.getParameter(3).toString());
	            offreCherche.put("endDate", guiEvent.getParameter(4).toString());

	            // Envoyer le message de type REQUEST
	            this.sendRequestMessage(offreCherche);
	        }
		
	}

	
	 public void registreClientinDF() {
	        DFAgentDescription description = new DFAgentDescription();
	        
	        description.setName(getAID());
	        ServiceDescription serviceDescription = new ServiceDescription();
			serviceDescription.setType("transaction");
			serviceDescription.setName("client");
			description.addServices(serviceDescription);
	        

	        try {
	            DFService.register(this, description);
	        } catch (FIPAException e) {
	            e.printStackTrace();
	        }
	    }

	    public DFAgentDescription[] commander() {
	        DFAgentDescription ecomDescription = new DFAgentDescription();
	        ServiceDescription ecomServiceDescription = new ServiceDescription();
	        ecomServiceDescription.setType("acheteur");
	        ecomServiceDescription.setName("acheteur");
	        ecomDescription.addServices(ecomServiceDescription);

	        DFAgentDescription[] result = null;
	        try {
	            result = DFService.search(this, ecomDescription);
	        } catch (FIPAException e) {
	            e.printStackTrace();
	        }

	        return result;
	    }

	    public void sendRequestMessage(Map<String, String> offreCherche) {
	        ACLMessage messageREQUEST = new ACLMessage(ACLMessage.REQUEST);

	        try {
	            messageREQUEST.setContent(offreCherche.toString());
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	        for (int i = 0; i < this.result.length; i++) {
	            messageREQUEST.addReceiver(this.result[i].getName());
	        }

	        this.send(messageREQUEST);
	    }
}
