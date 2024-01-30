/*
 *  JUnit test for the GenWeights Class.
 */
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.aden.hf_lib.HuffCompTestLib;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
class HF_GenWeightsTest {
	private final int ordCR = 13;
	HuffCompTestLib hflib = new HuffCompTestLib();
	GenWeights gw;
	HuffCompAlerts hca;
    String dir = "data/";
    
    /**
     * Gets the operating system from the System.
     *
     * @return the operating system from the 
     */
    private static String getOperatingSystem() {
    	String os = System.getProperty("os.name");
    	return os;
    }
	
	/**
	 * Determines which OS the test is running on, as it makes a difference
	 *
	 * @throws Exception the exception
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		System.out.println("Running on: "+getOperatingSystem());
	}

	/**
	 * Tear down after class.
	 *
	 * @throws Exception the exception
	 */
	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@BeforeEach
	void setUp() throws Exception {
	}

	/**
	 * Tear down.
	 *
	 * @throws Exception the exception
	 */
	@AfterEach
	void tearDown() throws Exception {
	}

	/**
	 * Test GenWeights on simple.txt
	 * Each of the tests in this section uses the same methodology:
	 * - use gw.readInputFileAndReturnWeights to generate the tstWeights array
	 * - compares the tstWeights array against expectations using hflib.checkWeights,
	 *   a method in HuffCompTestLib.jar. Checking distinguishes between Windows and
	 *   Mac/Linux/Unix file systems based upon the weight of the CR character (ordinal
	 *   value of 13) - Windows stores CR with every LF (ordinal value 10 - a new line), 
	 *   whereas Mac/Linux/Unix do not use CR.
	 */
	@Test
	@Order(1)
	void test_GenWeights_simple() {
		hca = new HuffCompAlerts(null);
		gw = new GenWeights(hca);
		
		int[] tstWeights = gw.readInputFileAndReturnWeights(dir+"simple.txt");
		boolean ignCHR13 = (tstWeights[ordCR] == 0);
		assertTrue(hflib.checkWeights("simple.txt", ignCHR13, tstWeights));
	}
	
	/**
	 * Test GenWeights on "Green Eggs and Ham.txt".
	 */
	@Test
	@Order(2)
	void test_GenWeights_GEAH() {
		hca = new HuffCompAlerts(null);
		gw = new GenWeights(hca);
		
		int[] tstWeights = gw.readInputFileAndReturnWeights(dir+"Green Eggs and Ham.txt");
		boolean ignCHR13 = (tstWeights[ordCR] == 0);
		assertTrue(hflib.checkWeights("Green Eggs and Ham.txt", ignCHR13, tstWeights));
	}

	/**
	 * Test GenWeights on "The Cat in the Hat.txt".
	 */
	@Test
	@Order(3)
	void test_GenWeights_TCITH() {
		hca = new HuffCompAlerts(null);
		gw = new GenWeights(hca);
		
		int[] tstWeights = gw.readInputFileAndReturnWeights(dir+"The Cat in the Hat.txt");
		boolean ignCHR13 = (tstWeights[ordCR] == 0);
		assertTrue(hflib.checkWeights("The Cat in the Hat.txt", ignCHR13, tstWeights));
	}

	/**
	 * Test GenWeights on "Harry Potter and the Sorcerer's Stone.txt".
	 */
	@Test
	@Order(4)
	void test_GenWeights_HPATS() {
		hca = new HuffCompAlerts(null);
		gw = new GenWeights(hca);
		
		int[] tstWeights = gw.readInputFileAndReturnWeights(dir+"Harry Potter and the Sorcerer.txt");
		boolean ignCHR13 = (tstWeights[ordCR] == 0);
		assertTrue(hflib.checkWeights("Harry Potter and the Sorcerer.txt", ignCHR13, tstWeights));
	}

	/**
	 * Test GenWeights on "warAndPeace.txt".
	 */
	@Test
	@Order(5)
	void test_GenWeights_WAP() {
		hca = new HuffCompAlerts(null);
		gw = new GenWeights(hca);
		
		int[] tstWeights = gw.readInputFileAndReturnWeights(dir+"warAndPeace.txt");
		boolean ignCHR13 = (tstWeights[ordCR] == 0);
		assertTrue(hflib.checkWeights("warAndPeace.txt", ignCHR13, tstWeights));
	}
	
	/**
	 * This test generates the weights for simple.txt and captures them in the
	 * int[] tstWeights. It then saves the weights to the specified file, checks that it
	 * exists, and then reads it line by to ensure that the format is correctly implemented 
	 * and that the content is correct.
	 */
	@Test
	@Order(6)
	void test_saveWeights_simple() {
		hca = new HuffCompAlerts(null);
		gw = new GenWeights(hca);
		int[] tstWeights = gw.readInputFileAndReturnWeights(dir+"simple.txt");
		File saveFile = new File("weights/test_simple.csv");
		if (saveFile.exists()) 
			assertTrue(saveFile.delete());
		gw.saveWeightsToFile(saveFile.getPath());
		assertTrue(saveFile.exists());
	
		boolean first = true;
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("weights/test_simple.csv")));
			String line ="";
			while ((line = br.readLine())!= null) {
				String tokens[] = line.split(",");
				if (first) {
					System.out.println("Testing file format:");
					assertTrue(line.matches("^\\d,\\d,+\\s*$"));  // remove + after 2022-23
					assertTrue(tokens.length == 2);
					first = false;
				}
				int index = Integer.parseInt(tokens[0]);
				int weight = Integer.parseInt(tokens[1]);
				assertTrue(tstWeights[index] == weight);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
