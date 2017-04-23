import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class UDPHandling {
	
	public static void sendPing(cdht peer, int successorNumber) throws Exception {
		
		int successorPort = successorNumber + 50000;
		DatagramSocket socket = new DatagramSocket();
	    socket.setSoTimeout(5000);
		
	    //Get information for sending out ping
		InetAddress successorAddress = InetAddress.getByName("127.0.0.1");
		
		//Creates request String
		String requestString = "A ping request message was received from Peer " + peer.getIdentity() + ".";
		byte[] byteString = requestString.getBytes();
	    
		//Sends UDP packet
		DatagramPacket outgoingPing = new DatagramPacket(byteString, byteString.length, successorAddress, successorPort);
        socket.send(outgoingPing);
        
        //Waits for UDP packet reply
		DatagramPacket incomingPing = new DatagramPacket(new byte[1024], 1024);
		socket.receive(incomingPing);
		
		String responseString = new String(incomingPing.getData());
		System.out.println(responseString);
		
		socket.close();
	}
	
	public static void recievePing(cdht peer) throws Exception {
		
		int identityPort = peer.getIdentity() + 50000;
		DatagramSocket socket = new DatagramSocket(identityPort);
		
		while (true) {
			//To store incoming UDP packet
			DatagramPacket incomingPing = new DatagramPacket(new byte[1024], 1024);
			
			//Waits until recieves UDP packet
	        socket.receive(incomingPing);
			
	        //Gets information from incoming UDP packet
			InetAddress predecessorAddress = incomingPing.getAddress();
	        int predecessorPort = incomingPing.getPort();
	        
	        String requestString = new String(incomingPing.getData());
			System.out.println(requestString);
	        
	        //Creates response string
	        String responseString = "A ping response message was received from Peer " + peer.getIdentity() + ".";
	        byte[] byteString = responseString.getBytes();
	        
	        //Creates and send a response message
	        DatagramPacket outgoingPing = new DatagramPacket(byteString, byteString.length, predecessorAddress, predecessorPort);
	        socket.send(outgoingPing);  	  
		         	        
	        String[] responseArray = requestString.split(" ");
	        String incomingPeerString = responseArray[8].replace(".", "").trim();
	        int incomingPeer = Integer.parseInt(incomingPeerString);
	        
	        //Order doesn't matter but this stops it from overwriting
	        //Locks predecessor updating until made sure that predecessor is peer that exists
	        if (peer.getFirstPredecessor() != incomingPeer || peer.getSecondPredecessor() != incomingPeer) {
				if (incomingPeer != peer.getFirstPredecessor() && incomingPeer != peer.getSecondPredecessor()) {
					peer.setSecondPredecessor(peer.getFirstPredecessor());
					peer.setFirstPredecessor(incomingPeer);
					if (peer.getChangePredecessor() == 0) {peer.setChangePredecessor(1);}
				} else if (incomingPeer != peer.getFirstPredecessor()) {
					peer.setSecondPredecessor(incomingPeer);
					if (peer.getChangePredecessor() == 1) {peer.setChangePredecessor(2);}
				}
			}
	        
		}	
		
	}

}
