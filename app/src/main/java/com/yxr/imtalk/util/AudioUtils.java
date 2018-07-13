package com.yxr.imtalk.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class AudioUtils {


    /**
     *  播放一个音频文件
     * @param path 路径
     * @param context
     */
    public static void play(String path, Context context) {
        final SoundPool sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

        // 音频管理器
        final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        // 加载音频, 优先权
        final int soundId = sp.load(path, 1);

        // 加载完成的监听
        sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                // 播放
                // 音量
                int volumn = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                sp.play(soundId, volumn, volumn, 1, 0, 1);
            }
        });
    }
}
