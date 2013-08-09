package com.aire.fwk.raw.sys.bitstream;

import java.util.BitSet;

/**
 * A class to facilitate higher level reading of bitstreams. Options allow you to read throw in a linear fashion to get
 * bits from any point in the bitstream.
 * 
 * @author AireL
 */
public class BitStreamReader extends BitStream
{
	/**
	 * Generated serial version
	 */
	private static final long serialVersionUID = 4116599365394815691L;

	/**
	 * Byte length as a static int
	 */
	private static final int BYTE_LENGTH = 8;
	
	/**
	 * Current location of the reader
	 */
	private int currentLocation = 0;
	
	/**
	 * General constructor, takes in a bit stream
	 * @param data	The bit stream to read
	 */
	public BitStreamReader(BitStream data)
	{
		super(data.getData(),data.getDataSize());
	}
	
	/**
	 * General constructor, takes in a bitset and builds a reader.
	 * @param data
	 */
	public BitStreamReader(BitSet data)
	{
		super(data, data.length());
	}
	
	/**
	 * private method to read bits from the bitset. Reads from the most significant bit
	 * @param bits The amount of bits to read
	 * @return	Returns a byte holding the number of bits read
	 */
	private byte readBitsLeft(int bits)
	{
		byte returnVal = 0;
		for (int i = 1; i <= bits; i++)
		{
			if (this.coreData.get(this.currentLocation))
			{
				returnVal += (1 << (BYTE_LENGTH - i));
			}
			currentLocation++;
		}
		return returnVal;
	}
	
	/**
	 * private method to read bits from the bitset. Reads so that the last bit returned is the least significant
	 * bit in the byte
	 * @param bits	Number of bits to read
	 * @return	Returns a byte holding the number of bits read
	 */
	private byte readBitsRight(int bits)
	{
		byte returnVal = 0;
		for (int i = 1; i <= bits; i++)
		{
			if (this.coreData.get(this.currentLocation))
			{
				returnVal += (1 << (bits - i));
			}
			currentLocation++;
		}
		return returnVal;
	}

	/**
	 * private method to get bits from the bitset starting at an offset. Reads from the most significant bit
	 * @param bits 		The amount of bits to read
	 * @param startPos	The starting position to read from
	 * @return	Returns a byte holding the number of bits read
	 */
	private byte getBitsLeft(int bits, int startPos)
	{
		byte returnVal = 0;
		for (int i = 1; i <= bits; i++)
		{
			if (this.coreData.get(startPos))
			{
				returnVal += (1 << (BYTE_LENGTH - i));
			}
			startPos++;
		}
		return returnVal;
	}
	
	/**
	 * private method to get bits from the bitset starting at an offset. Reads so that the last bit returned 
	 * is the least significant bit in the byte
	 * @param bits 		The amount of bits to read
	 * @param startPos	The starting position to read from
	 * @return	Returns a byte holding the number of bits read
	 */
	private byte getBitsRight(int bits, int startPos)
	{
		byte returnVal = 0;
		for (int i = 1; i <= bits; i++)
		{
			if (this.coreData.get(startPos))
			{
				returnVal += (1 << (bits - i));
			}
			startPos++;
		}
		return returnVal;
	}
	
	/**
	 * reads and returns a boolean
	 * @return	A boolean - true if the bit is 1, false if the bit is 0
	 */
	public boolean readBoolean()
	{
		checkValidRead(1);
		boolean returnVal = false;
		if (this.readBitsRight(1) % 2 == 1) returnVal = true;
		return returnVal;
	}
	
	/**
	 * Reads and returns x bits as a byte
	 * @param length	The number of bits to read
	 * @return	A byte containing the number of read bits
	 */
	public byte readByte(int length)
	{
		checkValidRead(length);
		if (length < 1 || length > 8) throw new IllegalArgumentException("Error, bitLength: " + length +
				" is out of range for a byte");
		return this.readBitsLeft(length);
	}
	
	/**
	 * Reads and returns x bits as a byte array.
	 * @param length	The number of bits to read
	 * @return	A byte array of length (ceil (length / 8))
	 */
	public byte[] readBytes(int length)
	{
		checkValidRead(length);
		if (length < 1) throw new IllegalArgumentException("Error, bitLength: " + length +
				" is out of range for a byte array");
		int arraySize = (int) Math.ceil(((double)length) / BYTE_LENGTH);
		byte[] returnVal = new byte[arraySize];
		for (int i = 0; i < (arraySize - 1); i++)
		{
			returnVal[i] = this.readBitsLeft(BYTE_LENGTH);
		}
		returnVal[(arraySize - 1)] = this.readBitsLeft(length % BYTE_LENGTH);
		return returnVal;
	}
	
