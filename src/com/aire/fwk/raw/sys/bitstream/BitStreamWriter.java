package com.aire.fwk.raw.sys.bitstream;

import java.util.BitSet;

/**
 * Class to enable writing to a BitStream. Contains functions to append and set data, as well as basic clear and padding functions.
 * 
 * @author AireL
 */
public class BitStreamWriter extends BitStream
{
	/**
	 * Generated serial ID
	 */
	private static final long serialVersionUID = -2024951673517071537L;
	
	private static final int DEFAULT_BIT_LENGTH = 81920;
	private static final int MAX_BYTE = 0xFF;
	private static final int BYTE_LENGTH = 8;
			
	private int currentLocation = 0;
	
	/**
	 * Constructor - uses a default size for the bitset
	 */
	public BitStreamWriter()
	{
		super(new BitSet(DEFAULT_BIT_LENGTH), DEFAULT_BIT_LENGTH);
	}
	
	/**
	 * Constructor, uses the entered size for the bitset
	 * @param dataSize	The bitSet size 
	 */
	public BitStreamWriter(int dataSize)
	{
		super(new BitSet(DEFAULT_BIT_LENGTH), dataSize);
	}
	
	/**
	 * Copy constructor
	 * @param copyFrom	Copy target
	 */
	public BitStreamWriter(BitStreamWriter copyFrom)
	{
		super(copyFrom.coreData, copyFrom.dataSize);
	}
	
	/**
	 * Creates a writer from the BitStream passed in
	 * @param data	BitStream to write
	 */
	public BitStreamWriter(BitStream data)
	{
		super(data.getData(),data.getDataSize());
	}
	
	/**
	 * Generally for byte structures - passes in the first X bits of a byte
	 * @param data
	 * @param bitLength
	 */
	private void appendByteLeft(byte data, int bitLength)
	{
		for (int i = 1; i <= bitLength; i++)
		{
			coreData.set(currentLocation, ((data >>> (BYTE_LENGTH - i)) % 2) == 1);
			currentLocation++;
		}
	}
	
	/**
	 * Generally for primitives - passes in X bits, where the Xth bit is always the rightmost bit.
	 * @param data
	 * @param bitLength
	 */
	private void appendByteRight(byte data, int bitLength)
	{
		for (int i = 1; i <= bitLength; i++)
		{
			coreData.set(currentLocation, ((data >>> (bitLength - i)) % 2) == 1);
			currentLocation++;
		}
	}
	
	/**
	 * Generally for byte structures - passes in the first X bits of a byte
	 * @param data
	 * @param bitLength
	 * @param startPos		Offset from where to start writing
	 */
	private void setByteLeft(byte data, int bitLength, int startPos)
	{
		for (int i = 1; i <= bitLength; i++)
		{
			coreData.set(startPos, ((data >>> (BYTE_LENGTH - i)) % 2) == 1);
			startPos++;
		}
	}
	
	/**
	 * Generally for primitives - passes in X bits, where the Xth bit is always the rightmost bit.
	 * @param data
	 * @param bitLength
	 * @param startPos		Offset from where to start writing
	 */
	private void setByteRight(byte data, int bitLength, int startPos)
	{
		for (int i = 1; i <= bitLength; i++)
		{
			coreData.set(startPos, ((data >>> (bitLength - i)) % 2) == 1);
			startPos++;
		}
	}
	
	/**
	 * Appends data to the BitStream
	 * @param data	The data to append
	 */
	public void append(Boolean data)
	{
		checkRange(1);
		Byte b = 0;
		if (data != null && data == true) b = 1;
		this.appendByteRight(b, 1);
	}
	
	/**
	 * Appends data to the BitStream
	 * @param data		The data to append
	 * @param bitLength	The amount of bits to append
	 */
	public void append(Byte data, int bitLength)
	{
		checkRange(bitLength);
		if (data == null) data = 0;
		if (bitLength < 1 || bitLength > BYTE_LENGTH) throw new IllegalArgumentException("Error, bitLength: " + bitLength + " is out of range for a byte");
		this.appendByteLeft(data, bitLength);
	}
	
