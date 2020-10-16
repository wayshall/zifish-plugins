package org.onetwo.plugins.admin;

import org.onetwo.boot.plugin.core.PluginMeta;
import org.onetwo.boot.plugin.core.SimplePluginMeta;
import org.onetwo.boot.plugin.core.WebPluginAdapter;

public class WebAdminPlugin extends WebPluginAdapter {
	public static final String NAME = "webAdmin";
	
	private final SimplePluginMeta meta = new SimplePluginMeta(NAME, "0.0.1");

	@Override
	public PluginMeta getPluginMeta() {
		return meta;
	}

}
