/*
Send a message using broadcast address of all interfaces thru port 9876 (by default).
if no msg is given on cmdline, the default is "HELLO from <hostname>".
Transmission is repeated 40x at 5 sec interval (total 200secs).
Usage: java UDPSendMsg [port] ["msg"]
To run this under pi user on RPi reboot,
  login as pi
  crontab -e
  @reboot /usr/bin/java UDPSendMsg > /tmp/UDPSendMsg.log 2>&1
*/
import java.net.*;
import java.util.*;

public class UDPSendMsg {
  public static final int REPEATS=40;
  public static final int SLEEP_INTERVAL=5000;
  public static final int BRDCST_PORT=9876;

  public static void sendUDPMessage(String message,String ipAddress, int port) throws Exception {
    DatagramSocket socket = new DatagramSocket();
    InetAddress group = InetAddress.getByName(ipAddress);
    byte[] msg = message.getBytes();
    DatagramPacket packet = new DatagramPacket(msg, msg.length, group, port);
    socket.send(packet);
    socket.close();
  }

  public static List<InetAddress> listAllBroadcastAddresses() throws Exception {
    int count=0;
    
    //Enumeration is repeated until network interfaces come up online during a reboot.
    List<InetAddress> broadcastList = new ArrayList<>();
    while (count < REPEATS) {
      Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
      while (interfaces.hasMoreElements()) {
          NetworkInterface networkInterface = interfaces.nextElement();
   
          if (networkInterface.isLoopback() || !networkInterface.isUp()) {
              continue;
          }
   
          networkInterface.getInterfaceAddresses().stream() 
            .map(a -> a.getBroadcast())
            .filter(Objects::nonNull)
            .forEach(broadcastList::add);
      }
      if (broadcastList.size() == 0) {
        count++;
        Thread.sleep(SLEEP_INTERVAL);    //wait for network to come up
      }
      else
        break;
    }
    return broadcastList;
  }
  
  public static void main(String[] args) throws Exception {
    String brdcstaddr="";
    InetAddress hostaddr = InetAddress.getLocalHost();
    String hostname = hostaddr.getHostName();
    String msg="HELLO from "+hostname;
    int port=BRDCST_PORT;

    if (args.length == 2) {
      port=Integer.parseInt(args[0]);
      msg=args[1];
    }
    if (args.length == 1) {
        port=Integer.parseInt(args[0]);
    }
    //System.out.println("message: "+msg+" port "+port);

    List<InetAddress> brdcstlist=listAllBroadcastAddresses();
    if (brdcstlist.size() > 0) {    //if list=0, something is wrong!
      for (int i=0; i < REPEATS; i++) {
        for (InetAddress brdcst : brdcstlist) {
          brdcstaddr=brdcst.getHostAddress();
          //System.out.println("broadcast addr: "+brdcstaddr);
          sendUDPMessage(msg,brdcstaddr, port);
        }
        Thread.sleep(SLEEP_INTERVAL);
      }
    }
    else
      System.out.println("No network broadcast addresses found.");
  }
}
