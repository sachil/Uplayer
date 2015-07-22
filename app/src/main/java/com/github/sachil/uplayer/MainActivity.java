package com.github.sachil.uplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.github.sachil.uplayer.adapter.NavAdapter;
import com.github.sachil.uplayer.content.PatchedExpandableListView;
import com.github.sachil.uplayer.upnp.UpnpUnity;
import com.github.sachil.uplayer.upnp.dmc.DeviceRegistryListener;
import com.github.sachil.uplayer.upnp.dmr.MediaRenderer;
import com.github.sachil.uplayer.upnp.dms.ContentGenerator;
import com.github.sachil.uplayer.upnp.dms.MediaServer;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.model.meta.Device;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Context mContext = null;
    private AndroidUpnpService mUpnpService = null;
    private NavAdapter mRendererAdapter = null;
    private NavAdapter mServerAdapter = null;
    private NavAdapter mMediaAdapter = null;
    private List<Device> mRenderers = new ArrayList<>();
    private List<Device> mServers = new ArrayList<>();


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == UpnpUnity.REFRESH_DMR_LIST) {
                mRendererAdapter.refresh(mRenderers);
            } else if (msg.what == UpnpUnity.REFRESH_DMS_LIST) {
                mServerAdapter.refresh(mServers);
            }

        }
    };


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            Log.e(TAG, "Service is connected.");
            mUpnpService = (AndroidUpnpService) iBinder;
            MediaServer server = new MediaServer(mContext, UplayerUnity.getInetAddress(mContext));
            mUpnpService.getRegistry().addDevice(server.getDevice());
            MediaRenderer renderer = new MediaRenderer(mContext);
            mUpnpService.getRegistry().addDevice(renderer.getDevice());
            ContentGenerator.prepareAudio(mContext, server);

            DeviceRegistryListener listener = new DeviceRegistryListener(mHandler, mRenderers, mServers);
            for (Device device : mUpnpService.getRegistry().getDevices())
                listener.refreshDevice(device, true);
            mUpnpService.getRegistry().addListener(listener);
            mUpnpService.getControlPoint().search();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

            Log.e(TAG, "Service is disconnected.");

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);
        mContext = this;
        mContext = this;
        UplayerUnity.setContext(mContext);
        initView();
        bindService(new Intent(this, AndroidUpnpServiceImpl.class),
                mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initNav() {

        PatchedExpandableListView renderer = (PatchedExpandableListView) findViewById(R.id.nav_renderer);

        PatchedExpandableListView server = (PatchedExpandableListView) findViewById(R.id.nav_library);
        PatchedExpandableListView media = (PatchedExpandableListView) findViewById(R.id.nav_media);

        mRendererAdapter = new NavAdapter(this);
        mServerAdapter = new NavAdapter(this);
        mMediaAdapter = new NavAdapter(this);

        renderer.setAdapter(mRendererAdapter);
        server.setAdapter(mServerAdapter);
        media.setAdapter(mMediaAdapter);
    }

    private void initView() {

        NavigationView navigationView = (NavigationView) findViewById(R.id.main_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initNav();
    }
}
