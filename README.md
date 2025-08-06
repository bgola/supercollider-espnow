# supercollider-espnow

Small sclang library to read OSC messages from the serial port and treat them register them as OSC messages (so you can use OSCFunc / OSCdef as you would with network messages). 
This is still very much a proof of concept, but it already works for very basic use cases.

Usage:

```
// Enable OSC message trace
OSCFunc.trace(true);

// Receiver is on serial port /dev/ttyACM0
e = ESPNow.new("/dev/ttyACM1");
```

In the folder `esp32/` there are basic examples for both `receiver` and `sender` written in Python. 
You will need to flash MicroPython https://micropython.org/ on your esp32 board and also micropython-osc (uosc) for the sender.

Clone (or download) the https://github.com/SpotlightKid/micropython-osc/ repository and put the `uosc` folder in your sender esp32 (together with the `boot.py`).

# TODO

- add support for other OSC typetags (only integer, string and float are currently supported)
- sending OSC message from SC to ESPNow via a new object ESPNowAddr that implements the NetAddr.sendMsg API
- maybe decouple the whole OSC parsing into a new sclang-based OSC implementation (LangOSC quark or something like that)
