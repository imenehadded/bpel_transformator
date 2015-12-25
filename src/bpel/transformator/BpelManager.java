package bpel.transformator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class BpelManager extends BaseXml {

	private Document xmlDocument;
	private MonitorWsdlManager monitorManager;

	protected BpelManager(String source, String monitor_wsdl_file) {

		this.monitorManager = new MonitorWsdlManager(monitor_wsdl_file);

		this.xmlDocument = this.loads(source);

	}

	private String tag(String tag_name) {

		String prefix = this.get_xmlns(this.xmlDocument, "http://docs.oasis-open.org/wsbpel/2.0/process/executable");

		return prefix + tag_name;
	}

	public void import_monitor_wsdl() {

		// 2- import monitor wsdl in bpel file as external file

		boolean is_imported = this.is_monitor_import_exist();

		if (is_imported == false) {

			String monitor_ns = this.monitorManager.get_namespace();

			Element monitorwsdlImportNode = this.xmlDocument.createElement(this.tag("import"));
			this.setAttributeNode(monitorwsdlImportNode, "location", MonitorWsdlManager.MONITOR_WSDL_FILE_NAME);
			this.setAttributeNode(monitorwsdlImportNode, "namespace", monitor_ns);
			this.setAttributeNode(monitorwsdlImportNode, "importType", "http://schemas.xmlsoap.org/wsdl/");

			Node import_node = this.xmlDocument.getElementsByTagName(this.tag("import")).item(0);
			Node node = this.xmlDocument.getFirstChild();
			node.insertBefore(monitorwsdlImportNode, import_node);

			// adding monitor wsdl as namespace
			this.setAttributeNode(node, "xmlns:" + MonitorWsdlManager.MONITOR_WSDL_PREFIX, monitor_ns);
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

		String monitor_request_name = this.monitorManager.get_input_message_name();
		String monitor_response_name = this.monitorManager.get_output_message_name();

		Element requestVariableNode = this.xmlDocument.createElement(this.tag("variable"));
		this.setAttributeNode(requestVariableNode, "name", MonitorWsdlManager.MONITOR_REQUEST_VARAIBLE_NAME);
		this.setAttributeNode(requestVariableNode, "messageType",
				MonitorWsdlManager.MONITOR_WSDL_PREFIX + ":" + monitor_request_name);

		Element responseVariableNode = this.xmlDocument.createElement(this.tag("variable"));
		this.setAttributeNode(responseVariableNode, "name", MonitorWsdlManager.MONITOR_RESPONSE_VARAIBLE_NAME);
		this.setAttributeNode(responseVariableNode, "messageType",
				MonitorWsdlManager.MONITOR_WSDL_PREFIX + ":" + monitor_response_name);

		Node variablesNode = this.xmlDocument.getElementsByTagName(this.tag("variables")).item(0);

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
					|| variableName.equals(MonitorWsdlManager.MONITOR_RESPONSE_VARAIBLE_NAME))

				variablesNode.removeChild(iNode);

		}
	}

	private boolean is_monitor_import_exist() {

		String monitor_ns = this.monitorManager.get_namespace();

		NodeList nodeList = this.xmlDocument.getElementsByTagName(this.tag("import"));

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node iNode = nodeList.item(i);
			String ns = iNode.getAttributes().getNamedItem("namespace").getNodeValue();
			if (ns.equals(monitor_ns))
				return true;

		}
		return false;
	}

	@Override
	public void dumps(String output_file) {

		super.dumps(this.xmlDocument, output_file);

	}
}
