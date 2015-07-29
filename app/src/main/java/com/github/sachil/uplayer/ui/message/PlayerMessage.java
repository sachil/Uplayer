package com.github.sachil.uplayer.ui.message;

/**
 * Created by 20001962 on 2015/7/28.
 */
public class PlayerMessage {

    public static final int REFRESH_METADATA = 0x01;
    public static final int REFRESH_VOLUME = 0x02;
    public static final int REFRESH_CURRENT_POSITION = 0x03;

    private int mMessageId = 0;
    private Object mExtra = null;

    public PlayerMessage(int messageId,Object extra){

        mMessageId = messageId;
        mExtra = extra;
    }

    public int getId(){

        return mMessageId;
    }

    public Object getExtra(){

        return mExtra;
    }

}
