package bpel.transformator;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

abstract class BaseXml {

	abstract public void dumps(String output_file);
	
	
	protected Document loads(String source){
		
		try {
			DocumentBuilder xmlBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document xmlDocument = xmlBuilder.parse(new File(source));
			return xmlDocument;
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
 
	}
	
	public String get_xmlns(Document document,String namespace_url) {
		 
		Node node = document.getFirstChild();
 
  		NamedNodeMap attributes  = node.getAttributes();
  		
  		for (int i= 0;i<attributes.getLength();i++) {
  			Node iNode = attributes.item(i);
  			
  			if(iNode.getNodeValue().equals(namespace_url)){
  				String node_name =iNode.getNodeName();
  				if(node_name.startsWith("xmlns:")){
 
  					String ns_name= node_name.substring(node_name.indexOf(":")+1);
  					
  					return ns_name.equals("") ? ns_name : ns_name+":";
  				}
  			}
		}
  		
  		return "";
	}
	
	protected void dumps(Document document, String output_file) {
		
		// write the content into xml file
		System.out.println("Start writing content in xml file "+output_file);

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		
		try {
			
			transformer = transformerFactory.newTransformer();

			DOMSource source = new DOMSource(document);
			
			File xmloutput = new File(output_file);
			
			if(!xmloutput.exists())
				xmloutput.createNewFile();

			StreamResult result = new StreamResult(xmloutput);

			transformer.transform(source, result);
			
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void setAttributeNode(Node node, String key, String value) {
		 
		NamedNodeMap attributes  = node.getAttributes();
		Node attribute = attributes.getNamedItem(key);
		if(attribute == null){

			Attr operationAttr = node.getOwnerDocument().createAttribute(key);
			operationAttr.setValue(value);
			
			Element node_element = (Element) node;
			node_element.setAttributeNode(operationAttr);
			
		}
		
	}
}
