package org.onetwo.plugins.admin.vo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.onetwo.common.tree.AbstractTreeModel;
import org.onetwo.common.utils.GuavaUtils;
import org.onetwo.common.utils.LangUtils;
import org.onetwo.common.utils.StringUtils;
import org.onetwo.common.web.utils.RequestUtils;
import org.onetwo.ext.permission.utils.PermissionUtils;
import org.onetwo.plugins.admin.entity.AdminPermission;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Maps;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 见：https://panjiachen.gitee.io/vue-element-admin-site/zh/guide/essentials/router-and-nav.html#%E9%85%8D%E7%BD%AE%E9%A1%B9
 * 
 * // 当设置 true 的时候该路由不会在侧边栏出现 如401，login等页面，或者如一些编辑页面/edit/1
hidden: true // (默认 false)

//当设置 noRedirect 的时候该路由在面包屑导航中不可被点击
redirect: 'noRedirect'

// 当你一个路由下面的 children 声明的路由大于1个时，自动会变成嵌套的模式--如组件页面
// 只有一个时，会将那个子路由当做根路由显示在侧边栏--如引导页面
// 若你想不管路由下面的 children 声明的个数都显示你的根路由
// 你可以设置 alwaysShow: true，这样它就会忽略之前定义的规则，一直显示根路由
alwaysShow: true

name: 'router-name' // 设定路由的名字，一定要填写不然使用<keep-alive>时会出现各种问题
meta: {
  roles: ['admin', 'editor'] // 设置该路由进入的权限，支持多个权限叠加
  title: 'title' // 设置该路由在侧边栏和面包屑中展示的名字
  icon: 'svg-name' // 设置该路由的图标，支持 svg-class，也支持 el-icon-x element-ui 的 icon
  noCache: true // 如果设置为true，则不会被 <keep-alive> 缓存(默认 false)
  breadcrumb: false //  如果设置为false，则不会在breadcrumb面包屑中显示(默认 true)
  affix: true // 若果设置为true，它则会固定在tags-view中(默认 false)

  // 当路由设置了该属性，则会高亮相对应的侧边栏。
  // 这在某些场景非常有用，比如：一个文章的列表页路由为：/article/list
  // 点击文章进入文章详情页，这时候路由为/article/1，但你想在侧边栏高亮文章列表的路由，就可以进行如下设置
  activeMenu: '/article/list'
}
 * @author wayshall
 * <br/>
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties({"id", "parent", "parentId", "sort", "level", "index", "leafage", "first", "last"})
public class VueRouterTreeModel extends AbstractTreeModel<VueRouterTreeModel> {
	public static final String LAYOUT_NODE = "Layout";
	
	@Getter
	@Setter
	@JsonIgnore
	private String url;
	//menu is false, permission is true
	private boolean hidden;
	private Map<String, Object> meta;
	private RouteData router;
	@JsonIgnore
	private AdminPermission permission;

	public VueRouterTreeModel(AdminPermission permission) {
		this(permission.getCode(), permission.getName(), permission.getParentCode());
		this.permission = permission;
	}

	public VueRouterTreeModel(String id, String title, String parentId) {
		super(id, id, parentId);
		getMeta().put("title", title);
	}
	
	@JsonIgnore
	public boolean isMenuNode() {
		return PermissionUtils.isMenu(permission);
	}
	

	@Override
	public void addChild(VueRouterTreeModel node) {
		// 构建树形菜单时，非菜单节点，不添加到子菜单节点
		if (!PermissionUtils.isMenu(node.permission)) {
			return ;
		}
		super.addChild(node);
	}
	
	public void setMeta(Map<String, Object> meta) {
		this.meta = meta;
	}
	
	public Map<String, Object> getMeta() {
		if (meta==null) {
			meta = Maps.newHashMap();
		}
		return meta;
	}
	
	/***
	 * 对应着路由的component属性
	 * @author weishao zeng
	 * @return
	 */
	public String getPath() {
		// 如果是外部链接，直接返回链接即可
		if (RequestUtils.isHttpPath(url)) {
			return url;
		}
		String path = (String)getId();
		/*
		String parentId = (String)getParentId();
		if(StringUtils.isNotBlank(parentId)) {
			path = path.substring(parentId.length()+1);
		}
		return "/"+path;
		*/
		path = "/" + StringUtils.replaceEach(path, "_", "/");
		if (router!=null && router.isParamsAsProps()) {
			String[] paramNames = (String[])router.getProps();
			if (LangUtils.isEmpty(paramNames)) {
				return path;
			}
			for (String paramName : paramNames) {
				path += "/:" + paramName;
			}
		}
		return path; 
	}

