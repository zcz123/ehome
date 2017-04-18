package cc.wulian.smarthomev5.entity;

public class AudioEntity
{
	public String mAudioName;
	public String mAudioPath;

	public AudioEntity( String audioName, String audioPath )
	{
		mAudioName = audioName;
		mAudioPath = audioPath;
	}

	public AudioEntity()
	{
		mAudioName = null;
		mAudioPath = null;
	}

	public String getmAudioName() {
		return mAudioName;
	}

	public void setmAudioName(String mAudioName) {
		this.mAudioName = mAudioName;
	}

	public String getmAudioPath() {
		return mAudioPath;
	}

	public void setmAudioPath(String mAudioPath) {
		this.mAudioPath = mAudioPath;
	}
	
}
