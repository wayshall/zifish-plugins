package org.onetwo.plugins.admin.controller;


import org.onetwo.common.data.Result;
import org.onetwo.common.spring.mvc.utils.DataResults;
import org.onetwo.ext.permission.api.annotation.ByPermissionClass;
import org.onetwo.plugins.admin.AdminMgr.DictMgr;
import org.onetwo.plugins.admin.service.DictionaryImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/dictionary/import")
//@Controller
public class DictionaryImportController extends WebAdminBaseController {
	 
	@Autowired
	private DictionaryImportService dictionaryService;
	
	public DictionaryImportController() {
	}

	@ByPermissionClass(DictMgr.class)
	@RequestMapping(method=RequestMethod.POST)
	public Result importDatas(MultipartFile dataFile){
		int count = this.dictionaryService.importDatas(dataFile);
		return DataResults.success("已同步"+count+"条字典数据！").build();
	}

	
}