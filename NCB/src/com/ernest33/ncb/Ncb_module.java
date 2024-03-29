package com.ernest33.ncb;

import android.content.res.XModuleResources;
import android.content.res.XResources;
import android.graphics.drawable.Drawable;
import android.os.Environment;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.XposedBridge;

public class Ncb_module implements IXposedHookZygoteInit, IXposedHookInitPackageResources
{
//private static String MODULE_PATH = null;

	@Override
	public void handleInitPackageResources(InitPackageResourcesParam resparam) throws Throwable
	{
		if (!resparam.packageName.equals("com.android.systemui"))
			return;
		XposedBridge.log("Alors?");
		try
		{
			XposedBridge.log(">>Loading image at /mnt/sdcard/notifbg.png");
			resparam.res.setReplacement("com.android.systemui", "drawable", "notification_panel_bg",
				new XResources.DrawableLoader()
				{
					@Override
					public Drawable newDrawable(XResources res, int id) throws Throwable
					{
						return Drawable.createFromPath(Environment.getExternalStorageDirectory() + "/notifbg.png");
					}
				});
		} catch (Throwable t)
		{
			XposedBridge.log(t);
		}
	}

	@Override
	public void initZygote(StartupParam startupParam) throws Throwable
	{
		XposedBridge.log("Don't make your Zygote!");
		//MODULE_PATH = startupParam.modulePath;
		XModuleResources modRes = XModuleResources.createInstance(startupParam.modulePath, null);
		XResources.setSystemWideReplacement(
			"android",
			"drawable",
			"background_holo_dark",
			modRes.fwd(R.drawable.background_holo_dark));
	}
}