	/**
	 * Appends data to the BitStream
	 * @param data	The data to append
	 * @param bitLength	The amount of bits to append
	 */
	public void append(byte[] data, int bitLength)
	{
		checkRange(bitLength);
		if (bitLength < 1 || bitLength > (BYTE_LENGTH * data.length)) throw new IllegalArgumentException("Error, bitLength: " + bitLength + " is out of range for a byte array of length: " + data.length);
		for (int i = 1; i <= data.length; i++)
		{
			this.appendByteLeft(data[(data.length - i)], bitLength);
		}
	}
	
	/**
	 * Appends data to the BitStream
	 * @param data	The data to append
	 * @param bitLength	The amount of bits to append
	 */
	public void append(Byte[] data, int bitLength)
	{
		checkRange(bitLength);
		if (data == null) data = new Byte[]{0};
		if (bitLength < 1 || bitLength > (BYTE_LENGTH * data.length)) throw new IllegalArgumentException("Error, bitLength: " + bitLength + " is out of range for a byte array of length: " + data.length);
		for (int i = 1; i <= data.length; i++)
		{
			this.appendByteLeft(data[(data.length - i)], bitLength);
		}
	}
	
	/**
	 * Appends data to the BitStream
	 * @param data	The data to append
	 * @param bitLength	The amount of bits to append
	 */
	public void append(Integer data, int bitLength)
	{
		checkRange(bitLength);
		if (data == null) data = 0;
		if (bitLength < 1 || bitLength > BYTE_LENGTH*4) throw new IllegalArgumentException("Error, bitLength: " + bitLength + " is out of range for an integer");
		int bitsRemaining = bitLength;
		while (bitsRemaining >= BYTE_LENGTH)
		{
			bitsRemaining =- BYTE_LENGTH;
			this.appendByteRight((byte) ((data >>> bitsRemaining) & MAX_BYTE), BYTE_LENGTH);
		}
		this.appendByteRight((byte) (data & MAX_BYTE), bitLength % BYTE_LENGTH);
	}
	
	/**
	 * Appends data to the BitStream
	 * @param data	The data to append
	 * @param bitLength	The amount of bits to append
	 */
	public void append(Long data, int bitLength)
	{
		checkRange(bitLength);
		if (data == null) data = 0L;
		if (bitLength < 1 || bitLength > BYTE_LENGTH*8) throw new IllegalArgumentException("Error, bitLength: " + bitLength + " is out of range for a long");
		int bitsRemaining = bitLength;
		while (bitsRemaining >= BYTE_LENGTH)
		{
			bitsRemaining =- BYTE_LENGTH;
			this.appendByteRight((byte) ((data >>> bitsRemaining) & MAX_BYTE), BYTE_LENGTH);
		}
		this.appendByteRight((byte) (data & MAX_BYTE), bitLength % BYTE_LENGTH);
	}
	
	/**
	 * Appends data to the BitStream
	 * @param data	The data to append
	 */
	public void append(Character data)
	{
		checkRange(2 * BYTE_LENGTH);
		if (data == null) data = 0;
		this.appendByteLeft((byte)(data.charValue() >>> 8), BYTE_LENGTH);
		this.appendByteLeft((byte)data.charValue(), BYTE_LENGTH);
	}
	
	/**
	 * Appends data to the BitStream
	 * @param data	The data to append
	 */
	public void append(String data)
	{
		checkRange(data.getBytes().length);
		if(data.length() < 1) throw new IllegalArgumentException("Error, the dataset is empty");
		this.append(data.getBytes(), data.getBytes().length * BYTE_LENGTH);
	}
	
	/**
	 * Appends data to the BitStream
	 * @param data	The data to append
	 */
	public void append(BitSet data)
	{
		checkRange(data.length());
		if(data.length() < 1) throw new IllegalArgumentException("Error, the dataset is empty");
		for (int i = 0; i < data.length(); i++)
		{
			this.append(data.get(i));
		}
	}
	
	/**
	 * Appends data to the BitStream
	 * @param data	The data to append
	 * @param bitLength	The number of bits to read
	 */
	public void append(BitStream data, int bitLength)
	{
		checkRange(bitLength);
		if (bitLength < 1 || bitLength > data.getDataSize()) throw new IllegalArgumentException("Error, bitLength: " + bitLength +
				" is out of range for a BitStream of length " + data.getDataSize());
		BitStreamReader bsr = new BitStreamReader(data);
		this.append(bsr.readBytes(bitLength), bitLength);
	}
	
