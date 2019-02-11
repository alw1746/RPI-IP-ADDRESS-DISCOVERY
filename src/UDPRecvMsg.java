/*
Listen to UDP port for broadcast msg from from UDPSendMsg.
Append source ip address and port to message.
Usage: java UDPRecvMsg [port]
HELLO from YOGA520@192.168.20.14
*/
import java.io.*;
import java.net.*;

public class UDPRecvMsg implements Runnable {
  public static final int BRDCST_PORT=9876;
  public static int port;

  public static void main(String[] args) throws Exception {
    port=BRDCST_PORT;
    if (args.length == 1) {
      port=Integer.parseInt(args[0]);
    }
    //System.out.println("port "+port);

    Thread t=new Thread(new UDPRecvMsg());
    t.start();
  }

  public void receiveUDPMessage(int port) throws Exception {
      
    byte[] buffer=new byte[1024];
    DatagramSocket socket = new DatagramSocket(port);
    DatagramPacket packet=new DatagramPacket(buffer,buffer.length);
    socket.receive(packet);
    String sourceip=packet.getAddress().getHostAddress();
    String msg=new String(packet.getData(),packet.getOffset(),packet.getLength());
    System.out.println(msg+"@"+sourceip+":"+port);         
    socket.close();
   }

  public void run() {
    try {
      receiveUDPMessage(port);
    } catch (Exception ex){
      ex.printStackTrace();
    }
  }
}
