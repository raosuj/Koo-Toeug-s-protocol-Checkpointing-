import com.sun.nio.sctp.SctpChannel;
import com.sun.nio.sctp.SctpServerChannel;
import java.net.InetSocketAddress;
import java.util.*;
import java.net.*;

//SCTP Server source code
public class SCTPServer extends Thread 
{
	public static int serverPortNumber;
	public static SctpChannel clientSockConn;
	public volatile static int projectID;
	public static boolean doRun = true;
	public static long startTime = 0L;
	public static long endTime = 0L;

	public  SCTPServer(){}

	public  SCTPServer(int portNumber, int myID) 
	{
		this.serverPortNumber = portNumber;
		this.projectID = myID;
		startTime = System.currentTimeMillis();
	} 

	public void run() 
	{
		try
		{
			// Create SCTP Server Object
			SctpServerChannel serverSock = SctpServerChannel.open();  
			InetSocketAddress serverAddr = new InetSocketAddress(this.serverPortNumber);
			serverSock.bind(serverAddr); 

			while (doRun)
			{

				// Receive a connection from client and accept it
				clientSockConn = serverSock.accept();

				try
				{
					// Start a thread for receiving messages continuously
					ReceiveThread ServerReceive = new ReceiveThread(clientSockConn, this.projectID);
					Thread receiveMessageThread = new Thread(ServerReceive);
					receiveMessageThread.start();
					Thread.sleep(100);

				}        
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}			
		catch(Exception e)
		{
			e.printStackTrace();
		}		
	}
}
