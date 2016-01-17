package com.example.administrator.mylistviewslide;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Scroller;

/**
 * Created by Administrator on 2016/1/17.
 */
public class MyFrameLayOut extends FrameLayout {

    private View delete;
    private int width;
    private int height;
    private int measuredWidth;
    private View contentView;
    private boolean isOpen=false;//默认关闭

    public MyFrameLayOut(Context context, AttributeSet attrs) {
        super(context, attrs);
        scroller=new Scroller(context);
    }

    @Override
    protected void onFinishInflate() {//加载完布局所有子view创建好之后才会调用
        super.onFinishInflate();
        delete = getChildAt(1);
        contentView = getChildAt(0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = delete.getMeasuredWidth();
        height = delete.getMeasuredHeight();
        measuredWidth =contentView.getMeasuredWidth();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        delete.layout(measuredWidth,0,measuredWidth+width,height);
    }
    private int lastx,downx,lasty,downy;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventx = (int) event.getRawX();
        int eventy = (int) event.getRawY();
        Log.e("TAG", "onTouchEvent()");
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN :
                lastx=downx=eventx;
                lasty=downy=eventy;

                break;
            case MotionEvent.ACTION_MOVE :
                int dx=eventx-lastx;
                int x=getScrollX()-dx;
                //x的范围是[0,width]
                if(x<0) {
                    x=0;
                }else if(x>width) {
                    x=width;
                }

                scrollTo(x,getScrollY());
                lastx=eventx;
                int totaly = Math.abs(eventy - downy);
                int totalx=Math.abs(eventx-downx);
                if(totalx>totaly) {//当水平的偏移比垂直方向的大舅进行反拦截
                    getParent().requestDisallowInterceptTouchEvent(true);//反拦截父视图的上下滑动响应
                }
                Log.e("TAG", "lastx="+lastx);
                break;
            case MotionEvent.ACTION_UP:
                //获取总的偏移量
                int scrollx=getScrollX();
                if(scrollx>width/2) {
                    open();
                }else {
                    close();
                }
                Log.e("TAG", "totalx="+scrollx);
                break;
        }
        return true;
    }
    private Scroller scroller;
    private void open() {
        Log.e("TAG", "open");
        //0-->width
        scroller.startScroll(getScrollX(), getScrollY(), width - getScrollX(), 0);
        invalidate();
        if(onStateChangeListener!=null) {
            onStateChangeListener.onOpen(this);
        }
        isOpen=true;
    }

    public void close() {
        Log.e("TAG", "close");
        scroller.startScroll(getScrollX(),getScrollY(),-getScrollX(),0);
        invalidate();
        if(onStateChangeListener!=null) {
            onStateChangeListener.onClose(this);
        }
        isOpen=false;
    }

    @Override
    public void computeScroll() {
        Log.e("TAG", "computeScroll");
        if(scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(),scroller.getCurrY());
            invalidate();
        }
    }

    //当触摸的时候首先是content的优先级最高，所以要想使父视图进行消费就要拦截：使listview中的item可以消费
    // 从而可以进行移动
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
         super.onInterceptTouchEvent(ev);
        int rawx = (int) ev.getRawX();
        int rawy = (int) ev.getRawY();
        boolean falg=false;//默认不拦截
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastx=downx=rawx;
                lasty=downy=rawy;
                if(onStateChangeListener!=null) {
                    onStateChangeListener.onDown(this);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = Math.abs(rawx - downx);
                if(dx>5) {
                    falg=true;//拦截
                }
                lastx=rawx;
                break;
        }
        return falg;
    }

    //自定义监听：
    private OnStateChangeListener onStateChangeListener;
    public void setOnStateChangeListener(OnStateChangeListener onStateChangeListener){
        this.onStateChangeListener=onStateChangeListener;
    }
    interface OnStateChangeListener{
        void onOpen(MyFrameLayOut myframgelayOut);
        void onClose(MyFrameLayOut myframgelayOut);
        void onDown(MyFrameLayOut myframgelayOut);//当点击的时候
    }

    public boolean isOpen(){
        return isOpen;
    }
}
