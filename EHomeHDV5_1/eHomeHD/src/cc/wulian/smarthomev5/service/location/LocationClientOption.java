package cc.wulian.smarthomev5.service.location;

public final class LocationClientOption
{
  public static final int MIN_SCAN_SPAN = 1000;
  public static final int AGpsFirst = 0;
  public static final int GpsFirst = 1;
  public static final int NetWorkFirst = 2;

  protected boolean mIsOpenGps = false;
  protected int mScanSpan = MIN_SCAN_SPAN;
  protected int mTimeOut = 12000;
  protected int mPriority = AGpsFirst;
  protected boolean mIsLocationNotify = false;

  public LocationClientOption()
  {
  }

  public LocationClientOption(LocationClientOption option)
  {
    this.mIsOpenGps = option.mIsOpenGps;
    this.mScanSpan = option.mScanSpan;
    this.mTimeOut = option.mTimeOut;
	this.mPriority = option.mPriority;
    this.mIsLocationNotify = option.mIsLocationNotify;
  }

  public boolean isOpenGps()
  {
    return this.mIsOpenGps;
  }

  public void setOpenGps(boolean isOpenGps)
  {
    this.mIsOpenGps = isOpenGps;
  }

  public boolean isLocationNotify()
  {
    return this.mIsLocationNotify;
  }

  public void setLocationNotify(boolean isLocationNotify)
  {
    this.mIsLocationNotify = isLocationNotify;
  }

  public int getScanSpan()
  {
    return this.mScanSpan;
  }

  public void setScanSpan(int scanSpan)
  {
    this.mScanSpan = scanSpan;
  }

  public int getTimeOut()
  {
    return this.mTimeOut;
  }

  public void setTimeOut(int timeOut)
  {
    this.mTimeOut = timeOut;
  }

  public int getPriority()
  {
		return this.mPriority;
  }

	public void setPriority( int priority )
  {
		if ((priority == GpsFirst) || (priority == NetWorkFirst)) this.mPriority = priority;
  }
}