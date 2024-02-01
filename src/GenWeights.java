import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

import myfileio.MyFileIO;

// TODO: Auto-generated Javadoc
/**
 * The Class GenWeights.
 */
public class GenWeights {
	
	/** Constant representing the number of ASCII characters. */
	private final int NUM_ASCII = 128;
	/** Constant representing the start of the printable range of ASCII characters */
    private final int ASCII_PRINT_MIN = 32;    
	/** Constant representing the end of the printable range of ASCII characters */
    private final int ASCII_PRINT_MAX = 126;    
    /** The input and output File handles. */
    private File inf,outf;
    
    /** The array for collecting the weights */
    private int[] weights = new int[NUM_ASCII]; 
    
    /** Instance the the MyFileIO package to access methods for handling files */
    private MyFileIO fio;
    
    /** The ignore chr 13. */
    private boolean ignoreChr13 = false;
    
    /** Instance of the HuffCompAlerts - used as an intermediary between this 
     *  class and the GUI
     */
    private HuffCompAlerts hca;
    
	/**
	 * Instantiates a new GenWeights object and connects it to the supplied
	 * HuffCompAlerts object (hca).
	 *
	 * @param hca the hca
	 */
	public GenWeights(HuffCompAlerts hca) {
		this.hca = hca;
		fio = new MyFileIO();

	}

	/**
	 * Initializes all elements of the weights array to 0
	 */
	void initWeights() {
		for (int i = 0; i < weights.length; i++) {
			weights[i] = 0;
		}
	}

	/**
	 * Generate character-based frequency weights. You will write this method,
	 * using the MyFileIO fio instance to create the File object, check, open 
	 * and close the file.
	 * 
	 * NOTE: All source text files are located in the data/ directory. You do NOT need to 
	 *       prepend "data/" to the infName - the GUI handles this for you.
	 * 
	 * You should check the file for the following issues (using the getFileStatus method
	 * in MyFileIO) and take the appropriate action if they occur.
	 * 
	 * a) if the filename is empty, raise a WARNING alert with an appropriate message and
	 *    return.
	 * b) if the file does not exist or is empty, raise a WARNING alert with an appropriate 
	 *    message and return.
	 * c) if the file exists and is not empty, but is not readable, raise a WARNING alert with 
	 *    an appropriate message and return.
	 * 
	 * Each of these errors should be differentiated and reported to the user via an alert 
	 * 
	 * Assuming that the requirements of a, b and c have all been met successfully, 
	 * initialize the weights and read the file character by character, incrementing 
	 * weights[character] by one each time. Remember to account for the EOF character 
	 * (ordinal value 0) - this will never be read, but is present at the end of each 
	 * file, so you must increment weights[0] manually (or set to 1).
	 *  
	 * Refer to the HuffAlerts and HuffCompAlerts Classes to understand the alert types
	 * 
	 * Once the input file has been fully processed, you should print the weights to the console.
	 *
	 * @param infName - the name of the text file to read
	 */
	void generateWeights(String infName) {
		File inf = new File(infName);
		int status = fio.checkFileStatus(inf, true);
		if (status != MyFileIO.FILE_OK) {
			if (inputErrors(status))
				return;
		}
		initWeights();
		BufferedReader br = fio.openBufferedReader(inf);
		int c;
		try {
			while ((c = br.read()) != -1) {
				weights[c]++;
			}
			fio.closeFile(br);
		} catch (IOException e) {
			e.printStackTrace();
		}
		weights[0]++;
		printWeights();
		return;
	}
	
	boolean inputErrors(int status) {
		switch (status) {
		case MyFileIO.EMPTY_NAME:
			hca.issueAlert(HuffAlerts.INPUT, "Input Error", "Empty file name");
			return true;
		case MyFileIO.FILE_DOES_NOT_EXIST:
			hca.issueAlert(HuffAlerts.INPUT, "Input Error", "File does not exist");
			return true;
		case MyFileIO.NOT_A_FILE:
			hca.issueAlert(HuffAlerts.INPUT, "Input Error", "Not a file");
			return true;
		case MyFileIO.READ_ZERO_LENGTH:
			hca.issueAlert(HuffAlerts.INPUT, "Input Error", "Zero length");
			return true;
		case MyFileIO.NO_READ_ACCESS:
			hca.issueAlert(HuffAlerts.INPUT, "Input Error", "Not readable");
			return true;
		}
		return false;
	}
	
