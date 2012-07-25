package com.mashmobile.mds.sample.simple;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.mashmobile.mds.library.MashmobileApplicationInfo;
import com.mashmobile.mds.library.MashmobileConnection;
import com.mashmobile.mds.library.MashmobileService;

public class SimpleService extends MashmobileService {

	private SimplePlugin simplePlugin;
	
	@Override
	public void setupConnection(MashmobileConnection con) {		
		simplePlugin = new SimplePlugin();
		con.registerPlugin(simplePlugin);
		con.connect();
	}

	@Override
	public void closeConnection(MashmobileConnection con) {
		con.unregisterPlugin(simplePlugin);
	}

	@Override
	public MashmobileApplicationInfo getMashmobileApplicationInfo() {

		return new MashmobileApplicationInfo() {
			@Override
			public String getRegistrationDomain() {
				return "api.mashmobile.com";
			}

			@Override
			public int getRegistrationPort() {
				return 443;
			}

			@Override
			public String getApplicationId() {
				return "5b845706";
			}

			@Override
			public int getApplicationVersion() {
				try {
					String packageName = "com.mashmobile.mds.sample.simple";
					PackageInfo pi = getPackageManager().getPackageInfo(
							packageName, PackageManager.GET_META_DATA);
					return pi.versionCode;
				} catch (NameNotFoundException e) {
					e.printStackTrace();
					return -1;					
				}
			}
		};
	}
}//class end here
