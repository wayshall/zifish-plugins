package org.onetwo.plugins.admin.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;

import org.hibernate.validator.constraints.NotBlank;
import org.onetwo.common.db.TimeRecordableEntity;
import org.onetwo.common.db.spi.CrudEntityManager;
import org.onetwo.dbm.core.BaseModel;
import org.onetwo.dbm.utils.Dbms;
import org.onetwo.plugins.admin.utils.WebConstant.ValidGroup.ValidWhenEdit;
import org.onetwo.plugins.admin.utils.WebConstant.ValidGroup.ValidWhenNew;

import lombok.Data;
import lombok.EqualsAndHashCode;


/*****
 * 
 * @Entity
 */
@SuppressWarnings("serial")
@Entity
@Table(name="admin_application")
@Data
@EqualsAndHashCode(callSuper=true)
public class AdminApplication extends BaseModel<AdminApplication, String> implements Serializable, TimeRecordableEntity {

	final public static CrudEntityManager<AdminApplication, String> MANAGER = Dbms.obtainCrudManager(AdminApplication.class);

	@Id
	@NotBlank
	@Size(min=1, max=20, groups={Default.class, ValidWhenNew.class, ValidWhenEdit.class})
	private String code;
	@NotBlank
	@Size(min=1, max=100, groups={Default.class, ValidWhenNew.class, ValidWhenEdit.class})
	private String name;

    private String baseUrl;

    private Date createAt;

    private Date updateAt;
	
}