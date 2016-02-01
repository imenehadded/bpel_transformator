package bpel.aldebarane.lts;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Hashtable;

import lts.CompositeState;
import lts.LTSCompiler;
import lts.LTSException;
import lts.LTSInput;
import lts.LTSOutput;
import lts.SymbolTable;

public class Lts2Aut implements LTSInput, LTSOutput {
	int fPos = -1;
	String fSrc = "\n";
	String currentDirectory = null;
	CompositeState current = null;

	private String lts_file = null;

	public Lts2Aut(String lts_file) {
		SymbolTable.init();
		this.lts_file = lts_file;
		this.currentDirectory = System.getProperty("user.dir");
	}

	private String getInputFromFile() {

		File file = new File(this.lts_file);
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
		}
		return result;
	}

	@SuppressWarnings("rawtypes")
	private Hashtable doparse() {
		this.fPos = -1;
		this.fSrc = this.getInputFromFile();
		LTSCompiler localLTSCompiler = new LTSCompiler(this, this, this.currentDirectory);
		Hashtable localHashtable1 = new Hashtable();
		Hashtable localHashtable2 = new Hashtable();
		try {
			localLTSCompiler.parse(localHashtable1, localHashtable2);
		} catch (LTSException localLTSException) {
			displayError(localLTSException);
			return null;
		}
		return localHashtable1;
	}

	public void exportAut(String aut_file) {

		@SuppressWarnings("rawtypes")
		Hashtable localHashtable = doparse();
		if (localHashtable == null)
			return;

		this.current = docompile();
		this.current.compose(this);

		try {

			File localFile = new File(aut_file);
			if (localFile.exists() == false)
				localFile.createNewFile();

			FileOutputStream localFileOutputStream = new FileOutputStream(localFile);

			PrintStream localPrintStream = new PrintStream(localFileOutputStream);

			this.current.composition.printAUT(localPrintStream);
			localPrintStream.close();
			localFileOutputStream.close();
			outln("Exported to: " + aut_file);
		} catch (IOException localIOException) {
			outln("Error exporting file: " + localIOException);
		}

	}

	private CompositeState docompile() {
		this.fPos = -1;
		this.fSrc = this.getInputFromFile();

		CompositeState localCompositeState = null;
		LTSCompiler localLTSCompiler = new LTSCompiler(this, this, this.currentDirectory);
		try {
			localCompositeState = localLTSCompiler.compile("DEFAULT");
		} catch (LTSException localLTSException) {
			displayError(localLTSException);
		}
		return localCompositeState;
	}

	private void displayError(LTSException paramLTSException) {
		if (paramLTSException.marker != null) {
			int i = ((Integer) paramLTSException.marker).intValue();
			int j = 1;
			for (int k = 0; k < i; k++) {
				if (this.fSrc.charAt(k) == '\n') {
					j++;
				}
			}
			outln("ERROR line:" + j + " - " + paramLTSException.getMessage());
			// this.input.select(i, i + 1);
		} else {
			outln("ERROR - " + paramLTSException.getMessage());
		}
	}

	@Override
	public void clearOutput() {

		System.out.println("*********** Clear output *************");
	}

	@Override
	public void out(String arg0) {

		System.out.println("[TLS-OUT] " + arg0);
	}

	@Override
	public void outln(String arg0) {

		System.out.println("[TLS-OUT] " + arg0);
	}

	@Override
	public char backChar() {
		this.fPos -= 1;
		if (this.fPos < 0) {
			this.fPos = 0;
			return '\000';
		}
		return this.fSrc.charAt(this.fPos);
	}

	@Override
	public int getMarker() {
		return this.fPos;
	}

	@Override
	public char nextChar() {
		this.fPos += 1;
		if (this.fPos < this.fSrc.length()) {
			return this.fSrc.charAt(this.fPos);
		}
		return '\000';
	}

}