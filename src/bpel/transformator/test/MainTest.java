package bpel.transformator.test;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import bpel.transformator.MainTransformator;

public class MainTest {

	public static void main(String[] args) {

		String bpel_project_path = "C:\\Users\\Imene\\Desktop\\orchestrator";

		// String bpel_project_path =
		// "E:\\TheseImeneHadded\\Workspace\\hello_world";

		String monitor_wsdl_file = "C:\\Users\\Imene\\Desktop\\bpel_test\\monitor.wsdl";

		String expression = "receiveInput.(InvokeInvoicing+InvokeShipping)";

		MainTransformator transformator = new MainTransformator(bpel_project_path);

		try {
			transformator.set_wsdl_monitoring_file(monitor_wsdl_file, expression);
			transformator.add_monitor_service(monitor_wsdl_file, expression);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
