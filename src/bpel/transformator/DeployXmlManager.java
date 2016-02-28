package bpel.transformator;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

class DeployXmlManager extends BaseXml {

	private Document xmlDocument;
	private MonitorWsdlManager monitorManager;

	protected DeployXmlManager(String source, String monitor_wsdl_file)
			throws ParserConfigurationException, SAXException, IOException {

		this.monitorManager = new MonitorWsdlManager(monitor_wsdl_file);

		this.xmlDocument = this.loads(source);

	}

	public void update_deploy_file() {
		// 5- update deploy.xml file

		/*
		 * <invoke partnerLink="extrenaLWsPartnerLink"> <service
		 * name="ws.external.org:ExternalWsService" port="ExternalWsPort"/>
		 * </invoke>
		 */

		NodeList nodeList = this.xmlDocument.getElementsByTagName("invoke");

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node iNode = nodeList.item(i);
			String partnerLink = iNode.getAttributes().getNamedItem("partnerLink").getNodeValue();
			if (partnerLink.equals(MonitorWsdlManager.MONITOR_PARTNERLINK_NAME))
				this.xmlDocument.getElementsByTagName("process").item(0).removeChild(iNode);
		}

		Element invokeNode = this.xmlDocument.createElement("invoke");
		this.setAttributeNode(invokeNode, "partnerLink", MonitorWsdlManager.MONITOR_PARTNERLINK_NAME);

		Element serviceNode = this.xmlDocument.createElement("service");
		String monitor_namespace_url = this.monitorManager.get_namespace();
		String service_name = "";

		String namespace = null;
		try {
			URL url = new URL(monitor_namespace_url);
			namespace = url.getHost();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		service_name = namespace + ":" + this.monitorManager.get_service_name();

		this.setAttributeNode(this.xmlDocument.getFirstChild(), "xmlns:" + namespace, monitor_namespace_url);

		String monitor_binding_port = this.monitorManager.get_binding_name();

		this.setAttributeNode(serviceNode, "name", service_name);
		this.setAttributeNode(serviceNode, "port", monitor_binding_port);

		invokeNode.appendChild(serviceNode);

		Node processNode = this.xmlDocument.getElementsByTagName("process").item(0);
		processNode.appendChild(invokeNode);

	}

	@Override
	public void dumps(String output_file) {

		super.dumps(this.xmlDocument, output_file);

	}
}
