package com.github.sachil.uplayer.ui;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.model.meta.Device;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.github.sachil.uplayer.R;
import com.github.sachil.uplayer.Utils;
import com.github.sachil.uplayer.player.MusicService;
import com.github.sachil.uplayer.ui.message.ActionMessage;
import com.github.sachil.uplayer.upnp.UpnpUnity;
import com.github.sachil.uplayer.upnp.dmc.DeviceRegistryListener;
import com.github.sachil.uplayer.upnp.dmr.MediaRenderer;
import com.github.sachil.uplayer.upnp.dms.ContentGenerator;
import com.github.sachil.uplayer.upnp.dms.MediaServer;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity
		implements ServiceConnection {

	private static final String TAG = MainActivity.class.getSimpleName();
	private Context mContext = null;
	private DrawerLayout mDrawerLayout = null;
	private Toolbar mToolbar = null;
	private NavigationView mNavigationView = null;
	private NavManager mNavmanager = null;
	private PlaybarManager mPlaybarManager = null;
	private ContentManager mContentManager = null;
	private AndroidUpnpService mUpnpService = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fresco.initialize(this);
		setContentView(R.layout.activity_main);
		mContext = this;
		mContext = this;
		initView();
		bindService(new Intent(this, AndroidUpnpServiceImpl.class), this,
				Context.BIND_AUTO_CREATE);
		startService(new Intent(mContext, MusicService.class));
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!EventBus.getDefault().isRegistered(this))
			EventBus.getDefault().register(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		mNavmanager.clean();
		mPlaybarManager.clean();
		mContentManager.clean();
		if (EventBus.getDefault().isRegistered(this))
			EventBus.getDefault().unregister(this);
		unbindService(this);
		super.onDestroy();
	}

	@Override
	public void onServiceConnected(ComponentName componentName,
			IBinder iBinder) {
		Log.e(TAG, "Service is connected.");
		mUpnpService = (AndroidUpnpService) iBinder;
		UpnpUnity.UPNP_SERVICE = mUpnpService;
		mNavmanager = new NavManager(mContext,
				findViewById(android.R.id.content));
		mPlaybarManager = new PlaybarManager(mContext,
				findViewById(android.R.id.content));
		mContentManager = new ContentManager(mContext,
				findViewById(android.R.id.content));
		MediaServer server = new MediaServer(mContext,
				Utils.getInetAddress(mContext));
		mUpnpService.getRegistry().addDevice(server.getDevice());
		MediaRenderer renderer = new MediaRenderer(mContext);
		mUpnpService.getRegistry().addDevice(renderer.getDevice());
		ContentGenerator.prepareAudio(mContext, server);

		DeviceRegistryListener listener = new DeviceRegistryListener();
		for (Device device : mUpnpService.getRegistry().getDevices())
			listener.refreshDevice(device, true);
		mUpnpService.getRegistry().addListener(listener);
		mUpnpService.getControlPoint().search();
	}

	@Override
	public void onServiceDisconnected(ComponentName componentName) {
		Log.e(TAG, "Service is disconnected.");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case android.R.id.home:

			mDrawerLayout.openDrawer(GravityCompat.START);

			break;

		case R.id.menu_layout:

			if (mContentManager != null) {
				if (mContentManager
						.getLayout() == ContentManager.LAYOUT_TYPE.LIST) {
					item.setIcon(
							getResources().getDrawable(R.drawable.menu_grid));
					EventBus.getDefault()
							.post(new ActionMessage(R.id.menu_layout, 0,
									ContentManager.LAYOUT_TYPE.GRID));
				}
//				else if (mContentManager
//						.getLayout() == ContentManager.LAYOUT_TYPE.GRID) {
//					item.setIcon(getResources()
//							.getDrawable(R.drawable.menu_staggered_grid));
//					EventBus.getDefault()
//							.post(new ActionMessage(R.id.menu_layout, 0,
//									ContentManager.LAYOUT_TYPE.STAGGERED_GRID));
//				}
				else {
					item.setIcon(
							getResources().getDrawable(R.drawable.menu_list));
					EventBus.getDefault()
							.post(new ActionMessage(R.id.menu_layout, 0,
									ContentManager.LAYOUT_TYPE.LIST));
				}
			}
			break;

		case R.id.menu_settings:

			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		if (mContentManager.isRootNode())
			super.onBackPressed();
		else
			mContentManager.viewParent();
	}

	public void onEvent(ActionMessage message) {

		switch (message.getViewId()) {
		case R.id.nav_settings:
			break;
		case R.id.nav_exit:
			stopService(new Intent(mContext, MusicService.class));
			finish();
			break;
		}
	}

	private void initView() {

		mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer);
		((CollapsingToolbarLayout) findViewById(R.id.main_toolbar_layout))
				.setTitle(getString(R.string.app_name));
		mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
				mDrawerLayout, mToolbar, R.string.app_name, R.string.app_name);
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		toggle.syncState();
		mDrawerLayout.setDrawerListener(toggle);
		mNavigationView = (NavigationView) findViewById(R.id.main_navigation);
	}
}
