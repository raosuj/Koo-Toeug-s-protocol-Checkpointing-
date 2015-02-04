import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.SctpChannel;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.charset.CharacterCodingException;
import java.net.*;
import com.sun.nio.sctp.*;
import java.util.*;

// Helps in sending message at regular intervals

public class MessageNotifier implements Runnable
{
	public MessageNotifier(){
	}

	public synchronized void run(){

		while(true){
			try
			{
				Thread.sleep(6000);
				System.out.println("FLOODING");
				Project3.floodMessages();
			} 
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