	/**
	 * Prints the weights to the console. Non-printing characters (0-31, 127) 
	 * are indicated with [ ], printing characters are displayed to help with debug
	 * 
	 */
	void printWeights() {
		for (int i = 0; i < weights.length; i++) {
			if ((i < ASCII_PRINT_MIN) || (i > ASCII_PRINT_MAX))  
				System.out.println("i:"+i+" [ ] = "+weights[i]);
			else 
				System.out.println("i:"+i+" ("+(char)i+") = "+weights[i]);

				
		}
	}
	
	/**
	 * Write the character-based frequency data to the specified file, one index per line.
	 * Use the following format:
	 *   print the index and the frequency count separated by a comma.
	 *   ie, if weights[10]=421, this would write the following line:
	 *   10,421,
	 *   DO NOT PRINT THE ACTUAL CHARACTER - as this will cause problems when you
	 *   try to analyze the data with your favorite spreadsheet (YFSS)
	 *   
	 * Again, you will use the MyFileIO methods to create the File object, check, open and close
	 * the file. 
	 * 
	 * NOTE: all weights file will be written to the weights/ directory. outfName will already account 
	 * for this - you do NOT need to prepend "weights/" to outfName.
	 *   
	 * Assuming that there are no errors, raise an INFORMATION alert with an appropriate message
	 * to indicate to the user that the output file was created successfully. Make sure to refer to 
	 * the notes from class today for any more details. Again, the actual writing of the file might be
	 * best done in a separate helper method.
	 *   
	 * You must handle the following error conditions and take the appropriate actions,
	 * as in the generateWeights() method:
	 *   if outfName is blank, raise a OUTPUT alert with an appropriate message and return.
	 *   if the output file exists but is not writeable, raise an OUTPUT alert with an appropriate message
	 *   and return.
	 *   if the output file exists and is writeable, raise a CONFIRM alert with an appropriate
	 *   message to the user. if they cancel the operation, return; otherwise, continue.
	 *   otherwise, if there is no error (status == MyFileIO.FILE_OK), continue.
	 *   
	 * Refer to the HuffAlerts and HuffCompAlerts Classes to understand the alert types
	 * 
	 * @param outfName the name of the weights file (includes weights/ )
	 */
	 void saveWeightsToFile(String outfName) {
		if (outfName.length() == 0) {
			hca.issueAlert(HuffAlerts.OUTPUT, "Output Error", "Empty file name");
			return;
		}
		File outf = new File(outfName);
		int status = fio.checkFileStatus(outf, false);
		if (status != MyFileIO.FILE_OK) {
			if (outputErrors(status))
				return;
		} else {
			if (!fio.createEmptyFile(outfName)) {
				hca.issueAlert(HuffAlerts.OUTPUT, "Output Error", "Could not create file");
				return;
			}
		}
		BufferedWriter bw = fio.openBufferedWriter(outf);
		try {
			for (int i = 0; i < weights.length; i++) {
					bw.write(i + "," + weights[i] + ",\n");
			}
			fio.closeFile(bw);
			hca.issueAlert(HuffAlerts.DONE, "Information", "File created successfully");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}
	
	 /**
		 * 
		 *
		 * @param infName the name of the text file to read
		 * @return the weights array
		 */
	boolean outputErrors(int status) {
		switch (status) {
		case MyFileIO.NO_WRITE_ACCESS:
			hca.issueAlert(HuffAlerts.OUTPUT, "Output Error", "Cannot Write");
			return true;
		case MyFileIO.WRITE_EXISTS:
			if (hca.issueAlert(HuffAlerts.CONFIRM, "Confirm", "OK or Cancel"))
				return false;
			return true;
		case MyFileIO.NOT_A_FILE:
			hca.issueAlert(HuffAlerts.OUTPUT, "Output Error", "Not a file");
			return true;
		}
		return false;
	}

	
	/**
	 * Read input file and return weights
	 * This file forces the creation of the weights for JUnit testing
	 * and during file encoding if the weights file does not exist.
	 * DO NOT CHANGE THIS METHOD
	 *
	 * @param infName the name of the text file to read
	 * @return the weights array
	 */
	int[] readInputFileAndReturnWeights(String infName) {
		System.out.println("Generating weights for: "+infName);
		generateWeights(infName);
		return weights;
	}	
}
