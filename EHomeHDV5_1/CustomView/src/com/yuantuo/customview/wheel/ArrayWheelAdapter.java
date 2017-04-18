package com.yuantuo.customview.wheel;

/**
 * The simple Array wheel adapter
 * 
 * @param <T>
 *          the element type
 */
public class ArrayWheelAdapter implements WheelAdapter
{

	/** The default items length */
	public static final int DEFAULT_LENGTH = -1;

	// items
	private String items[];
	// length
	private int length;

	public ArrayWheelAdapter( String items[], int length )
	{
		this.items = items;
		this.length = length;
	}

	public ArrayWheelAdapter( String items[] )
	{
		this(items, DEFAULT_LENGTH);
	}

	@Override
	public String getItem( int index )
	{
		if (index >= 0 && index < items.length) { return String.valueOf(items[index]); }
		return null;
	}

	@Override
	public int getItemsCount()
	{
		return items.length;
	}

	@Override
	public int getMaximumLength()
	{
		return length;
	}

}
