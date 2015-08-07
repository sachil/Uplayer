package com.github.sachil.uplayer.player;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.github.sachil.uplayer.upnp.dmc.Controller;

public class MusicService extends Service {

    private static final String TAG = MusicService.class.getSimpleName();
    private static MusicService SERVICE = null;
    private MusicPlayer mPlayer = null;
    private Controller mController = null;

    public static MusicService getInstance(){

        return SERVICE;
    }

    public MusicPlayer getPlayer(){

        return mPlayer;
    }

    public Controller getController(){

        return mController;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SERVICE = this;
        mPlayer = MusicPlayer.getInstance(this);
        mController = Controller.getInstance();
        mController.registerLastChange();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        mPlayer.close();
        super.onDestroy();
    }
}
