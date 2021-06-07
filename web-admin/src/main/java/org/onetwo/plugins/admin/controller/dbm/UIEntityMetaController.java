package org.onetwo.plugins.admin.controller.dbm;

import java.util.List;

import org.onetwo.boot.core.web.controller.AbstractBaseController;
import org.onetwo.dbm.ui.core.DefaultUIEntityMetaService;
import org.onetwo.dbm.ui.vo.SearchableFieldVO;
import org.onetwo.ext.permission.api.annotation.ByPermissionClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author weishao zeng
 * <br/>
 */
@RestController
@RequestMapping("/dbm/uientitymeta")
public class UIEntityMetaController extends AbstractBaseController {
	
	@Autowired
	private DefaultUIEntityMetaService entityMetaService;
	
	@ByPermissionClass
	@GetMapping(path = "getSearchFields")
	public List<SearchableFieldVO> getSearchFields(String entity) {
		List<SearchableFieldVO> fields = entityMetaService.getSearchFields(entity);
		return fields;
	}

}
