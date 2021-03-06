# RPI-IP-ADDRESS-DISCOVERY

A common problem with bringing the Raspberry Pi computer and a laptop to a meetup is not being able to connect to the RPi without going through a serial console login to get its ip address. The RPi is allocated an ip address by the router's dhcp server but only the network admin has logon rights to the router to view the dhcp client list.

A simple solution is to run a Java program on startup and broadcast the ip address using UDP packets for a period of time. A UDP client program on a laptop connected to the same lan segment listens to the UDP port for the message and displays it on the screen. ssh or vnc can then use the address for connection. Tested on Raspbian Stretch, Ubuntu 16, Windows 10.

Raspberry Pi
------------

-Login as pi user  
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

Program options
---------------
<pre>
UDPSendMsg [port] ["msg"]  
    port    any non-system UDP port eg. 34523 (default=9876).  
    msg     optional string which overrides the default "HELLO from <hostname>" msg.
  
UDPRecvMsg [port]
    port    any non-system UDP port eg. 34523 (default=9876). Must match UDPSendMsg's port.
</pre>

Useful commands
---------------
<pre>
 netstat --udp -an       #Linux list current UDP ports in use
 netstat -p UDP -an      #Windows
 ifconfig                #Linux show network interfaces and broadcast addresses
 ipconfig /all           #Windows
</pre>
 
 Windows 10 Firewall rules
--------------------------
By default Windows 10 Firewall rules block the Java runtime from receiving UDP broadcasts for a network's public profile. Follow the steps below to modify the rule.  

-get java runtime version on PC.
<pre>
java -version
java version "1.8.0_181"
</pre>
-verify profile of Active network interface (private or public).

**Control Panel -> Network and Sharing Center**

-Check the Windows Firewall rules for connected profile.

**Control Panel -> Windows Defender Firewall**

**Advanced Settings -> Inbound Rules**

-Look for line that corresponds to the java version, profile, action and protocol:
<pre>
Name                           Profile  Action  Program                                           Protocol
Java(TM) Platform SE binary    Public   Block   C:\program files\java\jdk1.8.0_181\bin\java.exe   UDP
</pre>
-Change current Action from Block to Allow. If there are duplicate rules, Block will override any other Allows.

**Rightclick on rule, select Properties.  
Select Action:  Allow the connection**

-Inbound UDP broadcasts are now permitted in Windows firewall for Java program testing.

-Note, this will permit any Java program to receive UDP broadcasts from any source.

