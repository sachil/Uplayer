package com.github.sachil.uplayer.ui.message;

import java.util.List;

/**
 * Created by 20001962 on 2015/7/23.
 */
public class BrowseMessage {

    private List mItems = null;
    private boolean mIsRootNode = false;

    public BrowseMessage(List items,boolean isRootNode){
        mItems = items;
        mIsRootNode = isRootNode;
    }

    public List getItems(){
        return mItems;
    }

    public boolean isRootNode(){
        return mIsRootNode;
    }

}
