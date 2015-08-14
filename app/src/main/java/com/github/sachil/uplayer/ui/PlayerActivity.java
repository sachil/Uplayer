package com.github.sachil.uplayer.ui;

import java.util.Timer;
import java.util.TimerTask;

import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.support.model.PlayMode;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.SeekMode;
import org.fourthline.cling.support.model.TransportState;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.github.sachil.uplayer.R;
import com.github.sachil.uplayer.Utils;
import com.github.sachil.uplayer.player.MusicService;
import com.github.sachil.uplayer.ui.message.ErrorMessage;
import com.github.sachil.uplayer.ui.message.PlayerMessage;
import com.github.sachil.uplayer.upnp.UpnpUnity;
import com.github.sachil.uplayer.upnp.dmc.Controller;
import com.github.sachil.uplayer.upnp.dmc.Metadata;
import com.github.sachil.uplayer.upnp.dmc.XMLFactory;

import de.greenrobot.event.EventBus;

public class PlayerActivity extends AppCompatActivity
		implements View.OnClickListener {
	private static final String TAG = PlayerActivity.class.getSimpleName();

	private Context mContext = null;
	private View mBackground = null;
	private Toolbar mToolbar = null;
	private TextView mTitle = null;
	private TextView mArtist = null;
	private TextView mAlbum = null;
	private SimpleDraweeView mThumb = null;
	private TextView mCurrentTime = null;
	private TextView mTotalTime = null;
	private SeekBar mSeekBar = null;
	private ImageView mHomeButton = null;
	private ImageView mModeButton = null;
	private ImageView mPrevButton = null;
	private ImageView mPlayPauseButton = null;
	private ImageView mNextButton = null;
	private ImageView mListButton = null;

	private Timer mPositionTimer = null;
	private TimerTask mPositionTask = null;
	private Controller mController = null;
	private TransportState mState = TransportState.NO_MEDIA_PRESENT;
	private PlaylistManager mManager = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player);
		mContext = this;
		initView();
		mController = MusicService.getInstance().getController();
		mController.registerLastChange();
		mManager = PlaylistManager.newInstance(mContext);
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
		if (EventBus.getDefault().isRegistered(this))
			EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_player, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.player_home:
			finish();
			break;
		case R.id.player_model:

			break;
		case R.id.player_prev:

			break;
		case R.id.player_play_pause:
			if (mState == TransportState.PLAYING)
				mController.pause();
			else if (mState == TransportState.PAUSED_PLAYBACK)
				mController.play();
			break;
		case R.id.player_next:

			break;
		case R.id.player_playlist:
			mManager.showPlaylistView(findViewById(R.id.player_control_layout));
			break;
		}
	}

	public void onEventMainThread(ErrorMessage message) {

		if (message.getId() == ErrorMessage.CONTROL_ERROR)
			Toast.makeText(this, message.getMessage(), Toast.LENGTH_SHORT)
					.show();
	}

	public void onEventMainThread(PlayerMessage message) {

		switch (message.getId()) {
		case PlayerMessage.REFRESH_METADATA:
			Metadata metadata = (Metadata) message.getExtra();
			syncState(PlayerMessage.REFRESH_METADATA, metadata);
			break;

		case PlayerMessage.REFRESH_VOLUME:
			metadata = (Metadata) message.getExtra();
			syncState(PlayerMessage.REFRESH_VOLUME, metadata);
			break;

		case PlayerMessage.REFRESH_CURRENT_POSITION:

			PositionInfo info = (PositionInfo) message.getExtra();
			syncState(PlayerMessage.REFRESH_CURRENT_POSITION, info);
			break;
		}

	}

	private void syncState(int id, Object data) {

		if (id == PlayerMessage.REFRESH_METADATA) {
			Metadata metadata = (Metadata) data;
			TransportState state = metadata.getState();
			mState = state;
			if (state == TransportState.PLAYING) {
				mPlayPauseButton.setImageResource(R.drawable.playback_pause);
				mTitle.setText(metadata.getTitle());
				mArtist.setText(metadata.getCreator());
				mAlbum.setText(metadata.getAlbum());
				mTotalTime.setText(metadata.getDuration());

				/**
				 * 当前播放的时间位置不会通过Lastchange事件触发得到，所以需要
				 * 单独启动一个线程以一定的频率(通常是1s)去不断的获取当前的播放位置，
				 * 该线程只会在transportState为PLAYING状态时才启动。
				 */
				if (mPositionTimer == null) {
					mPositionTimer = new Timer();
					mPositionTask = new TimerTask() {
						@Override
						public void run() {
							mController.getPosition();
						}
					};
					mPositionTimer.schedule(mPositionTask, 0, 1000);
				}
				PlayMode mode = metadata.getPlayMode();
				if (mode == PlayMode.NORMAL)
					mModeButton
							.setImageResource(R.drawable.playback_repeat_white);
				else if (mode == PlayMode.REPEAT_ONE)
					mModeButton.setImageResource(
							R.drawable.playback_repeat_1_white);
				else if (mode == PlayMode.SHUFFLE)
					mModeButton.setImageResource(
							R.drawable.playback_schuffle_white);

				if (metadata.getAlbumArt() != null)
					getAlbumArt(metadata.getAlbumArt());
			} else {
				mPlayPauseButton.setImageResource(R.drawable.playback_play);

				// transportState不是PLAYING状态时,停止获取当前播放的位置信息。

				if (state == TransportState.STOPPED) {
					if (mPositionTimer != null)
						mPositionTimer.cancel();
					mCurrentTime.setText("00:00:00");
					mSeekBar.setProgress(0);
					mPositionTimer = null;
					mPositionTask = null;
				}
			}
		} else if (id == PlayerMessage.REFRESH_CURRENT_POSITION) {
			PositionInfo info = (PositionInfo) data;
			String relTime = info.getRelTime();
			String duration = info.getTrackDuration();
			mCurrentTime.setText(relTime);
			long currentTime = ModelUtil.fromTimeString(relTime);
			long totalTime = ModelUtil.fromTimeString(duration);
			if (totalTime != 0) {
				int position = (int) (((float) currentTime / totalTime) * 100);
				mSeekBar.setProgress(position);
			}
		} else if (id == PlayerMessage.REFRESH_VOLUME) {

		}
	}

	private void initView() {
		mBackground = findViewById(R.id.player_background);
		mToolbar = (Toolbar) findViewById(R.id.player_toolbar);
		setSupportActionBar(mToolbar);
		mTitle = (TextView) findViewById(R.id.player_title);
		mArtist = (TextView) findViewById(R.id.player_artist);
		mAlbum = (TextView) findViewById(R.id.player_album);
		mThumb = (SimpleDraweeView) findViewById(R.id.player_thumb);
		mCurrentTime = (TextView) findViewById(R.id.player_current_time);
		mTotalTime = (TextView) findViewById(R.id.player_total_time);
		mSeekBar = (SeekBar) findViewById(R.id.player_seekbar);

		mSeekBar.setOnSeekBarChangeListener(
				new SeekBar.OnSeekBarChangeListener() {
					@Override
					public void onProgressChanged(SeekBar seekBar, int progress,
							boolean fromUser) {
						if (fromUser) {
							long totalTime = ModelUtil.fromTimeString(
									mController.getMetadata().getDuration());
							mController.seek(SeekMode.REL_TIME,
									ModelUtil.toTimeString(
											(long) ((progress / 100f)
													* totalTime)));
						}
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
					}

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
					}
				});

		mHomeButton = (ImageView) findViewById(R.id.player_home);
		mHomeButton.setOnClickListener(this);
		mModeButton = (ImageView) findViewById(R.id.player_model);
		mModeButton.setOnClickListener(this);
		mPrevButton = (ImageView) findViewById(R.id.player_prev);
		mPrevButton.setOnClickListener(this);
		mPlayPauseButton = (ImageView) findViewById(R.id.player_play_pause);
		mPlayPauseButton.setOnClickListener(this);
		mNextButton = (ImageView) findViewById(R.id.player_next);
		mNextButton.setOnClickListener(this);
		mListButton = (ImageView) findViewById(R.id.player_playlist);
		mListButton.setOnClickListener(this);
	}

	private void getAlbumArt(String uri) {
		ImageRequest imageRequest = ImageRequestBuilder
				.newBuilderWithSource(Uri.parse(uri))
				.setProgressiveRenderingEnabled(true).build();
		ImagePipeline imagePipeline = Fresco.getImagePipeline();
		DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline
				.fetchDecodedImage(imageRequest, mContext);
		dataSource.subscribe(new BaseBitmapDataSubscriber() {
			@Override
			protected void onNewResultImpl(Bitmap bitmap) {
				int defaultColor = mContext.getResources()
						.getColor(R.color.half_transparent);
				int i = 0;
				int backgroundColor;
				do {
					backgroundColor = generateBackgroundColor(bitmap);
					i++;
				} while (i < 3 && backgroundColor == defaultColor);

				final AlbumArt albumArt = new AlbumArt(backgroundColor, bitmap);

				Utils.UI_THREAD.post(new Runnable() {
					@Override
					public void run() {
						mBackground.setBackgroundColor(albumArt.mColor);
						mThumb.setImageBitmap(albumArt.mBitmap);
					}
				});

			}

			@Override
			protected void onFailureImpl(
					DataSource<CloseableReference<CloseableImage>> dataSource) {
			}
		}, CallerThreadExecutor.getInstance());

	}

	private int generateBackgroundColor(Bitmap bitmap) {
		Palette palette = Palette.from(bitmap).generate();
		return palette.getDarkMutedColor(
				getResources().getColor(R.color.half_transparent));
	}

	private void playItem() {

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
					mController.setUrl(
							UpnpUnity.PLAYING_ITEM.getFirstResource()
									.getValue(),
							XMLFactory.metadataToXml(
									UpnpUnity.PLAYING_ITEM));
					Thread.sleep(1000);
					mController.play();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}

	private class AlbumArt {
		private int mColor = 0;
		private Bitmap mBitmap = null;

		public AlbumArt(int color, Bitmap bitmap) {
			mColor = color;
			mBitmap = bitmap;
		}
	}
}
