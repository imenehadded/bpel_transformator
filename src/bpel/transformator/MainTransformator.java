package bpel.transformator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
 
public class MainTransformator {

 
	private String bpel_project_path = null;
	
	private String bpel_file =null;
	private String bpel_deploy_file  = null;
	

	public MainTransformator(String project_path){
		
		System.out.println(project_path);
		
		this.bpel_project_path = new File(project_path , "bpelContent").getPath();
		
		
		File folder = new File(this.bpel_project_path);
		File[] listOfFiles = folder.listFiles();

	    for (int i = 0; i < listOfFiles.length; i++) {
	      if (listOfFiles[i].isFile()) {
	         
	        if( listOfFiles[i].getName().endsWith(".bpel"))
	        	this.bpel_file = listOfFiles[i].getPath();

	        if( listOfFiles[i].getName().equals("deploy.xml"))
	        	this.bpel_deploy_file = listOfFiles[i].getPath();

	      } 
	    }
	    
		System.out.println(this.bpel_file);
		System.out.println(this.bpel_deploy_file);
 		
	}
	
	private String download_wsdl_file(String wsdl_url){
		
		String wsdl_location_output = null;
		
		try {
			
			wsdl_location_output = this.get_monitor_wsdl_file_output();
			
			URL website = new URL(wsdl_url);
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos = new FileOutputStream(wsdl_location_output);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return wsdl_location_output;
	}
	
	private String get_monitor_wsdl_file_output(){
		return new File(this.bpel_project_path , MonitorWsdlManager.MONITOR_WSDL_FILE_NAME).getPath();
	}
	
	public void set_wsdl_monitoring_file(String wsdl_location){
		
		if (wsdl_location.indexOf("http") > 0 ){
			wsdl_location = this.download_wsdl_file(wsdl_location);
		}
		
		String wsdl_location_output = this.get_monitor_wsdl_file_output();
		
		MonitorWsdlManager monitorManager = new MonitorWsdlManager(wsdl_location);
		monitorManager.add_parter_link();
		monitorManager.dumps(wsdl_location_output);
		 
		BpelManager bpelManager = new BpelManager(this.bpel_file, wsdl_location_output);
		
		bpelManager.import_monitor_wsdl();
		
		bpelManager.dumps(this.bpel_file);

		DeployXmlManager deployXml = new DeployXmlManager(this.bpel_deploy_file, wsdl_location_output);
		
		deployXml.update_deploy_file();
		
		deployXml.dumps(this.bpel_deploy_file);
		
		
	}

}
