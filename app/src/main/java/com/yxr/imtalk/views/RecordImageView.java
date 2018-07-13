package com.yxr.imtalk.views;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.media.MediaRecorder;
import android.os.SystemClock;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.yxr.imtalk.R;
import com.yxr.imtalk.util.Constants;
import com.yxr.imtalk.util.UUIDUtils;

import java.io.File;

public class RecordImageView extends AppCompatImageView {

    private Dialog dialog;

    private MediaRecorder recorder;

    private long startTime;

    private File audioFile;

    //private long endTime;

    private OnRecordFinishedListener onRecordFinishedListener;

    public RecordImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            // 按下弹出对话框
            case MotionEvent.ACTION_DOWN:
                // 弹出对话框
                startRecord();
                break;

            // 松开停止录音
            case MotionEvent.ACTION_UP:
                stopRecord();
                break;

            // 取消,手指移开了按钮
            case MotionEvent.ACTION_CANCEL:
                stopRecord();
                break;

                default:
                    break;
        }

        return true;
    }

    /**
     *  停止录音
     */
    private void stopRecord() {
        // 关闭对话框
        if(dialog != null) {
            dialog.dismiss();
        }

        // 关闭录音
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;

            // 判断录音的时长是否太短
            if (System.currentTimeMillis() - startTime < 1000) {
                Toast.makeText(getContext(), "录制时间太短", Toast.LENGTH_SHORT).show();
                // 删除录音文件
                if (audioFile.exists()) {
                    audioFile.delete();
                }
                return;
            }
        }

        // 录音完成
        if (onRecordFinishedListener != null) {
            Toast.makeText(getContext(), "录制成功", Toast.LENGTH_SHORT).show();
            onRecordFinishedListener.onFinished(audioFile, (int)(System.currentTimeMillis() - startTime));
        }
    }

    /**
     *  开始录音
     */
    private void startRecord() {
        startTime = System.currentTimeMillis();

        // 弹出对话框
        dialog = new Dialog(getContext(), R.style.like_toast_dialog_style);
        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(R.drawable.ic_attach_record);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        // 将图片添加到对话框显示
        dialog.addContentView(imageView, params);

        // 使用MediaRecorder 开始录音
        recorder = new MediaRecorder();

        // 音源
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);

        // 文件格式
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);

        // 编码格式
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        // 输出文件路径
        if (!Constants.AUDIO_DIR.exists()) {
            Constants.AUDIO_DIR.mkdirs();
        }

        audioFile = new File(Constants.AUDIO_DIR, UUIDUtils.generate() + ".amr");

        recorder.setOutputFile(audioFile.getAbsolutePath());

        Log.i("保存位置在", audioFile.getAbsolutePath());

        try {
            recorder.prepare();
            recorder.start();

            dialog.show();
        } catch (Exception e) {

        }

    }

    /**
     *  监听语音录制
     */
    public interface OnRecordFinishedListener {
        /**
         * 发送语音
         *
         * @param audioFile 录制的音频文件
         * @param duration 录制时长
         */
        void onFinished(File audioFile, int duration);
    }

    public void setOnRecordFinishedListener(OnRecordFinishedListener onRecordFinishedListener) {
        this.onRecordFinishedListener = onRecordFinishedListener;
    }
}
