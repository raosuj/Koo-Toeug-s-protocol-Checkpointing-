import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.*;
import java.net.InetSocketAddress;
import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.SctpChannel;

// Helps in receiving message from the sender

public class ReceiveThread implements Runnable {

	SctpChannel receiveSctpChannel;
	volatile static int processId;
	public static boolean doRun = true;

	ReceiveThread(SctpChannel receiveSctpChannel, int currentID) {
		this.receiveSctpChannel = receiveSctpChannel;
		this.processId = currentID;
	}

	private static String byteToString(ByteBuffer byteBuffer) {
		byteBuffer.position(0);
		byteBuffer.limit(512);
		byte[] bufArr = new byte[byteBuffer.remaining()];
		byteBuffer.get(bufArr);
		return new String(bufArr);
	}

	public synchronized void run() {
		while (doRun) {
			try {

				// Receive the message from the sender
				ByteBuffer byteBuffer = ByteBuffer.allocate(512);
				MessageInfo messageInfo = receiveSctpChannel.receive(byteBuffer, null, null);
				String message = byteToString(byteBuffer);

				// Display the received message
				if(message!=null && (message.trim().length() > 0)){

				System.out.println("MSG: " + message);

					SCTPClient Sctpclient = new SCTPClient();
					Sctpclient.processAlgorithmMsg(message);
				}

				
				// Check continuously for TERMINATION
				//if(SCTPClient.hostQuitMap.containsValue(null))
				{ 
					SCTPServer.doRun = true;
				}
				/*else{
					// Proceed for termination of client and server in current machine
					// Stop the server and initiate to close the socket connection
 
					if(SCTPServer.doRun){
						SCTPClient Sctpclient = new SCTPClient();
						Sctpclient.closeClient();
						SCTPServer.doRun = false;
						ReceiveThread.doRun = false;
					}  
				}
				*/
				byteBuffer.clear();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
