import network
import espnow
import struct
import sys

sta = network.WLAN(network.WLAN.IF_STA)
sta.active(True)

e = espnow.ESPNow()
e.active(True)

while True:
    host, msg = e.recv()
    if msg:
        # add the length of the message (OSC spec-1.0 for when sending OSC over TCP)
        msg = struct.pack('i', len(msg)) + msg
        sys.stdout.buffer.write(msg)
