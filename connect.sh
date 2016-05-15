#!/bin/bash

adb shell ip route > addrs.txt

ip_addrs=$(awk {'if( NF >=9){print $9;}'} addrs.txt)

rm addrs.txt

adb tcpip 5555

adb connect "$ip_addrs:5555" && echo "you can remove the cable now."
