package bpel.transformator;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MonitorWsdlManager extends BaseXml {

	private Document xmlDocument;

	public static String MONITOR_PARTNERLINK_NAME = "MonitorPartnerLinkType";
	public static String MONITOR_ROLE_NAME = "bpel_monitor";
	public static String MONITOR_WSDL_FILE_NAME = "bpel_monitor.wsdl";
	public static String MONITOR_WSDL_PREFIX = "monitns";
	public static String MONITOR_REQUEST_VARAIBLE_NAME = "monitorServiceRequest";
	public static String MONITOR_RESPONSE_VARAIBLE_NAME = "monitorServiceResponse";

	protected MonitorWsdlManager(String source) {

		this.xmlDocument = this.loads(source);

	}

	public void add_parter_link() {

		// 1- adding partner link definition in monitor wsdl file

		Node node = this.xmlDocument.getFirstChild();

		String targetNamespace = this.get_namespace();
		System.out.println("WSDL TARGET NAME-SPACE : " + targetNamespace);

		// xmlns:plnk="http://docs.oasis-open.org/wsbpel/2.0/plnktype"
		this.setAttributeNode(node, "xmlns:plnk", "http://docs.oasis-open.org/wsbpel/2.0/plnktype");

		// monitor targetNamespace
		String xmlns_monitor = this.get_xmlns(this.xmlDocument, targetNamespace);

		System.out.println("MONITOR XMLNS : " + xmlns_monitor);

		// remove old partner link nodes

		NodeList nodeList = node.getChildNodes();

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node iNode = nodeList.item(i);
			if (iNode.getNodeType() == 1)
				if (iNode.getNodeName().equals("plnk:partnerLinkType"))
					node.removeChild(iNode);

		}

		/*
		 * monitor wsdl operation portType
		 * 
		 * <plnk:partnerLinkType name="MonitorPartnerLinkType"> <plnk:role
		 * name="externalws" portType="tns:ExternalWs" />
		 * </plnk:partnerLinkType>
		 */

		String opertation_name = this.get_opation_name();

		Element partnerLinkTypeNode = this.xmlDocument.createElement("plnk:partnerLinkType");
		this.setAttributeNode(partnerLinkTypeNode, "name", MONITOR_PARTNERLINK_NAME);

		Element partnerLinkRoleNode = this.xmlDocument.createElement("plnk:role");
		this.setAttributeNode(partnerLinkRoleNode, "name", MONITOR_ROLE_NAME);
		this.setAttributeNode(partnerLinkRoleNode, "portType", xmlns_monitor + opertation_name);

		partnerLinkTypeNode.appendChild(partnerLinkRoleNode);

		node.appendChild(partnerLinkTypeNode);

		// create new monitor wsdl file with partnerlink node
		// String output_file = new File(this.bpel_project_path ,
		// MONITOR_WSDL_FILE_NAME).getPath();

		// this.dumps(monitorWsdlDocument, output_file);

	}

	@Override
	public void dumps(String output_file) {

		super.dumps(this.xmlDocument, output_file);
	}

	public boolean is_partnerLink_exist() {

		NodeList nodeList = this.xmlDocument.getElementsByTagName(this.tag("partnerLink"));

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node iNode = nodeList.item(i);
			String parnerLinkName = iNode.getAttributes().getNamedItem("name").getNodeValue();
			if (parnerLinkName.equals(MONITOR_PARTNERLINK_NAME))
				return true;
		}

		return false;
	}

	public String get_namespace() {

		Node node = this.xmlDocument.getFirstChild();

		String targetNamespace = node.getAttributes().getNamedItem("targetNamespace").getNodeValue();

		return targetNamespace;
	}

	public String tag(String tag_name) {

		String prefix = this.get_xmlns(this.xmlDocument, "http://schemas.xmlsoap.org/wsdl/");

		return prefix + tag_name;

	}

	public String get_input_message_name() {

		NodeList nodeList = this.xmlDocument.getElementsByTagName(this.tag("portType"));

		System.out.println(nodeList.item(0).getNodeName());

		nodeList = ((Element) nodeList.item(0)).getElementsByTagName(this.tag("operation"));

		Node inputNode = ((Element) nodeList.item(0)).getElementsByTagName(this.tag("input")).item(0);

		String message_name = inputNode.getAttributes().getNamedItem("message").getNodeValue();

		if (message_name.indexOf(":") > 0)
			message_name = message_name.substring(message_name.indexOf(":") + 1);

		return message_name;

	}

	public String get_output_message_name() {

		NodeList nodeList = this.xmlDocument.getElementsByTagName(this.tag("portType"));

		nodeList = ((Element) nodeList.item(0)).getElementsByTagName(this.tag("operation"));

		Node inputNode = ((Element) nodeList.item(0)).getElementsByTagName(this.tag("output")).item(0);

		String message_name = inputNode.getAttributes().getNamedItem("message").getNodeValue();

		if (message_name.indexOf(":") > 0)
			message_name = message_name.substring(message_name.indexOf(":") + 1);

		return message_name;

	}

	private Node get_operation_node() {

		NodeList nodeList = this.xmlDocument.getElementsByTagName(this.tag("portType"));

		if (nodeList.getLength() > 1)
			System.err.println("MORE THAN ONE PORT-TYPE DEFINED IN WSDL MONITOR FILE");

		nodeList = nodeList.item(0).getChildNodes();

		List<Node> operations = new ArrayList<Node>();

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node iNode = nodeList.item(i);
			if (iNode.getNodeType() == 1)
				if (iNode.getNodeName().equals(this.tag("operation")))
					operations.add(iNode);
		}

		if (operations.size() > 1)
			System.err.println("MORE THAN ONE OPERATION DEFINED IN WSDL MONITOR FILE");

		return operations.get(0);

	}

	public String get_opation_name() {
		Node operation = this.get_operation_node();
		String monitor_opertation_name = operation.getAttributes().getNamedItem("name").getNodeValue();
		System.out.println("MONITOR OPERATION PORTAL-TYPE NAME : " + monitor_opertation_name);
		return monitor_opertation_name;

	}

	public String get_portType_name() {

		NodeList nodeList = this.xmlDocument.getElementsByTagName(this.tag("portType"));

		String portType_name = nodeList.item(0).getAttributes().getNamedItem("name").getNodeValue();

		return portType_name;

	}

	public String get_service_name() {

		NodeList nodeList = this.xmlDocument.getElementsByTagName(this.tag("portType"));

		String service_name = nodeList.item(0).getAttributes().getNamedItem("name").getNodeValue();

		return service_name;

	}
}
