package com.mashmobile.mds.sample.simple;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.mashmobile.mds.library.MashmobileConnection;
import com.mashmobile.mds.library.MashmobileInfo;
import com.mashmobile.mds.library.MashmobileInfo.Bearer;
import com.mashmobile.mds.library.MashmobileInfo.State;
import com.mashmobile.mds.library.MashmobileNotifier;

public class HelloMDS1 extends Activity implements ServiceConnection {
    
	private static final int REGISTRATION_PROGRESS = 0;
	private static final int REGISTRATION_RESULT = 1;
	private String dialogMessage;
	private MashmobileConnection con;
	private SimpleNotifier notifier = new SimpleNotifier();
	private TextView user;
	private TextView state;
	private TextView bearer;
	private boolean showingRegistrationDialog = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		user = (TextView) findViewById(R.id.user);
		state = (TextView) findViewById(R.id.state);
		bearer = (TextView) findViewById(R.id.bearer);
		startService(new Intent(this, SimpleService.class));
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopService(new Intent(this, SimpleService.class));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_options, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (con != null && con.getInfo().getConnectionState() != State.OFFLINE) {
			MenuItem item = menu.findItem(R.id.menu_item_connect);
			item.setTitle(R.string.menu_item_disconnect);
			item.setIcon(R.drawable.disconnect);
		} else {
			MenuItem item = menu.findItem(R.id.menu_item_connect);
			item.setTitle(R.string.menu_item_connect);
			item.setIcon(R.drawable.connect);
		}
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_item_connect:
			if (item.getTitle()
					.equals(getString(R.string.menu_item_disconnect))) {
				con.disconnect();
			} else {
				con.connect();
			}
			break;
		case R.id.menu_item_exit:
			stopService(new Intent(this, SimpleService.class));
			finish();
			break;
		}
		return true;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case REGISTRATION_RESULT:
			return new AlertDialog.Builder(this).setTitle(
					getString(R.string.app_name)).setNeutralButton("OK", null)
					.setMessage(dialogMessage).create();
		case REGISTRATION_PROGRESS:
			ProgressDialog dlg = new ProgressDialog(this);
			dlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dlg.setTitle(R.string.snr_dialog_title);
			dlg.setMessage(getString(R.string.snr_dialog_message));
			dlg.setCancelable(false);
			return dlg;
		}
		return null;
	}

	@Override
	protected void onResume() {
		super.onResume();
		bindService(new Intent(this, SimpleService.class), this,
				BIND_AUTO_CREATE);
	}

	@Override
	protected void onPause() {
		super.onPause();
		unbindService(this);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		con = ((SimpleService.MashBinder) service).service.getConnection();
		con.registerNotifier(notifier);
		MashmobileInfo info = con.getInfo();
		update(info);
		user.setText("DeviceId: " + info.getDeviceID());
	}

	private void update(MashmobileInfo info) {
		switch (info.getConnectionState()) {
		case CONNECTED:
			setState("Connected");
			bearer.setText("Bearer: " + info.getCurrentbearer());
			break;
		case CONNECTING:
			setState("Connecting ...");
			break;
		case OFFLINE:
			setState("Offline");
		}
		
	}

	private void setState(String newState) {
		state.setText("State: " + newState);
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		con.unregisterNotifier(notifier);
		con = null;
	}

	class SimpleNotifier extends MashmobileNotifier {
		@Override
		public void onConnected(Bearer connectedBearer) {
			setState("Connected");
			bearer.setText("Bearer: " + connectedBearer);
		}

		@Override
		public void onConnecting() {
			setState("Connecting ...");
		}

		@Override
		public void onConnectionFailed(ConnectionError error) {
			setState("Connection failed - " + error);
		}

		@Override
		public void onOffline() {
			setState("Offline");
		}

		@Override
		public void onRegistrationCompleted(String deviceId) {
			if (showingRegistrationDialog) {
				dismissDialog(REGISTRATION_PROGRESS);
				showingRegistrationDialog = false;
			}
			user.setText("DeviceId: " + deviceId);
		}

		@Override
		public void onRegistrationFailed() {
			if (showingRegistrationDialog) {
				dismissDialog(REGISTRATION_PROGRESS);
				showingRegistrationDialog = false;
			}
			dialogMessage = getString(R.string.snr_dialog_failed_message);
			showDialog(REGISTRATION_RESULT);
		}

		@Override
		public void onStartingRegistration() {
			showDialog(REGISTRATION_PROGRESS);
			showingRegistrationDialog = true;
		}

	}
	
}
