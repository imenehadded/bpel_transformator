package bpel.transformator;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class Action {
	private  String partnerLink;
	// invoke receive onmessage
	private  String action;
	// action name
	private  String operation;

	public Action() {
	}

	public Action(Node node) {
		this.action = node.getNodeName();

		NamedNodeMap attributes = node.getAttributes();
		Node partnerLinkAttr = attributes.getNamedItem("partnerLink");
		this.partnerLink = partnerLinkAttr.getNodeValue();

		Node operationAttr = attributes.getNamedItem("operation");
		this.operation = operationAttr.getNodeValue();

	}

	public String getPartnerLink() {
		return partnerLink;
	}

	public void setPartnerLink(String partnerLink) {
		this.partnerLink = partnerLink;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	
}