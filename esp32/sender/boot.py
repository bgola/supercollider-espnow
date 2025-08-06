import network
import espnow
import random
import time

from uosc.client import create_message

sta = network.WLAN(network.WLAN.IF_STA)
sta.active(True)
sta.disconnect()

e = espnow.ESPNow()
e.active(True)

bcast = b'\xff' * 6  
e.add_peer(bcast)

while True:
    e.send(peer, create_message("/hello", "world", 42, random.random() * 4))
    time.sleep(t)
