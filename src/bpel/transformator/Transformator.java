package bpel.transformator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;
  

public class Transformator {

	private String bpel_project_path = null;

	private String bpel_file = null;
	private String bpel_deploy_file = null;
	
	
	public Transformator(String project_path) {
 
		this.bpel_project_path = new File(project_path, "bpelContent").getPath();
		
		File folder = new File(this.bpel_project_path);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {

				if (listOfFiles[i].getName().endsWith(".bpel"))
					this.bpel_file = listOfFiles[i].getPath();

				if (listOfFiles[i].getName().equals("deploy.xml"))
					this.bpel_deploy_file = listOfFiles[i].getPath();

			}
		}

		System.out.println(this.bpel_file);
		System.out.println(this.bpel_deploy_file);
	}

	private String download_wsdl_file(String wsdl_url) throws IOException {

		String wsdl_location_output = null;

		wsdl_location_output = this.get_monitor_wsdl_file_output();

		URL website = new URL(wsdl_url);
		ReadableByteChannel rbc = Channels.newChannel(website.openStream());
		FileOutputStream fos = new FileOutputStream(wsdl_location_output);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();

		return wsdl_location_output;
	}

	private String get_monitor_wsdl_file_output() {
		return new File(this.bpel_project_path, MonitorWsdlManager.MONITOR_WSDL_FILE_NAME).getPath();
	}

	private static void copyFile(File sourceFile, File destFile) throws IOException {
	    if(!destFile.exists()) {
	        destFile.createNewFile();
	    }

	    FileInputStream source = null;
	    FileOutputStream destination = null;

	    try {
	    	
	        source = new FileInputStream(sourceFile);
	        destination = new FileOutputStream(destFile);
	        destination.getChannel().transferFrom(source.getChannel(), 0, source.getChannel().size());
	        source.close();
	        destination.close();
	    }
	    finally {
	        if(source != null) {
	            source.close();
	        }
	        if(destination != null) {
	            destination.close();
	        }
	    }
	}
	
	public void set_wsdl_monitoring_file(String wsdl_location, String expression)
			throws IOException, ParserConfigurationException, SAXException {

			
		String wsdl_location_file = this.get_monitor_wsdl_file_output();
		
		if (wsdl_location.indexOf("http") > 0) 
			wsdl_location = this.download_wsdl_file(wsdl_location);
		else{
			copyFile(new File(wsdl_location), new File(wsdl_location_file));
		}
 
		MonitorWsdlManager monitorManager = new MonitorWsdlManager(wsdl_location_file);
		monitorManager.add_parter_link();
		monitorManager.dumps(wsdl_location_file);

		BpelManager bpelManager = new BpelManager(this.bpel_file);

		bpelManager.import_monitor_wsdl(expression, wsdl_location_file);

		bpelManager.dumps(this.bpel_file);

		DeployXmlManager deployXml = new DeployXmlManager(this.bpel_deploy_file, wsdl_location_file);

		deployXml.update_deploy_file();
		System.out.println("before dumps monitor wsdl file");
		

	}
	
	public List<Action> getActions() throws ParserConfigurationException, SAXException, IOException{
		
		BpelManager bpelManager = new BpelManager(this.bpel_file);
		return bpelManager.getActions();
		
	}
	
	public void add_monitor_service(String monitor_wsdl_file, String expression)
			throws TransformerException, ParserConfigurationException, SAXException, IOException {

		BpelManager bpelManager = new BpelManager(this.bpel_file);
		
		bpelManager.remove_monitor();
		
		MonitorWsdlManager monitorManager = new MonitorWsdlManager(monitor_wsdl_file);

/*		File bpel_file_obj = new File(this.bpel_file);

	    String lts_output_file = new File(this.bpel_project_path, bpel_file_obj.getName() + ".lts").toString();
		String aut_output_file = new File(this.bpel_project_path, bpel_file_obj.getName() + ".aut").toString();

		Bpel2Lts bpel2lts = new Bpel2Lts(this.bpel_file);
		bpel2lts.exportLts(lts_output_file);

		Lts2Aut lts2aut = new Lts2Aut(lts_output_file);
		lts2aut.exportAut(aut_output_file);

		AutManager aut = new AutManager(aut_output_file, expression);

		List<Edge> criticalEdges = aut.getCriticalEdges();

		aut.loadBpelLables(bpelManager, criticalEdges);*/
		 
		/*
		int i = 0;
		for (Edge edge : criticalEdges) {

			System.out.println("******** IN CRITICAL EDGE *******");

			String action = edge.getBpel_action();
			String partnerlink = edge.getBpel_partnerLink();
			String operation = edge.getBpel_operation();

			System.out.println(action + "," + partnerlink + "," + operation + "," + "[" + edge.getLabel() + "]"
					+ edge.to_string());

			if (action == null) {
				System.err.println("NODE NOT FOUND IN BPEL FILE");
				continue;
			}
			bpelManager.setMonitorService(monitorManager, action, partnerlink, operation, i);

			i++;
		}*/
		
 
		bpelManager.setMonitorService(monitorManager, "invoke", "ExternalWs", "expF", 0);
		bpelManager.setMonitorService(monitorManager, "invoke", "ExternalWs", "bl", 1);
		
		// bpelManager.setMonitorService("InvokeServiceA",0 );

		bpelManager.dumps(this.bpel_file);

	}

	public void removeMonitor() throws ParserConfigurationException, SAXException, IOException {
		BpelManager bpelManager = new BpelManager(this.bpel_file);
		bpelManager.remove_monitor();
		
		bpelManager.dumps(this.bpel_file);
	}

}
