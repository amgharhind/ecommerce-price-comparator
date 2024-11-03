package sma.agents;

import java.util.HashMap;
import java.util.Map;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;


public class ecomAgent extends Agent {
    private String offre = null;
@Override
protected void setup() {
	this.populateProduits();
    this.registerEcomInDF();
    this.addBehaviour(new CyclicBehaviour() {
		@Override
		public void action() {
			 MessageTemplate messageTemplate = MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.CFP),
					 MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL), 
							 MessageTemplate.MatchPerformative(ACLMessage.REJECT_PROPOSAL)));
	            ACLMessage message = this.myAgent.receive(messageTemplate);
	            
	            if (message != null) {
	                switch (message.getPerformative()) {
	                    case ACLMessage.CFP:
	                        if (message.getContent().equals("Demande des offres disponible")) {
	                            ACLMessage reply = message.createReply();
	                            reply.setPerformative(ACLMessage.PROPOSE);
	                            reply.setContent(offre);
	                            this.myAgent.send(reply); }
	                        break;
	                    case ACLMessage.ACCEPT_PROPOSAL:	                  
	                        ACLMessage reply = message.createReply();
	                        reply.setPerformative(ACLMessage.CONFIRM);
	                        this.myAgent.send(reply);
	                        break;
	                    case ACLMessage.REJECT_PROPOSAL:
	                        break;}
	            } else {
	                this.block();
	            }
	            
		}

		

	});
    System.out.println("Agent est démarré: " + this.getAID().getName());

    
}

@Override
public void takeDown() {
    try {
        DFService.deregister(this);
        System.out.println("Agent est fini: " + this.getAID().getName());
    } catch (FIPAException e) {
        e.printStackTrace();
    }
}

public void populateProduits() {
    Object[] arguments = this.getArguments();
    this.offre = (String)arguments[0];
}



public void registerEcomInDF() {
    DFAgentDescription description = new DFAgentDescription();
    description.setName(getAID());
    ServiceDescription serviceDescription = new ServiceDescription();
    //serviceDescription.setType("ecom");
    //serviceDescription.setName("ecom");
   serviceDescription.setType("Commerce-en-Ligne");
   serviceDescription.setName("Commerce en Ligne");
    description.addServices(serviceDescription);
    try {
        DFService.register(this, description);

        
    } catch (FIPAException e) {
        e.printStackTrace();
    }
}


private Map<String, String> stringToHashMap(String strMap) {
	 Map<String, String> map = new HashMap<>();
       String[] pairs = strMap.substring(1, strMap.length() - 1).split(", ");
       for (int i = 0; i < pairs.length; i++) {
           map.put(pairs[i].split("=")[0], pairs[i].split("=")[1]);
       }
       return map;
}
}
