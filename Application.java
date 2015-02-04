import java.io.File;
import java.io.IOException;
import java.util.*;
import java.io.*;
import java.nio.*;
import java.nio.ByteBuffer;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.charset.*;


//Main class that demonstrates the Testing and Implementation 

public class Application {
	public static int myID;
	public static volatile boolean isDone = false;
	static HashMap<Integer, String> hostPortMap = new HashMap<Integer, String>();
	static int maxRange = 0;
	static int testCaseNumber = 0;
	static int totalRunInTestCase = 0;
	public static int waitingTime = 0;

	public static void main(String[] args) throws IOException{

		//////////////////////////////////////////////////////////////////////////////////////////
		//Start of Config File Reading Operations	- to get host and port details from config.txt//
		//////////////////////////////////////////////////////////////////////////////////////////

		StringBuilder uniqueIdByLine = new StringBuilder();
		StringBuilder hostName = new StringBuilder();
		String firstCharacterInLine = "";
		int lineNumber = Integer.parseInt(args[0]);
		String[] splitStr = null;
		int i=0, count=0;
		String hostNameToSend = null; 
		int portNumber = 0;
		String pathStr = null;
		testCaseNumber = Integer.parseInt(args[1]);

		try {

			BufferedReader reader = new BufferedReader(new FileReader("config.txt"));
			ArrayList<String> line = new ArrayList<String>();
			String templine  = null;

			while ((templine = reader.readLine()) != null) {
				if(maxRange==0){ maxRange = Integer.parseInt(templine.trim());}
				else{ 
					line.add(templine) ;}
			}


			for(i=1; i<=line.size(); i++){

				splitStr = line.get(i-1).split("\\s+");
				if(!splitStr[0].contains("utdallas")){
					hostName.append(splitStr[0]);
					hostName.append(".utdallas.edu");				
				}
				else{
					hostName.append(splitStr[0]);
				}

				if(i==lineNumber){
					hostNameToSend  = hostName.toString();
					portNumber = Integer.parseInt(splitStr[1].trim());
					//SCTPClient.currentHost = hostNameToSend;
				}

				hostName.append(":");
				hostName.append(splitStr[1]);
				hostPortMap.put(i, hostName.toString());
				hostName.setLength(0);
			}

			String[] tmpBindKey;
			Set<Integer> keys = hostPortMap.keySet();
			for(Integer key: keys){
				//System.out.println(" HostPort " + hostPortMap.get(key));
			}

			//////////////////////////////////////////////////////////////////////////////////////
			//End of Config File Reading Operations							//
			//////////////////////////////////////////////////////////////////////////////////////

			//Initializing Process ID, Quorum Size and machine-address list
			Application.myID = lineNumber;
			Project3 startConnection = new Project3(Application.myID, maxRange, hostPortMap);
			startConnection.initialize();

			boolean started = false;
			int startClockTime = -1;
			int repeatTimes = 1;


			//Call CS Enter Service
			Application.csEnter(startConnection, startClockTime);


			//Call CS Leave Service
			Application.csLeave();

			//Start termination protocol to quit processes
			//startConnection.intiateTermination(); 

		}
		catch (IOException e) {
			System.out.println("I/O Error: " + e.getMessage());
		}


	}


	//Functionality for a process to initiate entering of CRITICAL SECTION
	public static void csEnter(Project3 startConnection, int clockInput){

		MessageNotifier ClientSend = new MessageNotifier();
		Thread messageThread = new Thread(ClientSend);
		messageThread.start();

		//Function in Project3.java source code
		startConnection.initiateAlgorithm(clockInput);
	}


	//Functionality for a process to release resources after leaving CRITICAL SECTION
	public static void csLeave() throws IOException {

	}


	//Helper function which causes the execution to pause for givenTime seconds
	public static void waitForMilliSeconds(int givenTime){
		try {
			if(givenTime == -1){givenTime = waitingTime*1000;}
			Thread.sleep(givenTime);
		} catch(InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}


	//Helper function which returns timeStamp and repeatTimes by reading input.txt file
	public static String[] getTimeStamp(int localmyID, int n)  
	{  

		String[] myNode = null;
		try  
		{  
			String[] addToList = null;
			int index = localmyID - 1;
			ArrayList<String>  testCase = new ArrayList<String>();
			String testCaseIWant = null;

			FileInputStream fs = new FileInputStream("input.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(fs));

			for(int i = 1; i <= n; ++i){
				testCaseIWant = br.readLine();
			}
			addToList = testCaseIWant.split("::");  
			for (int k=0; k<addToList.length; k++){
				testCase.add(addToList[k]);
			}            
			myNode = testCase.get(index).split(",");

		} catch (FileNotFoundException e){  
			e.printStackTrace(); 
		} 
		catch (IOException e){  
			e.printStackTrace();  
		}
		return myNode; 
	}//EOF getTimeStamp


	//Helper to write "myID" to output.txt file to determine the order of CS entry by processes
	//Just for cross-referencing the visual output
	public static void printToFile(int localmyID){

		try{
			
			File file = new File("Output.txt");
			String ID = (String)Integer.toString(localmyID)+"->";
			if (!file.exists()) {
				file.createNewFile();
			}

			FileInputStream fr = new FileInputStream("Output.txt");
			FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
			BufferedReader br = new BufferedReader(new InputStreamReader(fr));
			BufferedWriter bw = new BufferedWriter(fw);

			String currentLineChars = null; 
			int lineCount = 0;
			currentLineChars = br.readLine();

			while(br.readLine()!=null){lineCount++;}

			fr.getChannel().position(0);
			br = new BufferedReader(new InputStreamReader(fr));

			currentLineChars = null;
			for(int i = 0; i<=lineCount; i++){
				currentLineChars = br.readLine();
			}

			bw.write(ID);

			int processCount = 0;
			if(currentLineChars == null){
				processCount = 0;
			}
			else{
				for( int i=0; i<currentLineChars.length(); i++ ) {
					if( currentLineChars.charAt(i) == '-' ) {
       					processCount++;
    					} 
				}
			}

			if(processCount == (totalRunInTestCase-1) ){
				bw.write("\r\n ");
			}

			bw.close();
		} 
		catch (IOException e){
			e.printStackTrace(); 
		}

	}//EOF printToFile


}//EOF Application Class