	/**
	 * Sets data in the BitStream
	 * @param data		The data to set
	 * @param startPos	The starting position to set from
	 */
	public void set(Boolean data, int startPos)
	{
		checkRange(1, startPos);
		Byte b = 0;
		if (data != null && data == true) b = 1;
		this.setByteRight(b, 1, startPos);
	}
	
	/**
	 * Sets data in the BitStream
	 * @param data		The data to set
	 * @param bitLength	The amount of bits to append
	 * @param startPos	The starting position to set from
	 */
	public void set(Byte data, int bitLength, int startPos)
	{
		checkRange(bitLength, startPos);
		if (data == null) data = 0;
		if (bitLength < 1 || bitLength > BYTE_LENGTH) throw new IllegalArgumentException("Error, bitLength: " + bitLength + " is out of range for a byte");
		this.setByteLeft(data, bitLength, startPos);
	}
	
	/**
	 * Sets data in the BitStream
	 * @param data		The data to set
	 * @param bitLength	The amount of bits to append
	 * @param startPos	The starting position to set from
	 */
	public void set(byte[] data, int bitLength, int startPos)
	{
		checkRange(bitLength, startPos);
		if (bitLength < 1 || bitLength > (BYTE_LENGTH * data.length)) throw new IllegalArgumentException("Error, bitLength: " + bitLength + " is out of range for a byte array of length: " + data.length);
		
		int bitsRemaining = bitLength;
		int counter = 0;
		while (bitsRemaining >= BYTE_LENGTH)
		{
			bitsRemaining =- BYTE_LENGTH;
			this.setByteLeft((data[counter]), BYTE_LENGTH, startPos + counter * BYTE_LENGTH);
			counter++;
		}
		this.setByteLeft(data[counter], bitLength % BYTE_LENGTH, startPos + counter * BYTE_LENGTH);
	}
	
	/**
	 * Sets data in the BitStream
	 * @param data		The data to set
	 * @param bitLength	The amount of bits to append
	 * @param startPos	The starting position to set from
	 */
	public void set(Byte[] data, int bitLength, int startPos)
	{
		checkRange(bitLength, startPos);
		if (data == null) data = new Byte[]{0};
		if (bitLength < 1 || bitLength > (BYTE_LENGTH * data.length)) throw new IllegalArgumentException("Error, bitLength: " + bitLength + " is out of range for a byte array of length: " + data.length);
		
		int bitsRemaining = bitLength;
		int counter = 0;
		while (bitsRemaining >= BYTE_LENGTH)
		{
			bitsRemaining =- BYTE_LENGTH;
			this.setByteLeft((data[counter]), BYTE_LENGTH, startPos + counter * BYTE_LENGTH);
			counter++;
		}
		this.setByteLeft(data[counter], bitLength % BYTE_LENGTH, startPos + counter * BYTE_LENGTH);
	}
	
	/**
	 * Sets data in the BitStream
	 * @param data		The data to set
	 * @param bitLength	The amount of bits to append
	 * @param startPos	The starting position to set from
	 */
	public void set(Integer data, int bitLength, int startPos)
	{
		checkRange(bitLength, startPos);
		if (data == null) data = 0;
		if (bitLength < 1 || bitLength > BYTE_LENGTH*4) throw new IllegalArgumentException("Error, bitLength: " + bitLength + " is out of range for an integer");
		int bitsRemaining = bitLength;
		int counter = 0;
		while (bitsRemaining >= BYTE_LENGTH)
		{
			bitsRemaining =- BYTE_LENGTH;
			this.setByteRight((byte) ((data >>> bitsRemaining) & MAX_BYTE), BYTE_LENGTH, startPos + counter * BYTE_LENGTH);
			counter++;
		}
		this.setByteRight((byte) (data & MAX_BYTE), bitLength % BYTE_LENGTH, startPos + counter * BYTE_LENGTH);
	}
	
