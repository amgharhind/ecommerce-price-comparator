package sma.agents;

import java.util.*;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.ControllerException;
import sma.clientContainer;

public class acheteurAgent extends Agent {
	 private int nombreEcom = 0;
	    private AID client = null;
	    private AID meilleurAID;
	    private DFAgentDescription[] result = null;
	    private Map<String, String> produitAChercher = null;
	    private Map<String, String> meilleurChoix = null;
	    private List<String> produits = new ArrayList<>();
	    private List<AID> envoyeurs = new ArrayList<>();
	    
	    CyclicBehaviour clientAgent = new CyclicBehaviour() {
			
			@Override
			public void action() {
				 MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
		         ACLMessage message = this.myAgent.receive(messageTemplate);
		         if (message != null) {
		                if (message.getPerformative() == ACLMessage.REQUEST) {
		                	produitAChercher = stringToHashMap(message.getContent());
		                    client = message.getSender();
		                    sendCFPMessage(result);
		                }
		            } else {
		                this.block();
		            }
		        }
			
		};
		 CyclicBehaviour ecomAgent = new CyclicBehaviour() {
		        @Override
		        public void action() {
		            if (clientAgent != null) {
		                MessageTemplate messageTemplate = MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.PROPOSE), MessageTemplate.MatchPerformative(ACLMessage.CONFIRM));
		                ACLMessage message = this.myAgent.receive(messageTemplate);
		                if (message != null) {
		                    switch (message.getPerformative()) {
		                        case ACLMessage.PROPOSE:
		    		                System.out.println(message.getContent() + " i am in acheteur propose");

		                            ACLMessage reply = message.createReply();
		                            produits.add(message.getContent());
		                            envoyeurs.add(message.getSender());
		                            if (produits.size() == nombreEcom) {
		                                 meilleurAID = getMeilleurAID();
		                                for (AID agentID : envoyeurs) {
		                                    ACLMessage messageCFP = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
		                                    if (agentID == meilleurAID) {
		                                        messageCFP = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
		                                    }
		                                    messageCFP.addReceiver(agentID);
		                                    this.myAgent.send(messageCFP);
		                                }
		                                this.myAgent.send(reply);
		                                if (meilleurAID == null) {
		                                    ACLMessage messageInform = new ACLMessage(ACLMessage.INFORM);
		                                    messageInform.setContent("");
		                                    messageInform.addReceiver(client);
		                                    this.myAgent.send(messageInform);
		                                }
		                            }
		                            break;
		                        case ACLMessage.CONFIRM:
		                            ACLMessage messageInform = new ACLMessage(ACLMessage.INFORM);
		                            //messageInform.setContent(invertMapToString(meilleurChoix));
		                            messageInform.setContent(meilleurChoix.get("prix")+" DH  delai "+ meilleurChoix.get("startDate")+"      " + meilleurChoix.get("endDate") );
		                            messageInform.addReceiver(client);
		                            this.myAgent.send(messageInform);
		                            System.out.println(message != null ?  message.getSender().getLocalName().replace("ecom[", "").replace("]", ""): " test");
		                           // System.out.println(" the meilleur choi content quantity " + meilleurChoix.get("quantite") );
		                           // System.out.println(  " the messae content " +message.getContent());
		                           // System.out.println(" the messae content quantity " +stringToHashMap(message.getContent()).get("qnt") );


		                            //int ecomIndex = Integer.parseInt(message.getSender().getLocalName().replace("ecom[", "").replace("]", ""));
		                           // int newQuantity =Integer.parseInt(meilleurChoix.get("quantite") ) -  Integer.parseInt(stringToHashMap(message.getContent()).get("quantite"));
								/*try {
									((clientContainer) getContainerController().getAgent("client")).updateQuantity(ecomIndex, newQuantity);
								} catch (ControllerException e) {
									e.printStackTrace();
								}*/
								


		                            produitAChercher = null;
		                            produits = new ArrayList();
		                            envoyeurs = new ArrayList<>();
		                            meilleurChoix = null;
		                            client = null;
		                            break;
		                    }
		                } else {
		                    this.block();
		                }
		            }
		        }
		    };
		    
	
		private Map<String, String> stringToHashMap(String strMap) {
			 Map<String, String> map = new HashMap<>();
		        String[] pairs = strMap.substring(1, strMap.length() - 1).split(", ");
		        for (int i = 0; i < pairs.length; i++) {
		            map.put(pairs[i].split("=")[0], pairs[i].split("=")[1]);
		        }
		        return map;
		}
		 public void sendCFPMessage(DFAgentDescription[] result) {
		        ACLMessage messageCFP = new ACLMessage(ACLMessage.CFP);
		        messageCFP.setContent("Demande des offres disponible");
		        for (DFAgentDescription description : result) {
		            messageCFP.addReceiver(description.getName());
		        }
		        this.send(messageCFP);
		    }
		 
		  public AID getMeilleurAID() {
		        int meilleurIndex = -1;
		        for (int i = 0; i < produits.size(); i++) {
		            Map<String, String> produit = stringToHashMap(produits.get(i));
		            System.out.println("Product " + i + ": " + produit);
                     System.out.println(produitAChercher.get("prix"));
		            if (produit.get("prix").equals(produitAChercher.get("prix"))) {
		                meilleurIndex = i;
		                meilleurChoix = produit;
		                
		                

		            }
		        }
		        return (meilleurIndex == -1) ? null : envoyeurs.get(meilleurIndex);
		    }
		  
		  public DFAgentDescription[] getEcomListFromDF() {
		        DFAgentDescription ecomDescription = new DFAgentDescription();
		        ServiceDescription ecomServiceDescription = new ServiceDescription();
		        ecomServiceDescription.setType("Commerce-en-Ligne");
		        ecomServiceDescription.setName("Commerce en Ligne");
		        ecomDescription.addServices(ecomServiceDescription);

		        DFAgentDescription[] result = null;
		        try {
		            result = DFService.search(this, ecomDescription);
		        } catch (FIPAException e) {
		            e.printStackTrace();
		        }

		        return result;
		    }
		  
		  @Override
		protected void setup() {
			  this.registreAcheteurinDF();
		        this.result = this.getEcomListFromDF();
		        this.nombreEcom = this.result.length;
		        this.addBehaviour(clientAgent);
		        this.addBehaviour(ecomAgent);
		        System.out.println("Agent est démarré: " + this.getAID().getName());
			
		}
		  private void registreAcheteurinDF() {
			  DFAgentDescription description = new DFAgentDescription();
		        description.setName(getAID());
		        ServiceDescription serviceDescription = new ServiceDescription();
		        serviceDescription.setType("acheteur");
		        serviceDescription.setName("acheteur");
		        description.addServices(serviceDescription);
		        try {
		            DFService.register(this, description);
		        } catch (FIPAException e) {
		            e.printStackTrace();
		        }			
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
}