	/****
	 * vue组件命名推荐使用PascalCase，即单词大写开头的风格
	 * https://cn.vuejs.org/v2/style-guide/index.html#%E6%A8%A1%E6%9D%BF%E4%B8%AD%E7%9A%84%E7%BB%84%E4%BB%B6%E5%90%8D%E5%A4%A7%E5%B0%8F%E5%86%99-%E5%BC%BA%E7%83%88%E6%8E%A8%E8%8D%90
	 */
	public String getName() {
		if (router!=null && StringUtils.isNotBlank(router.getComonentName())) {
			return router.getComonentName();
		}
		
		String componentName = null;
		String componentViewPath = getComponentViewPath();
		if (StringUtils.isBlank(componentViewPath) || LAYOUT_NODE.equals(componentViewPath)) {
			componentName = componentName();
		}else if (componentViewPath.contains("dsqlMgr/dsqlTablePage")) {
			// 特殊处理，避免重复组件名称
			componentName = getId().toString();
		} else {
			List<String> strs = GuavaUtils.splitAsStream(componentViewPath, "/")
						.map(str -> StringUtils.capitalize(str))
						.collect(Collectors.toList());
			componentName = StringUtils.join(strs, "");
		}
		return componentName;
	}

	private String componentName() {
		if (StringUtils.isNotBlank(url)) {
			String componentName = StringUtils.toCamelWithoutConvert2LowerCase(url, '/', true);
			componentName = StringUtils.toCamelWithoutConvert2LowerCase(componentName, '-', true);
			return componentName;
		}
		return super.getName().replace("_", "");
	}
	
	public String getRedirect() {
		List<VueRouterTreeModel> children = getChildren();
		if(children.isEmpty()) {
			return null;
		}
		return "noredirect";
	}
	

	public List<VueRouterTreeModel> getChildren() {
		// 过滤所有隐藏节点（非菜单节点）, 非菜单节点均设置为了隐藏
//		List<VueRouterTreeModel> children = super.getChildren().stream().filter(p -> !p.isHidden()).collect(Collectors.toList());
		return super.getChildren();
	}
	
	/****
	 * 组件的viewPath，即前端组件的路径
	 * 如果是Layout，则表示这是一个有子节点的菜单
	 * @author weishao zeng
	 * @return
	 */
	public String getComponentViewPath() {
//		String title = (String)getMeta().get("title");
//		if ("部门管理".equals(title)) {
//			System.out.println("test");
//		}
		String componentViewPath = router==null?"":router.getComponentViewPath();
		if (StringUtils.isNotBlank(componentViewPath)) {
			return componentViewPath;
		}
		// 如果是外部链接，则不需要返回组件的view路径
		if (RequestUtils.isHttpPath(url)) {
			return null;
		}
		if(!getChildren().isEmpty()) {
			return LAYOUT_NODE;
		}
		String viewPath = StringUtils.toCamelWithoutConvert2LowerCase(url, '-', false);
		viewPath = StringUtils.trimStartWith(viewPath, "/");
		return viewPath;
	}
	
	public Object getProps() {
		if (router==null) {
			return null;
		}
		if (router.isParamsAsProps()) {
			return true;
		}
		return router.getProps();
	}
	
	
	/*public String getComponentViewPath2() {
		// 如果是外部链接，则不需要返回组件的view路径
		if (RequestUtils.isHttpPath(url)) {
			return null;
		}
		if(!getChildren().isEmpty()) {
			return LAYOUT_NODE;
		}
		
		List<String> viewPaths = GuavaUtils.splitAsStream((String) getId(), "_")
										.map(str->StringUtils.uncapitalize(str))
										.collect(Collectors.toList());
		String viewPath = StringUtils.join(viewPaths, "/");
		viewPath = StringUtils.trimStartWith(viewPath, "/");
		return viewPath;
	}*/

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	
	public void setIcon(String icon) {
		this.meta.put("icon", icon);
	}
	
	public void addMetas(Map<String, Object> meta){
		if(LangUtils.isEmpty(meta)){
			return ;
		}
		if(this.meta == null){
			this.meta = Maps.newHashMap();
		}
		this.meta.putAll(meta);
		this.router = (RouteData)this.meta.remove(VueRouteDatas.FIELD_ROUTER);
	}
	
//	public String getTemplate() {
//		if (this.router==null) {
//			return null;
//		}
//		return this.router.getTemplate();
//	}
//	
//	public boolean hasTemplate() {
//		String template = getTemplate();
//		return StringUtils.isNotBlank(template);
//	}
	
	@Data
	@NoArgsConstructor
	public static class RouteData {
		String componentViewPath;
		Object props;
		boolean paramsAsProps;
		String comonentName;

		public RouteData(String componentViewPath) {
			super();
			this.componentViewPath = componentViewPath;
		}
		
		/***
		 * 
		 * @param componentViewPath
		 * @param props 这里的map对象会生成前端路由的props属性
		 */
		public RouteData(String componentViewPath, Map<String, Object> props) {
			super();
			this.componentViewPath = componentViewPath;
			this.props = props;
		}
		
		public RouteData(boolean paramsAsProps, String... paramNames) {
			super();
			this.paramsAsProps = paramsAsProps;
			this.props = paramNames;
		}
	}
	

}
