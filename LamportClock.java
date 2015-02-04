//Lamport's Logical Clock implementation
public class LamportClock{

//Functions returns Lamport clock value

	//Function which updates Lamport Clock's value on message SEND operation
	int updateClockOnSend(int Ci){
		Ci = Ci + 1;
		return Ci;
	}

	//Function which updates Lamport Clock's value on message RECEIVE operation
	int updateClockOnReceive(int Ci, int tm){
		int updatedClock = 0;
		updatedClock = Math.max(Ci, tm);
		updatedClock = updatedClock + 1;

		return updatedClock ;
	}

}