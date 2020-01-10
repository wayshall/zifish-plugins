package org.onetwo.plugins.admin.controller.dbm;

import org.onetwo.boot.core.web.controller.AbstractBaseController;
import org.onetwo.dbm.ui.spi.UISelectDataProviderService;
import org.onetwo.dbm.ui.vo.UISelectDataRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author weishao zeng
 * <br/>
 */
@RestController
@RequestMapping("/dbm/uiselect")
@ConditionalOnBean(UISelectDataProviderService.class)
public class UISelectController extends AbstractBaseController {
	
	@Autowired
	private UISelectDataProviderService selectDataProviderService;
	
	@GetMapping(path = "dataProvider")
	public Object dataProvider(UISelectDataRequest request) {
		return selectDataProviderService.getDatas(request);
	}

}
