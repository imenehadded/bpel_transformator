package bpel.transformator;

import java.io.File;
 
public class MainTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
 
		String bpel_project_path = "C:\\Users\\Imene\\Desktop\\bpel_test\\orchestrator";
		
		String monitor_wsdl_file = "C:\\Users\\Imene\\Desktop\\bpel_test\\monitor.wsdl";
		String monitor_wsdl_file_output = "C:\\Users\\Imene\\Desktop\\bpel_test\\monitor_2.wsdl";
		
		MonitorWsdlManager monitorManager = new MonitorWsdlManager(monitor_wsdl_file);
		monitorManager.add_parter_link();
		monitorManager.dumps(monitor_wsdl_file_output);
		
		String bpel_file = "C:\\Users\\Imene\\Desktop\\bpel_test\\orchestrator\\bpelContent\\orchestrator.bpel";
		String bpel_file_output = "C:\\Users\\Imene\\Desktop\\bpel_test\\orchestrator\\bpelContent\\bpel_output.bpel";
		BpelManager bpelManager = new BpelManager(bpel_file, monitor_wsdl_file_output);
		
		bpelManager.import_monitor_wsdl();
		
		bpelManager.dumps(bpel_file);

		String deploy_file = "C:\\Users\\Imene\\Desktop\\bpel_test\\orchestrator\\bpelContent\\deploy.xml";
		String deploy_file_output = "C:\\Users\\Imene\\Desktop\\bpel_test\\orchestrator\\bpelContent\\deploy_output.xml";
		DeployXmlManager deployXml = new DeployXmlManager(deploy_file, monitor_wsdl_file_output);
		
		deployXml.update_deploy_file();
		
		deployXml.dumps(deploy_file);
		
	}
	
 
	private String bpel_file = "";
	private String bpel_project_path = "";
	private String bpel_deploy_file  = "";
	private String monitor_wsdl_file = "";
	
	public void file(String bpel_project_path,String monitor_wsdl_file){
		
		this.bpel_project_path = new File(bpel_project_path , "bpelContent").getPath();
		this.monitor_wsdl_file = monitor_wsdl_file;
		
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
 

}
