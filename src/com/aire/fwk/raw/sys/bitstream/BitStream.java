package com.aire.fwk.raw.sys.bitstream;

import java.io.Serializable;
import java.util.BitSet;

/**
 * A bit stream class. The class stores a series of bits in a highly storage-optimised form, and can be manipulated by the readers and
 * writers.
 * @author AireL
 */
public class BitStream implements Serializable
{
	/**
	 * Generated serial id
	 */
	private static final long serialVersionUID = 5071687193767514809L;

	protected BitSet coreData;
	
	protected final int dataSize;

	/**
	 * Generates a new final bit stream from the data provided
	 * @param data		The data to set
	 * @param dataSize	The length of the data
	 */
	public BitStream(BitSet data, int dataSize)
	{
		this.coreData = data;
		this.dataSize = dataSize;
	}
	
	/**
	 * Returns the underlying bitset attached to this BitStream
	 * @return
	 */
	public BitSet getData()
	{
		return this.coreData;
	}
	
	/**
	 * Returns the size of this BitStream
	 * @return
	 */
	public int getDataSize()
	{
		return this.dataSize;
	}
}
