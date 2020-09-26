import java.util.*;
import java.net.*;


public class ipv6addresses {

	public static void main(String[] args) throws SocketException {
		
			 ArrayList<NetworkInterface> nfs = Collections.list(NetworkInterface.getNetworkInterfaces());
			 
			 for(NetworkInterface z:nfs) {
				 
				 System.out.printf("\nNetwork Interface : %s \n",z.toString());
		        	
				 ArrayList<InetAddress> ia = Collections.list(z.getInetAddresses());
//		        	
		         for(InetAddress zs:ia) {
		        		
		        	if(zs instanceof Inet6Address) {
		        		
		        		if(!zs.isLinkLocalAddress() && !zs.isLoopbackAddress()) {
		        			System.out.println("* Global ipv6 found "+zs.getHostAddress());
		        		}else if(zs.isLinkLocalAddress()) {
		        			System.out.println("* Link LOcal ipv6 found "+zs.getHostAddress());
		        		}else if(zs.isLoopbackAddress()) {
		        			System.out.println("* Loopback Address found "+zs.getHostAddress());
		        		}else {
		        			System.out.println("* IPV6 Address "+zs.getHostAddress());
		        		}
		        	}
			 }
		}

	}

}
