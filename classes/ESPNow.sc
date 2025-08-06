ESPNow {
	var <serialport, <addr, readRate=0.005;
	var <routine;

	*new { arg port;
		if (port.isKindOf(SerialPort).not) {
			port = SerialPort.new(port, 115200);
		};
		^super.newCopyArgs(port).init;
	}

	parseString { arg stream;
		var ch, string="", read=0;
		while ({ch != 0x00}) {
			ch = stream.next;
			string = string ++ ch.asAscii;
			read = read + 1 % 4;
		};
		while {read != 0} { stream.next; read = read + 1 % 4 };
		^string;
	}

	parseOSC { arg stream;
		var ch, typetags;
		var address="", arguments=[];

		while ({ch != $,.asInteger}) {
			ch = stream.next;
			address = address ++ ch.asAscii;
		};

		// grab the comma back for reading the typetag
		address = address.drop(-1).asSymbol;
		stream.rewind(1);

		typetags = this.parseString(stream);
		typetags[1..].do { |tag|
			var ret = 0;
			if (tag.asAscii == $i) {
				ret = stream.getInt32;
			};
			if (tag.asAscii == $s) {
				ret = this.parseString(stream).asSymbol;
			};
			if (tag.asAscii == $f) {
				ret = stream.getFloat;
			};
			arguments = arguments.add(ret);
		};
		^([address] ++ arguments)
	}

	readSerialRoutineFunc {
		var coll = CollStream([]), state = 0, readPos=0;
		var length = 0, byte;
		loop {
			if (state == 0) {
				byte = serialport.next;
				if (byte.notNil) { coll.put(byte) };
				while ({ coll.collection.size < 4}) {
					byte = serialport.next;
					if (byte.notNil) { coll.put(byte) };
					// waits a bit before polling again
					0.001.wait;
				};

				coll.pos = readPos;
				length = coll.getInt32LE;
				if (length >= 1 and: {length <= 4096}) {
					state = 1;
				} {
					readPos = readPos + 1;
				};
			} {
				// stores current reading position so we can go back in case of
				// parsing failure
				readPos = coll.pos;

				length.do {
					var byte = serialport.next;
					if (byte.isNil) {
						0.005.wait;
						byte = serialport.next;
					};

					if (byte.notNil) {
						coll.put(byte);
					};
				};

				// resets to read again
				coll.pos = readPos;
				if (coll.collection[readPos..].size >= length and: {coll.collection[coll.pos].asAscii == $/}) {
					readPos = 0;
					thisProcess.recvOSCmessage(
						0,
						NetAddr("localhost", 57120),
						NetAddr.langPort,
						this.parseOSC(coll));
					coll.collection = [];
					coll.pos = 0;
					readPos = 0;
				} {
					// rollback
					coll.pos = readPos-3;
					coll.collection[coll.pos..coll.pos+3].postln;
				};
				state = 0;
			};
			readRate.wait;
		}
	}

	init {
		routine = Routine({this.readSerialRoutineFunc}).play;
	}

}

+ CollStream {
	getInt32LE {
		^this.getInt8
		.bitOr(this.getInt8.leftShift(8))
		.bitOr(this.getInt8.leftShift(16))
		.bitOr(this.getInt8.leftShift(24))
	}
}