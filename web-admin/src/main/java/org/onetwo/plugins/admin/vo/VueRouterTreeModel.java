package org.onetwo.plugins.admin.vo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.onetwo.common.tree.AbstractTreeModel;
import org.onetwo.common.utils.GuavaUtils;
import org.onetwo.common.utils.LangUtils;
import org.onetwo.common.utils.StringUtils;
import org.onetwo.common.web.utils.RequestUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.Setter;

/**
 * @author wayshall
 * <br/>
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties({"id", "parent", "parentId", "sort", "level", "index", "leafage", "first", "last"})
public class VueRouterTreeModel extends AbstractTreeModel<VueRouterTreeModel> {
	@Getter
	@Setter
	private String url;
	//menu is false, permission is true
	private boolean hidden;
	@Getter
	@Setter
	private Map<String, Object> meta = Maps.newHashMap();

	public VueRouterTreeModel(String id, String title, String parentId) {
		super(id, id, parentId);
		meta.put("title", title);
	}
	
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
		return path; 
	}

	/****
	 * vue组件命名推荐使用PascalCase，即单词大写开头的风格
	 * https://cn.vuejs.org/v2/style-guide/index.html#%E6%A8%A1%E6%9D%BF%E4%B8%AD%E7%9A%84%E7%BB%84%E4%BB%B6%E5%90%8D%E5%A4%A7%E5%B0%8F%E5%86%99-%E5%BC%BA%E7%83%88%E6%8E%A8%E8%8D%90
	 */
	public String getName() {
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
		// 如果是外部链接，则不需要返回组件的view路径
		if (RequestUtils.isHttpPath(url)) {
			return null;
		}
		if(!getChildren().isEmpty()) {
			return "Layout";
		}
		String viewPath = StringUtils.toCamelWithoutConvert2LowerCase(url, '-', true);
		viewPath = StringUtils.trimStartWith(viewPath, "/");
		return viewPath;
	}
	
	public String getComponentViewPath2() {
		// 如果是外部链接，则不需要返回组件的view路径
		if (RequestUtils.isHttpPath(url)) {
			return null;
		}
		if(!getChildren().isEmpty()) {
			return "Layout";
		}
		
		List<String> viewPaths = GuavaUtils.splitAsStream((String) getId(), "_")
										.map(str->StringUtils.uncapitalize(str))
										.collect(Collectors.toList());
		String viewPath = StringUtils.join(viewPaths, "/");
		viewPath = StringUtils.trimStartWith(viewPath, "/");
		return viewPath;
	}

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
	}

}
