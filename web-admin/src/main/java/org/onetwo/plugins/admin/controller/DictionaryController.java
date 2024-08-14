package org.onetwo.plugins.admin.controller;

import java.util.List;

import javax.annotation.Resource;

import org.onetwo.boot.core.web.controller.DateInitBinder;
import org.onetwo.common.data.Result;
import org.onetwo.common.spring.mvc.utils.DataResults;
import org.onetwo.common.tree.DefaultTreeModel;
import org.onetwo.common.utils.Page;
import org.onetwo.common.utils.StringUtils;
import org.onetwo.common.utils.map.MappableMap;
import org.onetwo.ext.permission.api.annotation.ByPermissionClass;
import org.onetwo.plugins.admin.AdminMgr.DictMgr;
import org.onetwo.plugins.admin.entity.DataDictionary;
import org.onetwo.plugins.admin.service.impl.DictionaryServiceImpl;
import org.onetwo.plugins.admin.view.EasyModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dictionary")
public class DictionaryController extends WebAdminBaseController implements DateInitBinder {
	
	@Resource
	private DictionaryServiceImpl dictionaryServiceImpl;
	
	@ByPermissionClass(DictMgr.class)
	@RequestMapping(method=RequestMethod.GET)
	public Page<DataDictionary> index(Page<DataDictionary> page, String parentCode){
//		return responsePageOrData("dictionary-index", ()->{
//				dictionaryServiceImpl.findPage(page, parentCode);
//				List<MappableMap> districtMaps = EasyModel.newTreeBuilder(DataDictionary.class)
//						.mapId("code")
//						.mapText("name")
//						.mapIsStateOpen(src->false)
//						.build(page.getResult());
//				EasyDataGrid<MappableMap> easyPage = EasyDataGrid.create(districtMaps, page);
//				return easyPage;
//			}
//		);
		return dictionaryServiceImpl.findPage(page, parentCode);
	}

	@ByPermissionClass(value = DictMgr.class, overrideMenuUrl = false)
	@GetMapping(path="tree")
	@ResponseBody
	public List<DefaultTreeModel> tree(){
		List<DefaultTreeModel> trees = this.dictionaryServiceImpl.loadAsTree();
		return trees;
	}
	
	@ByPermissionClass
	@RequestMapping(value="children", method=RequestMethod.GET)
	public List<MappableMap> children(String parentCode){
		List<DataDictionary> datalist = dictionaryServiceImpl.findChildren(parentCode);
		List<MappableMap> districtMaps = EasyModel.newTreeBuilder(DataDictionary.class)
//												.specifyMappedFields()
												.mapId("code")
												.mapText("name")
												.mapIsStateOpen((src)->StringUtils.isBlank(src.getParentCode()))
												.build(datalist);
//		return mv("dictionary-index", "datalist", datalist, DataWrapper.wrap(districtMaps));
		return districtMaps;
	}
	
	@ByPermissionClass(DictMgr.class)
	@RequestMapping(method=RequestMethod.POST)
	public Result create(DataDictionary dictionary){
		dictionaryServiceImpl.save(dictionary);
		return DataResults.success("保存成功").build();
	}
	
	@ByPermissionClass(DictMgr.class)
	@RequestMapping(value="{code}", method=RequestMethod.GET)
	public DataDictionary get(@PathVariable("code") String code){
		DataDictionary dictionary = dictionaryServiceImpl.findByCode(code);
//		MappableMap dictMap = EasyModel.newSimpleBuilder(DataDictionary.class)
//				.addMapping("valid", src->{
//					return src.getValid()==null?"false":src.getValid().toString();
//				})
//				.build(dictionary);
//		return responseData(dictMap);
		return dictionary;
	}
	
	@ByPermissionClass(DictMgr.class)
	@RequestMapping(value="{code}", method=RequestMethod.PUT)
	public Result update(@PathVariable("code") String code, DataDictionary dictionary){
		dictionary.setCode(code);
		dictionaryServiceImpl.update(dictionary);
		return DataResults.success("保存成功").build();
	}
	
	@ByPermissionClass(DictMgr.class)
	@RequestMapping(value="{code}", method=RequestMethod.DELETE)
	public Result delete(@PathVariable("code") String code){
		dictionaryServiceImpl.deleteByCode(code);
		return DataResults.success("删除成功").build();
	}
	
	@ByPermissionClass(DictMgr.class)
	@RequestMapping(method=RequestMethod.DELETE)
	public Result deleteBatch(String[] code){
		dictionaryServiceImpl.deleteByCodes(code);
		return DataResults.success("批量删除成功").build();
	}


	//open
	/***
	 * 返回easyui下拉框数据形式
	 * @param parentCode
	 * @param valueField
	 * @return
	 */
	@ByPermissionClass
	@RequestMapping(value="combobox/{parentCode}", method=RequestMethod.GET)
	public Object combobox(@PathVariable("parentCode")String parentCode, String valueField){
		/*if(StringUtils.isBlank(parentCode))
			throw new ServiceException("parentCode不能为空!");*/
		
		if(StringUtils.isBlank(valueField)){
			valueField = "value";
		}
		List<DataDictionary> datalist = dictionaryServiceImpl.findChildren(parentCode);
		List<MappableMap> districtMaps = EasyModel.newComboBoxBuilder(DataDictionary.class)
												.specifyMappedFields()
												.mapText("name")
												.mapValue(valueField)
//												.mapSelected(src->src==empty)
												.build(datalist);
//		return districtMaps;
		return districtMaps;
	}

	@ByPermissionClass
	@RequestMapping(value="getOne/{parentCode}/{value}")
	public DataDictionary getOne(@PathVariable("parentCode") String parentCode, @PathVariable("value")String value){
		DataDictionary dict = dictionaryServiceImpl.getByTypeAndValue(parentCode, value);
//		return responseData(dict);
		return dict;
	}
	
}
