package org.onetwo.plugins.admin.vo;

import org.onetwo.common.utils.CUtils;
import org.onetwo.plugins.admin.vo.VueRouterTreeModel.RouteData;

/**
 * @author weishao zeng
 * <br/>
 */

abstract public class VueRouteDatas {
	private static final RouteData Layout = new RouteData("Layout", null);
	
	/***
	 * 跳转路由到另一个view页面时，如果想保持侧栏和顶栏不改变，需要把父路由的component设置为Layout
	 * @author weishao zeng
	 * @return
	 */
	final public static RouteData layout() {
		return Layout;
	}
	
	/***
	 * 创建多级菜单时，为了避免重复嵌套侧栏和顶栏，父菜单不能使用Layout，需要重写一个模板页：
	 * <template>
  		<router-view/>
	   </template>
	 * @author weishao zeng
	 * @return
	 */
	final public static RouteData routerView() {
		RouteData menuNote = new RouteData("", null);
		menuNote.setTemplate("<template>" + 
							 "  <router-view/>" + 
							 "</template>");
		return menuNote;
	}
	
	final public static RouteData componentView(String componentViewPath) {
		return new RouteData(componentViewPath, null);
	}
	
	final public static RouteData componentView(String componentViewPath, Object...props) {
		return new RouteData(componentViewPath, CUtils.asMap(props));
	}

}
