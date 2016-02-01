package bpel.aldebarane.lts;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

public class Test {
 
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String current = System.getProperty("user.dir");

		String bpel_file_name = new File(current,"static//sample.bpel").toString();
		String lts_output_file = new File(current,"static//sample.lts").toString();
		String aut_output_file = new File(current,"static//sample.aut").toString();
	
		System.out.println(bpel_file_name);
		
		Bpel2Lts bpel2lts = new Bpel2Lts(bpel_file_name);
		
		try {
			bpel2lts.exportLts(lts_output_file);
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Lts2Aut lts2aut = new Lts2Aut(lts_output_file);
		lts2aut.exportAut(aut_output_file);
	}

}
