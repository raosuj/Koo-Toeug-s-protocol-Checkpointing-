import com.sun.nio.sctp.SctpChannel;
import com.sun.nio.sctp.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.*;

//Client Functionalities implementation for Maekawa Algorithm

public class SCTPClient extends Thread
{
	public static SctpMultiChannel ClientSock;
	public static boolean isOpen = false;
	public static HashMap<Integer, String> hostPortMap = new HashMap<Integer, String>();
	public static HashMap<String, String> hostQuitMap = new HashMap<String, String>();

	public static int myID = -1; 
	public static volatile int clockValue = 0;

	//Get Lamport clock object ----> Do Vector Clock??
	LamportClock clock = new LamportClock();

	//Initialize Process ID, hostPortMap, grantMap and failedMap
	public SCTPClient(){
		myID = Project3.myID;
	}

	//Initialize user defined clock input value
	public SCTPClient(int clockInput){
		this();
		if(clockInput != -1){
			clockValue = clockInput;
		}
	}

	// Code to close connections if termination detection occurs successfully
	public void closeClient ()  throws IOException
	{

		waitForMilliSeconds(3000);
		ClientSock.close();
		System.out.println("________________________________________________________"); 
		System.out.println(" EXIT PROCESS " + Project3.myID);
		System.out.println("________________________________________________________");
		System.exit(0);

	}

	// Open SctpMultiChannel only once
	// Used to maintain one-to-many associations from this SctpMultiChannel
	public void openChannel() throws IOException
	{
		if(!isOpen){
			ClientSock = SctpMultiChannel.open();
			isOpen = true;
		}
	}

	//Maekawa Algorithm control messages are processed here
	public void processAlgorithmMsg(String message) {

		int incomingTimeStamp = 0, incomingmyID = 0;

		//System.out.println("CLMSG" + message);

		//Extract Time Stamp and Process ID from the message
		if(message.contains("::") && message.contains(",")){
			//Extract incoming TimeStamp and process ID
			String[] splitTSPID = message.split("::");
			String[] getTSPID = splitTSPID[1].split(","); 
			incomingTimeStamp = Integer.parseInt(getTSPID[0].trim());
			incomingmyID = Integer.parseInt(getTSPID[1].trim());

			//Update clock value on receiving a message
			clockValue = clock.updateClockOnReceive(clockValue, incomingTimeStamp);
		}


		//Code to handle the Checkpointing&Recovery Algorithm's control messages
		if(message.contains("TEXT")){
			//Process the received meesage here

			//Assume dummy Process terminal number P2
			int nodeID = 2;

			//Send GRANT message
			messageSender(nodeID, "TEXT::"); 
		}

		//Case to handle Termination Protocol message
		else if(message.contains("CLOSE")){

			String[] tmpCloseHost = message.split(":");
			int closeID = Integer.parseInt(tmpCloseHost[1].trim());
			hostQuitMap.put(hostPortMap.get(closeID), "CLOSE");

		}//EOF Processing "CLOSE" message

	}


	//Helper function to send "GRANT/FAILED/INQUIRE/YIELD/RELEASE" messages to requested "processID"
	public void messageSender(int nodeID, String messageToken){
		String tempAddress = hostPortMap.get(nodeID);
		String[] nameHostPort = tempAddress.split(":");
		int tmpPort = Integer.parseInt(nameHostPort[1].trim());

		StringBuilder sendingToken = new StringBuilder(1);
		sendingToken.append(messageToken);
		sendingToken.append(clockValue);
		sendingToken.append(",");
		sendingToken.append(myID);

		//Update clock value on sending a message
		clockValue = clock.updateClockOnSend(clockValue);

		sendToClient(nameHostPort[0], tmpPort, sendingToken.toString());
	}


	//Helper function which causes the execution to pause for "givenTime" milliseconds 
	public void waitForMilliSeconds(int givenTime){
		try {
			Thread.sleep(givenTime);
		} 
		catch(InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}//EOF waitForMilliSeconds


	//Helper function to send messages based on given "hostName" and "portNumber"
	public void sendToClient (String hostName, int portNumber, String sendingToken)
	{
		try{

			InetSocketAddress serverAddr = new InetSocketAddress(hostName, portNumber);
			openChannel();

			SendThread ClientSend = new SendThread(ClientSock, serverAddr, sendingToken);
			Thread sendMessageThread = new Thread(ClientSend);
			sendMessageThread.start();
		}
		catch(Exception e){
			e.printStackTrace();
		}

	}//EOF sendToClient 


	//Initiate Termination Protocol: Broadcast "CLOSE" messages.
	public void startTermination(){

		// Broadcast to all other machines informing that current machine's execution has finished

		hostQuitMap.put(hostPortMap.get(myID), "CLOSE");
		String closeMsg = "CLOSE:" + myID;
		Set<Integer> keys = hostPortMap.keySet();
		for(Integer key: keys){

			if(key != myID){

				String tmpFullRemHost = hostPortMap.get(key);
				String[] tmpRemHost = tmpFullRemHost.split(":");
				String hostName = tmpRemHost[0];
				int portNumber = Integer.parseInt(tmpRemHost[1].trim());

				InetSocketAddress serverAddr = new InetSocketAddress(hostName, portNumber);						

				SendThread ClientSend = new SendThread(ClientSock, serverAddr, closeMsg);
				Thread sendMessageThread = new Thread(ClientSend);
				sendMessageThread.start();

			}
		}


	}//EOF startTermination

}//EO SCTPClient Class