	/**
	 * Reads and returns an integer, using a set number of bits
	 * @param length	The number of bits to read 
	 * @return an integer compromising the number of bits read
	 */
	public int readInt(int length)
	{
		checkValidRead(length);
		if (length < 1 || length > BYTE_LENGTH * 4) throw new IllegalArgumentException("Error, bitLength: " + length +
				" is out of range for an integer");
		int arraySize = (int) Math.ceil(((double)length) / BYTE_LENGTH);
		int returnVal = 0;
		for (int i = 1; i <= (arraySize - 1); i++)
		{
			returnVal += (this.readBitsRight(BYTE_LENGTH) << (length - (i * BYTE_LENGTH)));
		}
		returnVal += this.readBitsLeft(length % BYTE_LENGTH);
		return returnVal;
	}
	
	/**
	 * Reads and returns a long, using a set number of bits
	 * @param length	The number of bits to read
	 * @return a long compromising the number of bits read
	 */
	public long readLong(int length)
	{
		if (length < 1 || length > 8 * BYTE_LENGTH) throw new IllegalArgumentException("Error, bitLength: " + length +
				" is out of range for a Long");
		checkValidRead(length);
		int arraySize = (int) Math.ceil(((double)length) / BYTE_LENGTH);
		long returnVal = 0L;
		for (int i = 1; i <= (arraySize - 1); i++)
		{
			returnVal += (this.readBitsRight(BYTE_LENGTH) << (length - (i * BYTE_LENGTH)));
		}
		returnVal += this.readBitsLeft(length % BYTE_LENGTH);
		return returnVal;
	}
	
	/**
	 * Reads and returns a char, using 16 bits
	 * @return	A char.
	 */
	public char readChar()
	{
		checkValidRead(16);
		char returnVal;
		byte[] byteData;
		byteData = this.readBytes(16);
		returnVal = (char)(byteData[1] << BYTE_LENGTH + byteData[0]);
		return returnVal;
	}
	
	/**
	 * Reads and returns a string, using 16 bits per character
	 * @param bits	The number of bits to read
	 * @return	A string of length (bits / 16)
	 */
	public String readString(int bits)
	{
		checkValidRead(bits);
		if (bits % (BYTE_LENGTH * 2) != 0) throw new IllegalArgumentException("Error: Bits must be a multiple of 16");
		return new String(this.readBytes(bits));
	}
	
	/**
	 * Reads and returns a bitset of length bits
	 * @param bits	The length of the bitset to return
	 * @return	A bitset
	 */
	public BitSet readBitSet(int bits)
	{
		checkValidRead(bits);
		BitStreamWriter bsw = new BitStreamWriter(bits);
		for (int i = 0; i < bits; i++)
		{
			bsw.append(this.readBoolean());
		}		
		return bsw.getData();
	}
	
	/**
	 * Gets a boolean at the specified position
	 * @param startPos	The position to read from
	 * @return	A boolean (true if the bit was 1, false if 0)
	 */
	public boolean getBoolean(int startPos)
	{
		checkValidGet(1, startPos);
		boolean returnVal = false;
		if (this.getBitsRight(1, startPos) % 2 == 1) returnVal = true;
		return returnVal;
	}
	
	/**
	 * Reads and returns x bits as a byte
	 * @param length	The number of bits to read
	 * @param startPos	The position to read from
	 * @return	A byte containing the number of read bits
	 */
	public byte getByte(int length, int startPos)
	{
		checkValidGet(length, startPos);
		if (length < 1 || length > 8) throw new IllegalArgumentException("Error, bitLength: " + length +
				" is out of range for a byte");
		return this.getBitsLeft(length, startPos);
	}
	
	/**
	 * Reads and returns x bits as a byte array.
	 * @param length	The number of bits to read
	 * @param startPos	The position to read from
	 * @return	A byte array of length (ceil (length / 8))
	 */
	public byte[] getBytes(int length, int startPos)
	{
		checkValidGet(length, startPos);
		if (length < 1) throw new IllegalArgumentException("Error, bitLength: " + length +
				" is out of range for a byte array");
		int arraySize = (int) Math.ceil(((double)length) / BYTE_LENGTH);
		byte[] returnVal = new byte[arraySize];
		for (int i = 0; i < (arraySize - 1); i++)
		{
			returnVal[i] = this.getBitsLeft(BYTE_LENGTH, startPos + i * BYTE_LENGTH);
		}
		returnVal[(arraySize - 1)] = this.getBitsLeft(length % BYTE_LENGTH, startPos + ((arraySize - 1) * BYTE_LENGTH));
		return returnVal;
	}
	
