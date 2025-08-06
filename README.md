# supercollider-espnow

Small sclang library to read OSC messages from the serial port and register them as regular OSC messages (so you can use `OSCFunc` / `OSCdef` as you would with network messages). 

*This is still very much a proof of concept but I thought it might be useful for some people.*

Usage:

```
// Enable OSC message trace
OSCFunc.trace(true);

// Receiver is on serial port /dev/ttyACM0
e = ESPNow.new("/dev/ttyACM0");

// to stop receiving:
e.serialport.close;
```

In the folder `esp32/` there are basic examples for both `receiver` and `sender` written in Python. 
You will need to flash https://micropython.org/ on your esp32 board and also [micropython-osc](https://github.com/SpotlightKid/micropython-osc/) for the sender.

Clone (or download) the micropython-osc repository and add the `uosc` folder in your sender esp32 (together with `boot.py`).

# TODO

- add support for other OSC typetags (only integer, string and float are currently supported)
- sending OSC message from SC to ESPNow via a new object ESPNowAddr that implements the NetAddr.sendMsg API
- maybe decouple the whole OSC parsing into a new sclang-based OSC implementation (LangOSC quark or something like that)
