package bpel.transformator;

import java.net.MalformedURLException;
import java.net.URL;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DeployXmlManager extends BaseXml {

	private Document xmlDocument;
	private MonitorWsdlManager monitorManager;

	protected DeployXmlManager(String source, String monitor_wsdl_file) {

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
		try {
			URL url = new URL(monitor_namespace_url);
			service_name = url.getHost() + ":" + this.monitorManager.get_service_name();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String monitor_service_port = this.monitorManager.get_portType_name();
		this.setAttributeNode(serviceNode, "name", service_name);
		this.setAttributeNode(serviceNode, "port", monitor_service_port);

		invokeNode.appendChild(serviceNode);

		Node processNode = this.xmlDocument.getElementsByTagName("process").item(0);
		processNode.appendChild(invokeNode);

	}

	@Override
	public void dumps(String output_file) {

		super.dumps(this.xmlDocument, output_file);

	}
}
