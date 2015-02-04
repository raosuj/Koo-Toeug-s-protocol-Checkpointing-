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

//Wrapper class to start Server and run Maekawa Algorithm

public class Project3 {
	public static int myID = -1;
	public static int myPort = 0;
	public static int maxQuorumSize = 0;
	public static HashMap<Integer, String> hostPortMap = new HashMap<Integer, String>();
	public static boolean serverStarted = false;
	public static ArrayList<Integer> quorumMembers = new ArrayList<Integer>();

	public Project3(int currentID, int maxRange, HashMap<Integer, String> hostMap){
		myID = currentID;
		maxQuorumSize = maxRange;
		hostPortMap.putAll(hostMap);
	}

	//Function that starts up the server in the current machine
	public void startServer(){
		if(!serverStarted){
			try {
				String addressByID = hostPortMap.get(myID);
				String[] hostPort = addressByID.split(":");
				int portNumber = Integer.parseInt(hostPort[1].trim());

				//System.out.println(" Starting Server");

				//Start Server for current dc machine
				SCTPServer Sctpserver = new SCTPServer(portNumber, myID);
				Thread serverThread = new Thread(Sctpserver);
				serverThread.start();
				serverStarted = true;
				int waitTimeServer = (int)(((Math.sqrt(maxQuorumSize))*2) + 2)*1000;
				Thread.sleep(4000);
			} 
			catch(InterruptedException ex) 
			{
				Thread.currentThread().interrupt();
			}
		}
	}


	//Start Server and construct Quorum
	public void initialize(){
		//Start the server
		startServer();

		//Start Client for current dc machine
		SCTPClient Sctpclient = new SCTPClient();

	}


	public static void floodMessages(){
			//Start Client for current dc machine
			SCTPClient Sctpclient = new SCTPClient(0);

			//Send REQUEST message to each member in the Quorum list
			for (int i = 1; i <= 1; i++) {
				StringBuilder sendingToken = new StringBuilder(1);
				sendingToken.append("FLOOD::");
				int quorumName = (1 + (int)(Math.random() * ((4 - 1) + 1)));

				System.out.println("From: " + Project3.myID + "To:" + quorumName);

				String addressPath = hostPortMap.get(quorumName);
				sendingToken.append(2);
				sendingToken.append("," + myID);


				String[] hostPort = addressPath.split(":");
				int portNumber = Integer.parseInt(hostPort[1].trim());
				Sctpclient.sendToClient(hostPort[0], portNumber, sendingToken.toString());

			}
	}



	//Function to start running of C&R Algorithm
	public void initiateAlgorithm(int clockInput) {

		try{

			//Start the server
			startServer();

			//Start Client for current dc machine
			SCTPClient Sctpclient = new SCTPClient(clockInput);
			
			//WAIT TO ORDER HAPPENED-BEFORE RELATIONSHIP FOR TEST CASES FROM "input.txt" FILE
			Application.waitForMilliSeconds(-1);

			int currentClockValue = SCTPClient.clockValue;

			//Send REQUEST message to each member in the Quorum list
			for (int i = 1; i <= 4; i++) {
				StringBuilder sendingToken = new StringBuilder(1);
				sendingToken.append("REQUEST::");
				int quorumName = i;

				String addressPath = hostPortMap.get(quorumName);
				sendingToken.append(2);
				sendingToken.append("," + myID);


				String[] hostPort = addressPath.split(":");
				int portNumber = Integer.parseInt(hostPort[1].trim());
				Sctpclient.sendToClient(hostPort[0], portNumber, sendingToken.toString());

			}

		}
		catch(Exception e1){
			e1.printStackTrace();
		}
	}

	//Start termination protocol
	//public void intiateTermination(){
		//SCTPClient Sctpclient = new SCTPClient();
		//Sctpclient.startTermination();
	//}
}