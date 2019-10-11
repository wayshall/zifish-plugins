package org.onetwo.boot.plugins.swaggerui;

import org.onetwo.boot.plugin.core.PluginMeta;
import org.onetwo.boot.plugin.core.WebPluginAdapter;

/**
 * @author wayshall
 * <br/>
 */
public class SwaggerUIPlugin extends WebPluginAdapter {
    private final PluginMeta meta = PluginMeta.useKebabCaseBy(this.getClass());

    @Override
    public PluginMeta getPluginMeta() {
        return meta;
    }

}
