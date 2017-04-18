package cc.wulian.smarthomev5.tools;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

public class MediaPlayerTool {
	private static MediaPlayer mediaPlayer;

	public synchronized static void play(Context context, Uri sound) {
		try {
			if (mediaPlayer != null) {
				mediaPlayer.stop();
				mediaPlayer.release();
				mediaPlayer = null;
			}
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setDataSource(context, sound);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setLooping(false);
			mediaPlayer.prepare();
			mediaPlayer.start();
			Thread.sleep(500);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized static void stop() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}
}
