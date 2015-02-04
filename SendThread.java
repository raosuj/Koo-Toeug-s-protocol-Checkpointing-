import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.SctpChannel;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.charset.CharacterCodingException;
import java.net.*;
import com.sun.nio.sctp.*;
import java.util.*;

// Helps in sending message to destination

public class SendThread implements Runnable
{
	SctpMultiChannel	sendSctpChannel;
	InetSocketAddress destMachineId;
	String message;
	ByteBuffer byteBuffer = ByteBuffer.allocate(512);

	public SendThread(SctpMultiChannel sendSctpChannel, InetSocketAddress destMachineId, String message)
	{
		this.sendSctpChannel = sendSctpChannel;
		this.destMachineId = destMachineId;
		this.message = message;
	}

	private void sendToken(SctpMultiChannel clientSock, String Message) throws CharacterCodingException,ClosedChannelException, InterruptedException
	{
		// Prepare byte buffer to send message
		ByteBuffer sendBuffer = ByteBuffer.allocate(512);
		sendBuffer.clear();

		// Reset a pointer to point to the start of buffer 
		sendBuffer.put(Message.getBytes());
		sendBuffer.flip();

		try 
		{
			// Send the message to destination channel 
			MessageInfo messageInfo = MessageInfo.createOutgoing(destMachineId,0);
			clientSock.send(sendBuffer, messageInfo);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}      
	}

	public synchronized void run()
	{
		try
		{
			String currentMessage = this.message;
			sendToken(this.sendSctpChannel, currentMessage);
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
