package cc.wulian.app.model.device.utils;

/**
 * when we creating or use device will come across some error, throw with this exception
 */
public class DeviceException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public DeviceException( String detailMessage, Throwable throwable )
	{
		super(detailMessage, throwable);
	}

	public DeviceException( String detailMessage )
	{
		super(detailMessage);
	}

	public DeviceException( Throwable throwable )
	{
		super(throwable);
	}

}
