package com.example.admin.newnews.dataUtils;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Admin on 4/12/2016.
 */
public class BaselineTextView extends TextView {

    public BaselineTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int yOffset = getHeight() - getBaseline();
        canvas.translate(0, yOffset);
        super.onDraw(canvas);
    }

}
