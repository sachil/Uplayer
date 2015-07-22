package com.github.sachil.uplayer.content;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

/**
 * Created by 20001962 on 2015/7/22.
 */
public class PatchedExpandableListView extends ExpandableListView {

    public PatchedExpandableListView(Context context){
        super(context);
    }

    public PatchedExpandableListView(Context context, AttributeSet attrs) {
        super(context,attrs);
    }

    public PatchedExpandableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context,attrs,defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);

        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
