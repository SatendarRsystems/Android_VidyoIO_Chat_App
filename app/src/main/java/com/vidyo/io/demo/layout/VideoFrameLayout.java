package com.vidyo.io.demo.layout;
/**
 * Summary: This frame show the video chat
 * Description: It handles the Multiple or single video frames and Video frame Listeners
 * @author RSI
 * @date 15.09.2018
 */
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class VideoFrameLayout extends FrameLayout {

    private IVideoFrameListener mListener;
    private float mDownX;
    private float mDownY;
    private final float SCROLL_THRESHOLD = 10;
    private boolean isOnClick;

    public VideoFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Register the Video Frame
    */
    public void Register(IVideoFrameListener listener) {
        mListener = listener;
    }

    /**
     * when Tap on video frame , need to notify the VideoChatActivity and
     * differentiate between a tap versus other touch events.
     */
    @Override
    public boolean dispatchTouchEvent (MotionEvent ev) {
        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getX();
                mDownY = ev.getY();
                isOnClick = true;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (isOnClick) {
                    // call back to the MainActivity
                    mListener.onVideoFrameClicked();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isOnClick && (Math.abs(mDownX - ev.getX()) > SCROLL_THRESHOLD || Math.abs(mDownY - ev.getY()) > SCROLL_THRESHOLD)) {
                    isOnClick = false;
                }
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
