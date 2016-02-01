package bpel.aldebarane.lts;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import bpel.transformator.BpelManager;
import uk.ic.doc.ltsa.eclipse.bpel.lang.bpel2fsp;

public class Bpel2Lts {

	private String bpel_file_name = null;
	
	public Bpel2Lts(String bpel_file_name){
		this.bpel_file_name = bpel_file_name;
		
	}
	
	private String getInputFromFile() throws TransformerException, ParserConfigurationException, SAXException, IOException {

/*		File file = new File(this.bpel_file_name);
		String result = null;
		DataInputStream in = null;

		try {

			byte[] buffer = new byte[(int) file.length()];
			in = new DataInputStream(new FileInputStream(file));
			in.readFully(buffer);
			result = new String(buffer);
			in.close();
		} catch (IOException e) {
			throw new RuntimeException("IO problem in fileToString", e);
		}*/
		BpelManager bpelManager = new BpelManager(this.bpel_file_name);
		
		return bpelManager.get_content_without_assign();
	}
	
	public void exportLts(String output_file) throws TransformerException, IOException, ParserConfigurationException, SAXException{
		
	
			  ByteArrayOutputStream out = new ByteArrayOutputStream();
			  ByteArrayOutputStream baos = new ByteArrayOutputStream();
			  
			File localFile = new File(output_file);
			if (localFile.exists() == false)
				localFile.createNewFile();
			
		      String source = this.getInputFromFile();
		      
		      bpel2fsp bpeltranslator = new bpel2fsp();
		      bpeltranslator.loadBPEL(source, false);
		      bpeltranslator.setFSPOutput(baos);
		      bpeltranslator.setOutput(out);
		      bpeltranslator.translateToFSP(false);
		      
		      String strout = new String(out.toByteArray(), "UTF-8");
		      System.out.println(strout);

		      OutputStream outputStream = new FileOutputStream (localFile); 
		      baos.writeTo(outputStream);
   

	}
}
