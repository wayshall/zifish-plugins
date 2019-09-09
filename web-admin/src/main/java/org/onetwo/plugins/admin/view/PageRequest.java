package org.onetwo.plugins.admin.view;

import org.onetwo.common.utils.Page;

import lombok.Data;

/**
 * @author wayshall
 * <br/>
 */
@Data
public class PageRequest {
	protected int page = 1;
	protected int rows = Page.getDefaultPageSize();
	protected boolean pagination = true;
	
	public <E> Page<E> toPageObject(){
		Page<E> pageObj = Page.create(page, rows);
		pageObj.setPagination(pagination);
		return pageObj;
	}

}
