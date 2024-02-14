import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import myfileio.MyFileIO;

/**
 * The Class EncodeDecode. 
 */
public class EncodeDecode {
	
	/** The encodeMap maps each ascii value to its huffman code */
	private String[] encodeMap;
	
	/** Instance of the huffman compression utilites for building the tree and encode man */
	private HuffmanCompressionUtilities huffUtil;
	
	/** Instance of GenWeights used to generate the frequency weights if no weights file is specified */
	private GenWeights gw;
	
	/** Instance of HuffCompAlerts for relaying information to the GUI or console */
	private HuffCompAlerts hca;
	
	/**  Provides facilities to robustly handle external file IO. */
	private MyFileIO fio;
	
	/** The bin util. */
	private BinaryIO binUtil;
	
	/**  The array for storing the frequency weights. */
	private int[] weights;	

	/**
	 * Instantiates a new EncodeDecode instance
	 *
	 * @param gw - instance of GenWeights
	 * @param hca - instance of HuffCompAlerts
	 */
	public EncodeDecode (GenWeights gw, HuffCompAlerts hca) {
		fio = new MyFileIO();
		this.gw = gw;
		this.hca = hca;
		huffUtil = new HuffmanCompressionUtilities();
		binUtil = new BinaryIO();
	}
	
	/**
	 * Encode. This function will do the following actions:
	 *         1) Error check the inputs
	 * 	       - Perform error checking on the file to encode, using MyFileIO fio.
	 *         - Generate the array of frequency weights - either read from a file in the output/ directory
	 *           or regenerate from the file to encode in the data/ directory
	 *         - Error check the output file...
	 *         Any errors will abort the conversion...
	 *         
	 *         2) set the weights in huffUtils
	 *         3) build the Huffman tree using huffUtils;
	 *         4) create the Huffman codes by traversing the trees.
	 *         5) call executeEncode to perform the conversion.
	 *
	 * @param fName 	the name of the input file to be encoded
	 * @param bfName 	the name of the binary (compressed) file to be created
	 * @param freqWts 	the name of the file to read for the frequency weights. If blank, or other error,
	 *                  generate the frequency weights from fName.
	 * @param optimize 	if true, ONLY add leaf nodes with non-zero weights to the priority queue
	 */
	void encode(String fName, String bfName, String freqWts, boolean optimize) {
		
		File f = fio.getFileHandle(fName);
		File bf = fio.getFileHandle(bfName);
		File fw = fio.getFileHandle(freqWts);
		if (errorCheck(fName, bfName, freqWts)) {
			return;
		}
		
		huffUtil.setWeights(huffUtil.readFreqWeights(fw));
		huffUtil.buildHuffmanTree(optimize);
		huffUtil.createHuffmanCodes(huffUtil.getTreeRoot(), "", 0);
		executeEncode(f, bf);
	}
	/**
	 * checks 4 errors
	*/
	boolean errorCheck(String in, String out, String weights) {
		File f = fio.getFileHandle(in);
		File bf = fio.getFileHandle(out);
		File fw = fio.getFileHandle(weights);
		if (fio.checkFileStatus(f, true) != MyFileIO.FILE_OK) {
			hca.issueAlert(HuffAlerts.INPUT, "Input Error", "Could not read input file");
			return true;
		}
		if (fio.checkFileStatus(bf, false) != MyFileIO.FILE_OK) {
			if (fio.checkFileStatus(bf, false) == MyFileIO.WRITE_EXISTS) {
				if (!hca.issueAlert(HuffAlerts.CONFIRM, "Confirmation", "Write in the file?"))
					return true;
			} else {
				hca.issueAlert(HuffAlerts.OUTPUT, "Output Error", "Could not create binary file");
				return true;
			}
		}
		fio.createEmptyFile(out);
		if (fio.checkFileStatus(fw, true) != MyFileIO.FILE_OK) {
			if (fio.checkFileStatus(fw, true) == MyFileIO.READ_ZERO_LENGTH) {
				gw.generateWeights(in);
				gw.saveWeightsToFile(weights);
				hca.issueAlert(HuffAlerts.INPUT, "Input Error", "Weights needed to be created");
			} else {
				hca.issueAlert(HuffAlerts.INPUT, "Input Error", "Could not read weights");
				return true;
			}
			
		}
		return false;
	}
	
	/**
	 * Execute encode. This function will write compressed binary file as part of part 3
	 * 
	 * This functions should:
	 * 1) get the encodeMap from HuffUtils 
	 * 2) initialize binStr to ""
	 * 3) open a BufferedReader for the text file and a BufferedOutputStream for the binary file
	 * 4) for each character in the textfile:
	 * 	  - append the huffman code to binStr;
	 *    - if binStr length >= 8, write the binStr to the binary file using binUtils.writeBinString();
	 *      binStr should be set to any returned string value.
	 * 5) when the input file is exhausted, write the EOF character, padding with 0's if needed 
	 * 6) close the the input and output files...
	 *
	 * @param inFile the File object that represents the file to be compressed
	 * @param binFile the File object that represents the compressed output file
	 */
	private void executeEncode(File inFile, File binFile) {
		// TODO: write this method and any required helper methods
		encodeMap = huffUtil.getEncodeMap();
		String binStr = "";
		BufferedReader br = fio.openBufferedReader(inFile);
		BufferedOutputStream bo = fio.openBufferedOutputStream(binFile);
		
		int c;
		try {
			while ((c = br.read()) != -1) {
				binStr += encodeMap[c];
				if (binStr.length() >= 8) {
					binStr = binUtil.writeBinString(bo, binStr);
				}
			}
			binUtil.writeBinString(bo, binStr + encodeMap[0] + "0000000");
			fio.closeStream(bo);
			fio.closeFile(br);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	// DO NOT CODE THIS METHOD UNTIL EXPLICITLY INSTRUCTED TO DO SO!!!
	/**
	 * Decode. This function will only be addressed in part 5. It will 
	 *         1) Error check the inputs
	 * 	       - Perform error checking on the file to decode
	 *         - Generate the array of frequency weights - this MUST be provided as a file
	 *         - Error check the output file...
	 *         Any errors will abort the conversion...
	 *         
	 *         2) set the weights in huffUtils
	 *         3) build the Huffman tree using huffUtils;
	 *         4) create the Huffman codes by traversing the trees.
	 *         5) executeDecode
	 *
	 * @param bfName 	the name of the binary file to read
	 * @param ofName 	the name of the text file to write...
	 * @param freqWts the freq wts
	 * @param optimize - exclude 0-weight nodes from the tree
	 */
	void decode(String bfName, String ofName, String freqWts,boolean optimize) {
	}
	
	// DO NOT CODE THIS METHOD UNTIL EXPLICITLY INSTRUCTED TO DO SO!!!
	/**
	 * Execute decode.  - This is part of PART 5...
	 * This function performs the decode of the binary(compressed) file.
	 * It will read each byte from the binary file and convert it to a string of 1's and 0's
	 * This will be appended to any leftover bits from prior conversions.
	 * Starting from the head of the string, decode occurs by traversing the Huffman Tree from the root
	 * until a Leaf node is reached. If a leaf node is reached, the character is written to the output
	 * file, and the corresponding # of bits is removed from the string. If the end of the bit string is reached
	 * without reaching a leaf node, the next byte is processed, and so on until the encoded EOF
	 * character is encountered. 
	 * After completely decoding the file, close the input file and
	 * flushed and close the output file.
	 *
	 * @param binFile the file object for the binary input file
	 * @param outFile the file object for the binary output file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void executeDecode(File binFile, File outFile) throws IOException {
	}

}
