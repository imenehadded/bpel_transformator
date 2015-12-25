package bpel.transformator.test;

import bpel.transformator.MainTransformator;

public class MainTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String bpel_project_path = "E:\\TheseImeneHadded\\Workspace\\orchestrator";
		
		String monitor_wsdl_file = "C:\\Users\\Imene\\Desktop\\bpel_test\\monitor.wsdl";
		
		MainTransformator transformator = new MainTransformator(bpel_project_path);
		
		transformator.set_wsdl_monitoring_file(monitor_wsdl_file);
	}

}
