import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class cdht {
	
	private int identityNumber;
	private int firstSuccessor;
	private int secondSuccessor;
	private int firstPredecessor;
	private int secondPredecessor;
	private int changePredecessor;

	public static void main(String[] args) {
		
		int identityN = Integer.parseInt(args[0]);
		int firstS = Integer.parseInt(args[1]);
	    int secondS = Integer.parseInt(args[2]);
	    
	    final cdht peer = new cdht(identityN, firstS, secondS);
	    ScheduledExecutorService timer1 = Executors.newScheduledThreadPool(1);
	    ScheduledExecutorService timer2 = Executors.newScheduledThreadPool(1);
	    
	    Thread pingFirst = new Thread(new Runnable() {

	        @Override
	        public void run() {
				try {
					UDPHandling.sendPing(peer, peer.firstSuccessor);
				} catch (Exception e) {
					//e.printStackTrace();
				}
	        }
	        
	    });

	    timer1.scheduleAtFixedRate(pingFirst, 0, 20, TimeUnit.SECONDS);
	    
	    Thread pingSecond = new Thread(new Runnable() {

	        @Override
	        public void run() {
				try {
					UDPHandling.sendPing(peer, peer.secondSuccessor);
				} catch (Exception e) {
					//e.printStackTrace();
				}
	        }
	        
	    });

	  	timer2.scheduleAtFixedRate(pingSecond, 0, 20, TimeUnit.SECONDS);
	    
	    Thread recievePing = new Thread(new Runnable() {

	        @Override
	        public void run() {
	        	try {
					UDPHandling.recievePing(peer);
				} catch (Exception e) {
					//e.printStackTrace();
				}
	        }
	        
	    });

	    recievePing.start();
	    
	    Thread readSTDIN = new Thread(new Runnable() {

	        @Override
	        public void run() {
				try {
					
					while (true) {
						Scanner input = new Scanner(System.in);
						String inputString = input.nextLine();
						String[] arguments = inputString.split(" ");
						
						if (arguments[0].equals("request")) {
							String fileNumber = arguments[1];
							try {
								TCPHandling.sendFileRequest(peer, fileNumber);
							} catch (Exception e) {
								//e.printStackTrace();
							}
						} else if (arguments[0].equals("quit")) {
							//For it to work user must wait for predecessor peers
							// To ping their successors
							try {
								TCPHandling.sendQuitRequest(peer);
							} catch (Exception e) {
								//e.printStackTrace();
							}
						} else if (arguments[0].equals("debug")) {
							System.out.println("I is " + peer.identityNumber);
							System.out.println("P1 is " + peer.firstPredecessor);
							System.out.println("P2 is " + peer.secondPredecessor);
							System.out.println("S1 is " + peer.firstSuccessor);
							System.out.println("S2 is " + peer.secondSuccessor);
				
						}
					}	

				} catch (Exception e) {
					//e.printStackTrace();
				}
	        }
	        
	    });
	    
	    readSTDIN.start();
	    
	    Thread recieveTCP = new Thread(new Runnable() {

	        @Override
	        public void run() {
	        	try {
	        		TCPHandling.recieveFileRequest(peer);
				} catch (Exception e) {
					//e.printStackTrace();
				}
	        }
	        
	    });
	    
	    recieveTCP.start();
		
	}
	
	public cdht(int identityN, int firstS, int secondS) {
		
		identityNumber = identityN;
		firstSuccessor = firstS;
		secondSuccessor = secondS;
		firstPredecessor = -1;
		secondPredecessor = -1;
		changePredecessor = 0;
	}
	
	public int getIdentity() {
		return identityNumber;
	}
	
	public int getFirstSuccessor() {
		return firstSuccessor;
	}
	
	public int getSecondSuccessor() {
		return secondSuccessor;
	}
	
	public int getFirstPredecessor() {
		return firstPredecessor;
	}
	
	public int getSecondPredecessor() {
		return secondPredecessor;
	}
	
	public int getChangePredecessor() {
		return changePredecessor;
	}
	
	public void setFirstSuccessor(int number) {
		firstSuccessor = number;
	}
	
	public void setSecondSuccessor(int number) {
		secondSuccessor = number;
	}
	
	public void setFirstPredecessor(int number) {
		firstPredecessor = number;
	}
	
	public void setSecondPredecessor(int number) {
		secondPredecessor = number;
	}
	
	public void setChangePredecessor(int number) {
		changePredecessor = number;
	}
		
}

/*

xterm -hold -title "Peer 1" -e "java cdht 1 3 4" &
xterm -hold -title "Peer 3" -e "java cdht 3 4 5" &
xterm -hold -title "Peer 4" -e "java cdht 4 5 8" &
xterm -hold -title "Peer 5" -e "java cdht 5 8 10" &
xterm -hold -title "Peer 8" -e "java cdht 8 10 12" &
xterm -hold -title "Peer 10" -e "java cdht 10 12 15" &
xterm -hold -title "Peer 12" -e "java cdht 12 15 1" &
xterm -hold -title "Peer 15" -e "java cdht 15 1 3" &

*/