	/**
	 * Reads and returns an integer, using a set number of bits
	 * @param length	The number of bits to read 
	 * @param startPos	The position to read from
	 * @return an integer compromising the number of bits read
	 */
	public int getInt(int length, int startPos)
	{
		checkValidGet(length, startPos);
		if (length < 1 || length > BYTE_LENGTH * 4) throw new IllegalArgumentException("Error, bitLength: " + length +
				" is out of range for an integer");
		int arraySize = (int) Math.ceil(((double)length) / BYTE_LENGTH);
		int returnVal = 0;
		for (int i = 1; i <= (arraySize - 1); i++)
		{
			returnVal += (this.getBitsLeft(BYTE_LENGTH, startPos + (i - 1) * BYTE_LENGTH) << (length - (i * BYTE_LENGTH)));
		}
		returnVal += this.getBitsLeft(length % BYTE_LENGTH, startPos + (arraySize - 1) * BYTE_LENGTH);
		return returnVal;
	}
	
	/**
	 * Reads and returns a long, using a set number of bits
	 * @param length	The number of bits to read
	 * @param startPos	The position to read from
	 * @return a long compromising the number of bits read
	 */
	public long getLong(int length, int startPos)
	{
		checkValidGet(length, startPos);
		if (length < 1 || length > 8 * BYTE_LENGTH) throw new IllegalArgumentException("Error, bitLength: " + length +
				" is out of range for a Long");
		int arraySize = (int) Math.ceil(((double)length) / BYTE_LENGTH);
		long returnVal = 0L;
		for (int i = 1; i <= (arraySize - 1); i++)
		{
			returnVal += (this.getBitsLeft(BYTE_LENGTH, startPos + (i-1)*BYTE_LENGTH) << (length - (i * BYTE_LENGTH)));
		}
		returnVal += this.getBitsLeft(length % BYTE_LENGTH, startPos + (arraySize - 1) * BYTE_LENGTH);
		return returnVal;
	}
	
	/**
	 * Reads and returns a char, using 16 bits
	 * @param startPos	The position to read from
	 * @return	A char.
	 */
	public char getChar(int startPos)
	{
		checkValidGet(16, startPos);
		char returnVal;
		byte[] byteData;
		byteData = this.getBytes(16, startPos);
		returnVal = (char)(byteData[1] << BYTE_LENGTH + byteData[0]);
		return returnVal;
	}
	
	/**
	 * Reads and returns a string, using 16 bits per character
	 * @param bits		The number of bits to read
	 * @param startPos	The position to read from
	 * @return	A string of length (bits / 16)
	 */
	public String getString(int bits, int startPos)
	{
		checkValidGet(bits, startPos);
		if (bits % (BYTE_LENGTH * 2) != 0) throw new IllegalArgumentException("Error: Bits must be a multiple of 16");
		return new String(this.getBytes(bits, startPos));
	}
	
	/**
	 * Reads and returns a bitset of length bits
	 * @param bits		The length of the bitset to return
	 * @param startPos	The position to read from
	 * @return	A bitset
	 */
	public BitSet getBitSet(int bits, int startPos)
	{
		checkValidGet(bits, startPos);
		BitStreamWriter bsw = new BitStreamWriter(bits);
		for (int i = 0; i < bits; i++)
		{
			bsw.append(this.getBoolean(startPos + i));
		}		
		return bsw.getData();
	}
	
	/**
	 * Returns the current location of the writer
	 * @return
	 */
	public int getCurrentLocation() 
	{
		return this.currentLocation;
	}
	
	/**
	 * Sets the current location of the writer
	 * @param currentLocation
	 */
	public void setCurrentLocation(int currentLocation) 
	{
		this.currentLocation = currentLocation;
	}
	
	/**
	 * Skips the next bits in the reader
	 * @param bits	The number of bits to skip
	 */
	public void skip(int bits)
	{
		this.currentLocation += bits;
	}

	/**
	 * Validates the read to ensure there are enough bits remaining to complete a read
	 * @param bits
	 */
	private void checkValidRead(int bits)
	{
		if (this.currentLocation + bits > this.dataSize) throw new IndexOutOfBoundsException("Error, reading " + bits + " from the "
				+ "dataset will cause the BitStream to go out of bounds. There are " + (this.dataSize - this.currentLocation) + 
				" bits remaining");
	}
	
	/**
	 * Validates the get to ensure there are enough bits remaining to get.
	 * @param bits
	 * @param startPos
	 */
	private void checkValidGet(int bits, int startPos)
	{
		if (startPos + bits > this.dataSize) throw new IndexOutOfBoundsException("Error, reading " + bits + " from the "
				+ "dataset will cause the BitStream to go out of bounds. There are " + (this.dataSize - startPos) + 
				" bits remaining");
	}
}
