package bpel.transformator.test;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.Test;
import org.xml.sax.SAXException;

import bpel.transformator.Action;
import bpel.transformator.Transformator; 

public class UnitTest {
	
	String bpel_project_path = "C:\\Users\\Imene\\git\\bpel_orchestator";
	String monitor_wsdl_file = "C:\\Users\\Imene\\Desktop\\bpel_test\\monitor.wsdl";
	
	//@Test
	public void transformator() {

		String expression = "receiveInput.(InvokeInvoicing+InvokeShipping)";

		Transformator transformator = new Transformator(bpel_project_path);

		try {
			transformator.set_wsdl_monitoring_file(monitor_wsdl_file, expression);
			transformator.add_monitor_service(monitor_wsdl_file, expression);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}

	}
	
	@Test
	public void get_action() {
 
		Transformator transformator = new Transformator(bpel_project_path);
		
		try {
		 
			List<Action> listAction = transformator.getActions();

			for (Action a : listAction) {
				System.out.println(a.getOperation());
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
 
}
