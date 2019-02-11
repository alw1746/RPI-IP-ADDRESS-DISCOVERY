# RPI-IP-ADDRESS-DISCOVERY

A common problem with bringing the Raspberry Pi computer and a laptop to a meetup is not being able to connect to the RPi without going through a serial console login to get its ip address. The RPi is allocated an ip address by the router's dhcp server but only the network admin has logon rights to the router to view the dhcp client list.

A simple solution is to run a Java program on startup and broadcast the ip address using UDP packets for a period of time. A UDP client program on a laptop connected to the same lan segment listens to the UDP port for the message and displays it on the screen. ssh or vnc can then use the address for connection.

Raspberry Pi
------------

-login as pi user  
-Download UDP server program UDPSendMsg.java to /home/pi and compile it.  

**javac UDPSendMsg.java**

-create a crontab entry for reboot action.

**crontab -e  
@reboot /usr/bin/java UDPSendMsg > /tmp/UDPSendMsg.log 2>&1**

-On startup, the program will automatically broadcast the UDP message for 200secs at 5-sec interval.

Laptop
------

-Download UDP client program UDPRecvMsg.java and compile it.  

**javac UDPRecvMsg.java**

-Run the UDP client program first before starting up the RPi.

**java UDPRecvMsg**

-The ip address will be displayed upon reception of the UDP broadcast eg:

**HELLO from berry1@192.168.20.101:9876**

Program parameters
------------------
<pre>
UDPSendMsg [port] ["msg"]
    port    any non-system UDP port eg. 34523 (default=9876).  
    msg     optional string which overrides the default "HELLO from <hostname>" msg.
  
UDPRecvMsg [port]
    port    any non-system UDP port eg. 34523 (default=9876). Must match UDPSendMsg's port.

Run this Linux command to list current UDP ports in use:

 netstat --udp -an
</pre>
 
