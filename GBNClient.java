import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.swing.Timer;


public class GBNClient {
	public static int WIN_SIZE;
	public static int start=0, end, dataCount;
	public static void main(String[] args) throws Exception {
		InetAddress serverAddress = InetAddress.getByName("localhost");
		DatagramSocket clientSocket = new DatagramSocket(9999);
		byte[] sendData;
		int end_ack;
		Timer[] timers = new Timer[20];
		Scanner scanner = new Scanner(System.in);
		dataCount = scanner.nextInt();
		WIN_SIZE = scanner.nextInt();
		end = start + WIN_SIZE - 1;
		System.out.println("丢包概率为0.3,在服务器端设定");
		System.out.println("重传定时器为3秒,在客户端设定,逾期则GoBack重新发送");
		System.out.println("滑动窗大小为" + WIN_SIZE);
		System.out.println("客户端即将发送" + dataCount +"个数据包");
		
		for (int i=start;i<=end;i++){
			sendData = (i + "seq").getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, 8888);
			clientSocket.send(sendPacket);
			timers[i] = new Timer(3000, new DelayActionListener(clientSocket, i, timers));
			timers[i].start();
			System.out.println("客户端发送数据包 " + i);
		}
		
		while (true){
			byte[] recvData = new byte[100];
			DatagramPacket recvPacket = new DatagramPacket(recvData, recvData.length);
			clientSocket.receive(recvPacket);
			int ack_seq = new String(recvPacket.getData()).charAt(3) -'0';
			System.out.println("客户端接收 ack=" + ack_seq);
			timers[ack_seq].stop();
			if (ack_seq == start){
				start++;
				end++;
				if (end > dataCount - 1)
					end = dataCount - 1;
				sendData = (end + "seq").getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, 8888);
				clientSocket.send(sendPacket);
				timers[end] = new Timer(3000, new DelayActionListener(clientSocket, start, timers));
				timers[end].start();
				System.out.println("客户端发送数据包 " + end);
			}
			if (ack_seq == dataCount - 1){
				System.out.println("全部数据已被发送成功！");
				return;
			}
		}
	}
}

class DelayActionListener implements ActionListener{

	DatagramSocket clientSocket;
	int end_ack;
	Timer[] timers;
	public DelayActionListener(DatagramSocket clientSocket, int end_ack, Timer[] timers){
		this.clientSocket = clientSocket;
		this.end_ack = end_ack;
		this.timers = timers;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		int end = GBNClient.end;
		System.out.println("客户端准备重传数据 " + end_ack +"--" + end);
		for (int i=end_ack;i<=end;i++){
			byte[] sendData;
			InetAddress serverAddress = null;
			try {
				serverAddress = InetAddress.getByName("localhost");
				sendData = (i + "seq").getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, 8888);
				clientSocket.send(sendPacket);
				System.out.println("客户端发送数据包 " + i);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			timers[i].stop();
			timers[i].start();
		}
	}
}


