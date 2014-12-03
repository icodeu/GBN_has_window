import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class GBNServer {
	public static void main(String[] args) throws Exception {
		DatagramSocket serverSocket = new DatagramSocket(8888);
		int endReceive = -1;
		
		while (true){
			byte[] data = new byte[100];
			DatagramPacket receivePacket = new DatagramPacket(data, data.length);
			serverSocket.receive(receivePacket);
			int seq = new String(receivePacket.getData()).charAt(0) - '0';
			if (Math.random()<=0.7){
				if (seq == endReceive + 1){
					//接收成功，发ack;
					byte[] ackData = new String("ack"+seq).getBytes(); 
					InetAddress clientAddress = receivePacket.getAddress();
					int clientPort = receivePacket.getPort();
					DatagramPacket sendPacket = new DatagramPacket(ackData, ackData.length, clientAddress, clientPort);
					serverSocket.send(sendPacket);
					endReceive++;
					System.out.println("服务端发送 ack= " + seq);
				}else if (endReceive != -1){
					byte[] ackData = new String("ack"+endReceive).getBytes(); 
					InetAddress clientAddress = receivePacket.getAddress();
					int clientPort = receivePacket.getPort();
					DatagramPacket sendPacket = new DatagramPacket(ackData, ackData.length, clientAddress, clientPort);
					serverSocket.send(sendPacket);
					System.out.println("服务端发送 ack= " + endReceive);
				}
				
//				//接收成功，发ack;
//				byte[] ackData = new String("ack"+hasReceive).getBytes(); 
//				InetAddress clientAddress = receivePacket.getAddress();
//				int clientPort = receivePacket.getPort();
//				DatagramPacket sendPacket = new DatagramPacket(ackData, ackData.length, clientAddress, clientPort);
//				serverSocket.send(sendPacket);
//				if (seq == hasReceive)
//					hasReceive++;
			}
		}
	}
}
