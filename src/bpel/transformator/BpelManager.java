package bpel.transformator;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class BpelManager extends BaseXml {

	private Document xmlDocument;

	public BpelManager(String bpel_file) throws ParserConfigurationException, SAXException, IOException {

		this.xmlDocument = this.loads(bpel_file);

	}

	public String get_process_name() {

		Node node = this.xmlDocument.getFirstChild();

		NamedNodeMap attributes = node.getAttributes();
		Node attribute = attributes.getNamedItem("name");
		return attribute.getNodeValue();

	}

	private String tag(String tag_name) {

		String prefix = this.get_xmlns(this.xmlDocument, "http://docs.oasis-open.org/wsbpel/2.0/process/executable");

		return prefix + tag_name;
	}

	public void import_monitor_wsdl(String expression, String monitor_wsdl_file)
			throws ParserConfigurationException, SAXException, IOException {

		// 2- import monitor wsdl in bpel file as external file

		MonitorWsdlManager monitorManager = new MonitorWsdlManager(monitor_wsdl_file);

		boolean is_imported = this.is_monitor_import_exist(monitorManager);

		if (is_imported == false) {

			String monitor_ns = monitorManager.get_namespace();

			Element monitorwsdlImportNode = this.xmlDocument.createElement(this.tag("import"));
			this.setAttributeNode(monitorwsdlImportNode, "location", MonitorWsdlManager.MONITOR_WSDL_FILE_NAME);
			this.setAttributeNode(monitorwsdlImportNode, "namespace", monitor_ns);
			this.setAttributeNode(monitorwsdlImportNode, "importType", "http://schemas.xmlsoap.org/wsdl/");

			Node import_node = this.xmlDocument.getElementsByTagName(this.tag("import")).item(0);
			Node node = this.xmlDocument.getFirstChild();
			node.insertBefore(monitorwsdlImportNode, import_node);

			// adding monitor wsdl as namespace
			this.setAttributeNode(node, "xmlns:" + MonitorWsdlManager.MONITOR_WSDL_PREFIX, monitor_ns);
			this.setAttributeNode(node, "xmlns:xsd", "http://www.w3.org/2001/XMLSchema");

		}

		// 3- add monitor partner link in bpel file

		boolean is_parnerLink_added = this.is_monitor_partnerLink_exist();

		if (is_parnerLink_added == false) {

			Element monitorPartnerLinkNode = this.xmlDocument.createElement(this.tag("partnerLink"));
			this.setAttributeNode(monitorPartnerLinkNode, "name", MonitorWsdlManager.MONITOR_PARTNERLINK_NAME);

			String partnerLinkType = MonitorWsdlManager.MONITOR_WSDL_PREFIX + ":"
					+ MonitorWsdlManager.MONITOR_PARTNERLINK_NAME;
			this.setAttributeNode(monitorPartnerLinkNode, "partnerLinkType", partnerLinkType);
			this.setAttributeNode(monitorPartnerLinkNode, "partnerRole", MonitorWsdlManager.MONITOR_ROLE_NAME);

			Node partnerLinksNode = this.xmlDocument.getElementsByTagName(this.tag("partnerLinks")).item(0);

			partnerLinksNode.appendChild(monitorPartnerLinkNode);

		}
		// 4- add request/response message as variables in bpel file

		this.remove_all_monitor_variables();
		this.remove_all_monitor_variables();
		this.remove_all_monitor_variables();

		String monitor_request_name = monitorManager.get_input_message_name();
		String monitor_response_name = monitorManager.get_output_message_name();

		Element requestVariableNode = this.xmlDocument.createElement(this.tag("variable"));
		this.setAttributeNode(requestVariableNode, "name", MonitorWsdlManager.MONITOR_REQUEST_VARAIBLE_NAME);
		this.setAttributeNode(requestVariableNode, "messageType",
				MonitorWsdlManager.MONITOR_WSDL_PREFIX + ":" + monitor_request_name);

		Element responseVariableNode = this.xmlDocument.createElement(this.tag("variable"));
		this.setAttributeNode(responseVariableNode, "name", MonitorWsdlManager.MONITOR_RESPONSE_VARAIBLE_NAME);
		this.setAttributeNode(responseVariableNode, "messageType",
				MonitorWsdlManager.MONITOR_WSDL_PREFIX + ":" + monitor_response_name);

		Element latestExpVariableNode = this.xmlDocument.createElement(this.tag("variable"));
		this.setAttributeNode(latestExpVariableNode, "name", MonitorWsdlManager.MONITOR_LATEST_EXPRESSION);
		this.setAttributeNode(latestExpVariableNode, "type", "xsd:string");

		Element fromVariableNode = this.xmlDocument.createElement(this.tag("from"));

		Element literalVariableNode = this.xmlDocument.createElement(this.tag("literal"));
		this.setAttributeNode(literalVariableNode, "xml:space", "preserve");
		literalVariableNode.setTextContent(expression);
		fromVariableNode.appendChild(literalVariableNode);
		latestExpVariableNode.appendChild(fromVariableNode);

		Node variablesNode = this.xmlDocument.getElementsByTagName(this.tag("variables")).item(0);

		variablesNode.appendChild(latestExpVariableNode);
		variablesNode.appendChild(requestVariableNode);
		variablesNode.appendChild(responseVariableNode);

	}

	private boolean is_monitor_partnerLink_exist() {

		Node partnerLinksNode = this.xmlDocument.getElementsByTagName(this.tag("partnerLinks")).item(0);

		NodeList nodeList = ((Element) partnerLinksNode).getElementsByTagName(this.tag("partnerLink"));

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node iNode = nodeList.item(i);

			String att_name = iNode.getAttributes().getNamedItem("name").getNodeValue();
			if (att_name.equals(MonitorWsdlManager.MONITOR_PARTNERLINK_NAME))
				return true;

		}
		return false;

	}

	private void remove_all_monitor_variables() {

		Element variablesNode = (Element) this.xmlDocument.getElementsByTagName(this.tag("variables")).item(0);

		NodeList nodeList = variablesNode.getElementsByTagName(this.tag("variable"));

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node iNode = nodeList.item(i);

			String variableName = iNode.getAttributes().getNamedItem("name").getNodeValue();

			if (variableName.equals(MonitorWsdlManager.MONITOR_REQUEST_VARAIBLE_NAME)
					|| variableName.equals(MonitorWsdlManager.MONITOR_RESPONSE_VARAIBLE_NAME)
					|| variableName.equals(MonitorWsdlManager.MONITOR_LATEST_EXPRESSION))

				variablesNode.removeChild(iNode);

		}
	}

	private boolean is_monitor_import_exist(MonitorWsdlManager monitorManager) {

		String monitor_ns = monitorManager.get_namespace();

		NodeList nodeList = this.xmlDocument.getElementsByTagName(this.tag("import"));

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node iNode = nodeList.item(i);
			String ns = iNode.getAttributes().getNamedItem("namespace").getNodeValue();
			if (ns.equals(monitor_ns))
				return true;

		}
		return false;
	}

	/*
	 * private Node getActionNodeByName(String operation,String action) {
	 * 
	 * NodeList list =
	 * this.xmlDocument.getElementsByTagName(this.tag("invoke"));
	 * System.out.println(list.getLength() + " INVOKE FOUND"); for (int i = 0; i
	 * < list.getLength(); i++) { Node node = list.item(i); NamedNodeMap
	 * attributes = node.getAttributes(); String attrName =
	 * attributes.getNamedItem("name").getNodeValue(); if
	 * (attrName.equals(action)) { System.out.println("NODE FOUND " + attrName);
	 * return node; } }
	 * 
	 * return null;
	 * 
	 * }
	 */

	public void setMonitorService(MonitorWsdlManager monitorManager, String action, String partnerlink,
			String operation, int index) {

		System.out.println("IN ADDING MONITOR SERVICE WITH ACTION : " + operation);

		Node actionNode = this.get_Node(action, partnerlink, operation);
		/*
		 * NamedNodeMap attributes = actionNode.getAttributes(); String
		 * action_name = attributes.getNamedItem("name").getNodeValue();
		 */

		String assignMonitorInputName = "assignMonitorInputTo" + action + "_" + index;
		Node assignMonitorInputNode = this.getAssignMonitorInput(monitorManager, assignMonitorInputName, action);

		String invokeMonitorName = "invokeMonitor_" + action + "_" + index;
		Node invokeMonitorNode = this.getInvokeMonitorNode(monitorManager, invokeMonitorName);

		String assignMonitorOutputName = "assignMonitorOutputTo" + action + "_" + index;
		Node assignMonitorOutputNode = this.getAssignMonitorOutput(assignMonitorOutputName);

		String exitConditionName = "condition_" + action + "_" + index;
		Node exitcondtionNode = this.getExitCondition(exitConditionName);

		// Insert before
		Node parentNode = actionNode.getParentNode();

		if (action.equals("invoke") || action.equals("reply")) {

			System.out.println(" INSERT BEFORE ********** ");

			parentNode.insertBefore(exitcondtionNode, actionNode);

		} else if (action.equals("receive")) {
			// Insert after
			parentNode.insertBefore(exitcondtionNode, actionNode.getNextSibling());
		}

		parentNode.insertBefore(assignMonitorOutputNode, exitcondtionNode);

		parentNode.insertBefore(invokeMonitorNode, assignMonitorOutputNode);

		parentNode.insertBefore(assignMonitorInputNode, invokeMonitorNode);

	}

	private Node getAssignMonitorOutput(String assignName) {

		String part = "parameters";
		String return_name = "return";

		Element assignNode = this.xmlDocument.createElement(this.tag("assign"));
		this.setAttributeNode(assignNode, "validate", "no");
		this.setAttributeNode(assignNode, "name", assignName);

		Element copyNode = this.xmlDocument.createElement(this.tag("copy"));

		Element fromNode = this.xmlDocument.createElement(this.tag("from"));
		this.setAttributeNode(fromNode, "part", part);
		this.setAttributeNode(fromNode, "variable", MonitorWsdlManager.MONITOR_RESPONSE_VARAIBLE_NAME);

		Element queryNode = this.xmlDocument.createElement(this.tag("query"));
		this.setAttributeNode(queryNode, "queryLanguage", "urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0");
		Node cdata = this.xmlDocument.createCDATASection(return_name);
		queryNode.appendChild(cdata);

		fromNode.appendChild(queryNode);

		Element toNode = this.xmlDocument.createElement(this.tag("to"));
		this.setAttributeNode(toNode, "variable", MonitorWsdlManager.MONITOR_LATEST_EXPRESSION);

		copyNode.appendChild(toNode);
		copyNode.insertBefore(fromNode, toNode);

		assignNode.appendChild(copyNode);
		return assignNode;
	}

	private Node getAssignMonitorInput(MonitorWsdlManager monitorManager, String assignName, String actionName) {

		Element assignNode = this.xmlDocument.createElement(this.tag("assign"));
		this.setAttributeNode(assignNode, "name", assignName);

		// TODO extract them from wsdl monitor file
		String part = "parameters";
		String arg0 = "arg0";
		String arg1 = "arg1";

		// ########################## ASSIGN INIT ARGS
		Element literalNode = this.xmlDocument.createElement(this.tag("literal"));
		String service_name = MonitorWsdlManager.MONITOR_WSDL_PREFIX + ":" + monitorManager.get_output_message_name();
		Element serviceNode = this.xmlDocument.createElement(service_name);

		Node arg0Node = this.xmlDocument.createElement(arg0);
		Node arg1Node = this.xmlDocument.createElement(arg1);

		serviceNode.appendChild(arg0Node);
		serviceNode.appendChild(arg1Node);

		literalNode.appendChild(serviceNode);

		Element fromNode = this.xmlDocument.createElement(this.tag("from"));
		fromNode.appendChild(literalNode);

		Element toNode = this.xmlDocument.createElement(this.tag("to"));
		this.setAttributeNode(toNode, "part", part);
		this.setAttributeNode(toNode, "variable", MonitorWsdlManager.MONITOR_REQUEST_VARAIBLE_NAME);

		Element copyNode = this.xmlDocument.createElement(this.tag("copy"));

		copyNode.appendChild(toNode);
		copyNode.insertBefore(fromNode, toNode);

		assignNode.appendChild(copyNode);

		// ########################## ASSIGN ARG 0
		Element copyArg0Node = this.xmlDocument.createElement(this.tag("copy"));
		Element fromArg0Node = this.xmlDocument.createElement(this.tag("from"));
		this.setAttributeNode(fromArg0Node, "variable", MonitorWsdlManager.MONITOR_LATEST_EXPRESSION);

		Element toArg0Node = this.xmlDocument.createElement(this.tag("to"));
		String arg0Totext = "$" + MonitorWsdlManager.MONITOR_REQUEST_VARAIBLE_NAME + "." + part + "/" + arg0;
		toArg0Node.setTextContent(arg0Totext);

		copyArg0Node.appendChild(toArg0Node);
		copyArg0Node.insertBefore(fromArg0Node, toArg0Node);

		assignNode.appendChild(copyArg0Node);

		// ########################## ASSIGN ARG 1
		Element copyArg1Node = this.xmlDocument.createElement(this.tag("copy"));
		Element fromArg1Node = this.xmlDocument.createElement(this.tag("from"));

		Element toArg1Node = this.xmlDocument.createElement(this.tag("to"));

		Element literalArg1Node = this.xmlDocument.createElement(this.tag("literal"));

		this.setAttributeNode(literalArg1Node, "xml:space", "preserve");
		literalArg1Node.setTextContent(actionName);

		fromArg1Node.appendChild(literalArg1Node);

		String arg1Totext = "$" + MonitorWsdlManager.MONITOR_REQUEST_VARAIBLE_NAME + "." + part + "/" + arg1;
		toArg1Node.setTextContent(arg1Totext);

		copyArg1Node.appendChild(toArg1Node);
		copyArg1Node.insertBefore(fromArg1Node, toArg1Node);

		assignNode.appendChild(copyArg1Node);

		return assignNode;

	}

	private Node getInvokeMonitorNode(MonitorWsdlManager monitorManager, String invokeName) {

		String inputVariable = MonitorWsdlManager.MONITOR_REQUEST_VARAIBLE_NAME;
		String operation = monitorManager.get_operation_name();
		String outputVariable = MonitorWsdlManager.MONITOR_RESPONSE_VARAIBLE_NAME;
		String partnerLink = MonitorWsdlManager.MONITOR_PARTNERLINK_NAME;
		String portType = MonitorWsdlManager.MONITOR_WSDL_PREFIX + ":" + monitorManager.get_portType_name();

		Element monitorInvok = this.xmlDocument.createElement(this.tag("invoke"));
		this.setAttributeNode(monitorInvok, "inputVariable", inputVariable);
		this.setAttributeNode(monitorInvok, "name", invokeName);
		this.setAttributeNode(monitorInvok, "operation", operation);
		this.setAttributeNode(monitorInvok, "outputVariable", outputVariable);
		this.setAttributeNode(monitorInvok, "partnerLink", partnerLink);
		this.setAttributeNode(monitorInvok, "portType", portType);

		return monitorInvok;
	}

	private Node getExitCondition(String conditionName) {

		/*
		 * <bpel:if name="monitorCondition"> <bpel:condition
		 * expressionLanguage="urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0">
		 * <![CDATA[$monitorLatestExpression = 0]]> </bpel:condition> <bpel:exit
		 * name="Exit"></bpel:exit> </bpel:if>
		 */

		Element ifNode = this.xmlDocument.createElement(this.tag("if"));
		this.setAttributeNode(ifNode, "name", conditionName);

		Element conditionNode = this.xmlDocument.createElement(this.tag("condition"));
		this.setAttributeNode(conditionNode, "expressionLanguage", "urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0");

		Node cdata = this.xmlDocument.createCDATASection("$" + MonitorWsdlManager.MONITOR_LATEST_EXPRESSION + " = 0");
		conditionNode.appendChild(cdata);

		Element exitNode = this.xmlDocument.createElement(this.tag("exit"));
		this.setAttributeNode(ifNode, "name", "Exit");

		ifNode.appendChild(conditionNode);
		ifNode.appendChild(exitNode);

		return ifNode;

	}

	@Override
	public void dumps(String output_file) {

		super.dumps(this.xmlDocument, output_file);

	}

	private Node get_Node(String action, String partnerlink, String operation) {

		NodeList listNodes = this.xmlDocument.getElementsByTagName(this.tag(action));

		for (int i = 0; i < listNodes.getLength(); i++) {

			Node node = listNodes.item(i);

			NamedNodeMap attributes = node.getAttributes();
			Node partnerLinkAttr = attributes.getNamedItem("partnerLink");

			if (partnerLinkAttr == null)
				continue;

			String value = partnerLinkAttr.getNodeValue();

			if (value.toLowerCase().equals(partnerlink.toLowerCase()) == false)
				continue;

			Node operationNode = attributes.getNamedItem("operation");
			if (operationNode == null)
				continue;

			value = operationNode.getNodeValue();

			if (value.toLowerCase().equals(operation.toLowerCase()) == false)
				continue;
			System.out.println("IN ALL ACTIONS LOOP OPERATION NAME  : " + value);
			return node;
		}

		return null;
	}

	public String get_action_name(String action, String partnerlink, String service_name) {

		Node node = this.get_Node(action, partnerlink, service_name);

		if (node == null)
			return null;

		NamedNodeMap attributes = node.getAttributes();
		return attributes.getNamedItem("name").getNodeValue();

	}

	public String get_content_without_assign() throws TransformerException {

		Node firstNode = this.xmlDocument.getFirstChild();

		this.remove_assign_from_children(firstNode);

		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer;

		transformer = tf.newTransformer();

		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(this.xmlDocument), new StreamResult(writer));
		String output = writer.getBuffer().toString().replaceAll("\n|\r", "");

		return output;
	}

	private void remove_assign_from_children(Node firstNode) {

		if (firstNode.getNodeName().equals(this.tag("assign"))) {
			firstNode.getParentNode().removeChild(firstNode);
			return;
		}
		NodeList children = firstNode.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			this.remove_assign_from_children(node);
		}

	}

	protected List<Action> getActions() {

		Node firstNode = this.xmlDocument.getFirstChild();

		return this.get_actions(firstNode.getChildNodes());
	}

	private List<Action> get_actions(NodeList nodeList) {

		List<Action> list_action = new ArrayList<Action>();

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			List<Action> child_list = this.get_actions(node.getChildNodes());
			list_action.addAll(child_list);

			if (this.is_action(node) == true) {
				list_action.add(new Action(node));
			}
		}

		return list_action;
	}

	private boolean is_action(Node node) {
		String name = node.getNodeName();
		
		NamedNodeMap attributes = node.getAttributes();
		if(attributes == null)
			return false;
		
		Node partnerLinkAttr = attributes.getNamedItem("partnerLink");
		
		if(partnerLinkAttr == null)
			return false;
		
		
		String partnerLink = partnerLinkAttr.getNodeValue();
		
		if(partnerLink.equals(MonitorWsdlManager.MONITOR_PARTNERLINK_NAME))
			return false;
 
		
		if (name.equals(this.tag("invoke")) || name.equals(this.tag("receive")) || name.equals(this.tag("onMessage")))
			return true;

		return false;
	}

	public void remove_monitor() {
		// TODO
	}

 
}