	/**
	 * Sets data in the BitStream
	 * @param data		The data to set
	 * @param bitLength	The amount of bits to append
	 * @param startPos	The starting position to set from
	 */
	public void set(Long data, int bitLength, int startPos)
	{
		checkRange(bitLength, startPos);
		if (data == null) data = 0L;
		if (bitLength < 1 || bitLength > BYTE_LENGTH*8) throw new IllegalArgumentException("Error, bitLength: " + bitLength + " is out of range for a long");
		int bitsRemaining = bitLength;
		int counter = 0;
		while (bitsRemaining >= BYTE_LENGTH)
		{
			bitsRemaining =- BYTE_LENGTH;
			this.setByteRight((byte) ((data >>> bitsRemaining) & MAX_BYTE), BYTE_LENGTH, startPos + counter * BYTE_LENGTH);
			counter++;
		}
		this.setByteRight((byte) (data & MAX_BYTE), bitLength % BYTE_LENGTH, startPos + counter * BYTE_LENGTH);
	}
	
	/**
	 * Sets data in the BitStream
	 * @param data		The data to set
	 * @param startPos	The starting position to set from
	 */
	public void set(Character data, int startPos)
	{
		checkRange(2 * BYTE_LENGTH, startPos);
		if (data == null) data = 0;
		this.setByteLeft((byte)(data.charValue() >>> 8), BYTE_LENGTH, startPos);
		this.setByteLeft((byte)data.charValue(), BYTE_LENGTH, startPos + BYTE_LENGTH);
	}
	
	/**
	 * Sets data in the BitStream
	 * @param data		The data to set
	 * @param startPos	The starting position to set from
	 */
	public void set(String data, int startPos)
	{
		checkRange(data.getBytes().length, startPos);
		if(data.length() < 1) throw new IllegalArgumentException("Error, the dataset is empty");
		this.set(data.getBytes(), data.getBytes().length * BYTE_LENGTH, startPos);
	}
	
	/**
	 * Sets data in the BitStream
	 * @param data		The data to set
	 * @param startPos	The starting position to set from
	 */
	public void set(BitSet data, int startPos)
	{
		checkRange(data.length(), startPos);
		if(data.length() < 1) throw new IllegalArgumentException("Error, the dataset is empty");
		for (int i = 0; i < data.length(); i++)
		{
			this.set(data.get(i), startPos + i * BYTE_LENGTH);
		}
	}
	
	/**
	 * Sets data in the BitStream
	 * @param data		The data to set
	 * @param startPos	The starting position to set from
	 */
	public void set(BitStream data, int bitLength, int startPos)
	{
		checkRange(bitLength, startPos);
		if (bitLength < 1 || bitLength > data.getDataSize()) throw new IllegalArgumentException("Error, bitLength: " + bitLength +
				" is out of range for a BitStream of length " + data.getDataSize());
		BitStreamReader bsr = new BitStreamReader(data);
		this.set(bsr.readBytes(bitLength), bitLength, startPos);

	}

	/**
	 * Pads the bitStream with a number of 0s
	 * @param bits	The number of 0s to add
	 */
	public void pad(int bits)
	{
		for (int i = 0; i < bits; i++)
		{
			this.append(false);
		}
	}
	
	/**
	 * sets a number of bits to 0
	 * @param bits		The amount of bits to set to 0
	 * @param startPos	The start position to clear from
	 */
	public void clear(int bits, int startPos)
	{
		for (int i = 0; i < bits; i++)
		{
			this.set(false, startPos + i);
		}
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
	 * Generates and returns a BitStream using the current data
	 * @return
	 */
	public BitStream getBitStream()
	{
		return new BitStream(this.coreData,this.dataSize);
	}

	/**
	 * validates the range to ensure the entry is in range
	 * @param range	Number of bits to check for
	 */
	private void checkRange(int range)
	{
		if (this.currentLocation + range > this.dataSize) throw new IndexOutOfBoundsException("Error, adding " + range + " to the "
				+ "dataset will cause the BitStream to go out of bounds. There are " + (this.dataSize - this.currentLocation) + 
				" bits remaining");
	}
	
	/**
	 * validates the range to ensure the entry is in range
	 * @param range		Number of bits to check for
	 * @param startPos	The start position to check from
	 */
	private void checkRange(int range, int startPos)
	{
		if (startPos + range > this.dataSize) throw new IndexOutOfBoundsException("Error, adding " + range + " to the "
				+ "dataset will cause the BitStream to go out of bounds. There are " + (this.dataSize - startPos) + 
				" bits remaining");
	}
}
