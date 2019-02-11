# RPI-IP-ADDRESS-DISCOVERY

A common problem with bringing the Raspberry Pi comnputer and a laptop to a meetup is not being able to connect to the RPi without going through a serial console login to get its ip address. The RPi is allocated an ip address by the router's dhcp server but only the network admin has logon rights to the router to view the dhcp client list.

A simple solution is to run a pre-loaded Java program on RPi startup and broadcast the ip address using UDP packets for a period of time. A UDP client program on a laptop connected to the same lan segment listens to the UDP port for the message and displays it on the screen.

