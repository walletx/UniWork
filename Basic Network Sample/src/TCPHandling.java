import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;


public class TCPHandling {
	
	public static void sendFileRequest(cdht peer, String fileNumber) throws Exception {
		
		int successorPort = peer.getFirstSuccessor() + 50000;
		
		Socket clientSocket = new Socket("127.0.0.1", successorPort);
		
		String message = "Request " + fileNumber + " " + peer.getIdentity() + "\n";		
		
		//Get output stream and send the message
		DataOutputStream outgoingRequest = new DataOutputStream(clientSocket.getOutputStream());
		outgoingRequest.writeBytes(message);  
		
		System.out.println("File request message for " + fileNumber + " has been sent to my successor.");   
	    clientSocket.close();
	}
	
	private static void sendFileRequestForward(int destination, String toSend) throws Exception {
		
		//Same as sendFileRequest but packaged to a smaller size
		
		int successorPort = destination + 50000;
		
		Socket clientSocket = new Socket("127.0.0.1", successorPort);		
		
		DataOutputStream outgoingRequest = new DataOutputStream(clientSocket.getOutputStream());
		outgoingRequest.writeBytes(toSend);  
		
	    clientSocket.close();
	}
	
    public static void sendQuitRequest(cdht peer) throws Exception {
    	
    	//Sends quit messages to both predecessors
    	
    	if (peer.getChangePredecessor() != 2) {
    		System.out.println("Cannot quit until predecessor updated.");
    		return;
    	}
		
		int predecessorPortOne = peer.getFirstPredecessor() + 50000;
		int predecessorPortTwo = peer.getSecondPredecessor() + 50000;
		int successorOne = peer.getFirstSuccessor() + 50000;
		int successorTwo = peer.getSecondSuccessor() + 50000;

		String quitString = "Quit " + peer.getIdentity() + " " + peer.getFirstSuccessor() + " " + peer.getSecondSuccessor() + "\n";
		String successorInform = "Inform";
		
		Socket clientSocketOne = new Socket("127.0.0.1", predecessorPortOne);
		Socket clientSocketTwo = new Socket("127.0.0.1", predecessorPortTwo);
		Socket clientSocketThree = new Socket("127.0.0.1", successorOne);
		Socket clientSocketFour = new Socket("127.0.0.1", successorTwo);

		DataOutputStream outgoingRequestOne = new DataOutputStream(clientSocketOne.getOutputStream());
		DataOutputStream outgoingRequestTwo = new DataOutputStream(clientSocketTwo.getOutputStream());
		DataOutputStream outgoingRequestThree = new DataOutputStream(clientSocketThree.getOutputStream());
		DataOutputStream outgoingRequestFour = new DataOutputStream(clientSocketFour.getOutputStream());

		outgoingRequestOne.writeBytes(quitString); 
		outgoingRequestTwo.writeBytes(quitString);
		outgoingRequestThree.writeBytes(successorInform);
		outgoingRequestFour.writeBytes(successorInform);
		
		clientSocketOne.close();
		clientSocketTwo.close();
		clientSocketThree.close();
		clientSocketFour.close();
	}
	
	public static void recieveFileRequest(cdht peer) throws Exception {
		
		int identityNumber = peer.getIdentity();
		int firstSuccessorNumber = peer.getFirstSuccessor();
		
		//Used to tally the number of confirmation quit messages
		int exitConfirm = 0;
		
		int identityPort = identityNumber + 50000;
		ServerSocket serverSocket = new ServerSocket(identityPort);
		
		while (true) {
			
			// Listens AND accepts
			Socket connectionSocket = serverSocket.accept();
			
	        BufferedReader incomingRequest = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
	        String requestString = incomingRequest.readLine();
	        
	        String[] identifierString = requestString.split(" ");
	        	        
	        if (identifierString[0].equals("Request")) {
	        	
	        	 String fileRequestNumber = identifierString[1];
	        	 int fileHash = (Integer.parseInt(identifierString[1]) + 1) % 256;
	        	 firstSuccessorNumber = peer.getFirstSuccessor();
	             
	             String successString = "Received a response message from peer " + identityNumber + 
	              		", which has the file " + fileRequestNumber + ".";
	                     
	             //Deals with case where successor port number wraps around
	             if (identityNumber > firstSuccessorNumber) {
	             	if (fileHash >= identityNumber || fileHash < firstSuccessorNumber) {
	                 	System.out.println("File " + fileRequestNumber + " is here.");
	                 	System.out.println("A response message, destined for peer " + identifierString[2] + ", has been sent.");
	                 	sendFileRequestForward(Integer.parseInt(identifierString[2]), successString);
	                 } else {
	                 	System.out.println("File " + fileRequestNumber + " is not stored here.");
	                 	System.out.println("File request message has been forwarded to my successor.");
	                 	sendFileRequestForward(peer.getFirstSuccessor(), requestString);
	                 }
	             } else {
	                 if (fileHash >= identityNumber && fileHash < firstSuccessorNumber) {
	                 	System.out.println("File " + fileRequestNumber + " is here.");
	                 	System.out.println("A response message, destined for peer " + identifierString[2] + ", has been sent.");
	                 	sendFileRequestForward(Integer.parseInt(identifierString[2]), successString);
	                 } else {
	                 	System.out.println("File " + fileRequestNumber + " is not stored here.");
	                 	System.out.println("File request message has been forwarded to my successor.");
	                 	sendFileRequestForward(peer.getFirstSuccessor(), requestString);
	                 }
	             }
	             
	        } else if (identifierString[0].equals("Received")) {
	        	
	        	//Case where file is found and peer has a response message
	        	System.out.println(requestString);
	        	
	        } else if (identifierString[0].equals("Quit")) {
	        		        	
	        	int quittingPeer = Integer.parseInt(identifierString[1]);
	        	int successorOne = Integer.parseInt(identifierString[2]);
	        	int successorTwo = Integer.parseInt(identifierString[3]);
	        	
	        	System.out.println("Peer " + quittingPeer + " will depart from network.");
	        	
	        	//Sets new successors
	        	if (peer.getFirstSuccessor() == quittingPeer) {
	        	 	peer.setFirstSuccessor(successorOne);
	        	 	peer.setSecondSuccessor(successorTwo);	        	
	        	} else {
	        	 	peer.setSecondSuccessor(successorOne);
	        	}
	        	
        	 	System.out.println("My first successor is now peer " + peer.getFirstSuccessor() + ".");
        	 	System.out.println("My second successor is now peer " + peer.getSecondSuccessor() + ".");
        	 	 
        	 	//Sends a quit ack message to the quitting peer
        	 	Socket socket = new Socket("127.0.0.1", quittingPeer + 50000);		
        		
        		DataOutputStream outgoingRequest = new DataOutputStream(socket.getOutputStream());
        		outgoingRequest.writeBytes("Exit");  
        		
        		socket.close();
        	    
	        } else if (identifierString[0].equals("Exit")) {
	        	
		        	exitConfirm++;
	        	
	        	//If two confirmation messages recieved, quit.
	        	if (exitConfirm == 2) {
		        	System.out.println("Quit confirmation recieved twice. Exit program.");
		        	System.exit(0);
	        	} 
	        	
	        } else if (identifierString[0].equals("Inform")) {
	        	
	            peer.setChangePredecessor(0); 
	        	
	        }
		}   
                
	}

}
