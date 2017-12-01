package lhy.lhylibrary.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Lilaoda on 2017/12/1.
 * Email:749948218@qq.com
 */

public class ProgressView extends View {

    private Paint mPaint;
    private int mWidth;
    private int mHeight;
    private int mProgress;

    public ProgressView(Context context) {
        this(context,null);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initConfig();
    }

    private void initConfig() {
        mPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0,mHeight/2-10,mWidth,mHeight/2+10,mPaint);
    }

    public void setProgress(int progress){
        this.mProgress = progress;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getDefaultSize(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getMode(widthMeasureSpec));
        mHeight = getDefaultSize(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.getMode(heightMeasureSpec));
    }
}
