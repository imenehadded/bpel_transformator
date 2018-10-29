package bpel.transformator.test;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import bpel.transformator.Action;
import bpel.transformator.Transformator;

public class UnitTest {

	String bpel_project_path = "C:\\Users\\Imene\\git\\bpel_orchestator";
	String monitor_wsdl_file = "C:\\Users\\Imene\\Desktop\\bpel_test\\monitor.wsdl";

	// @Test
	public void transformator() {

		String expression = "receiveInput.(InvokeInvoicing+InvokeShipping)";

		Transformator transformator = new Transformator(bpel_project_path);

		try {
			transformator.set_wsdl_monitoring_file(monitor_wsdl_file, expression);
			transformator.add_monitor_service(monitor_wsdl_file, expression);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	@Test
	public void clean_up() {

 
		Transformator transformator = new Transformator(bpel_project_path);

		try {
			transformator.removeMonitor();
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

	}
	
	private boolean relevant_to_monitor(String name){
		
		String[] relevant_re = { "assignMonitorInputTo\\w+_\\w+", "invokeMonitor_\\w+_\\w+",
				"assignMonitorOutputTo\\w+_\\w+", "condition_\\w+_\\w+" };
 
		
		for( String action_pattern : relevant_re){
			Pattern pattern = Pattern.compile(action_pattern);
			Matcher matcher = pattern.matcher((CharSequence) name);
			boolean is_matched = matcher.matches();
			if(is_matched==true){
				return true;
			}
		}
 
		return false;
	}
	
	// @Test
	public void relevant(){
		
		System.out.println("abc , " +relevant_to_monitor("abc"));
		System.out.println("assignMonitorInputTomohame_moha , " +relevant_to_monitor("assignMonitorInputTomohame_moha"));
		System.out.println("invokeMonitor_allo_allo , " +relevant_to_monitor("invokeMonitor_al_lo_allo"));
	}
	
	// @Test
	public void get_action() {

		Transformator transformator = new Transformator(bpel_project_path);

		try {

			List<Action> listAction = transformator.getActions();

			for (Action a : listAction) {
				System.out.println(a.getAction() + " , " + a.getOperation() + " , " + a.getPartnerLink());
			}

		} catch (Exception e) {
			e.printStackTrace();

		}

	}

}
