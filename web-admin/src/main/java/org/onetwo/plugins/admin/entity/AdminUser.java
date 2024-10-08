package org.onetwo.plugins.admin.entity;

import java.util.Date;
import java.util.Optional;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.onetwo.boot.utils.ImageUrlJsonSerializer;
import org.onetwo.common.jackson.JsonMapper;
import org.onetwo.common.spring.validator.annotation.Mobile;
import org.onetwo.common.utils.StringUtils;
import org.onetwo.common.web.userdetails.UserRoot;
import org.onetwo.dbm.annotation.DbmTableIdGenerator;
import org.onetwo.plugins.admin.utils.DataUtils;
import org.onetwo.plugins.admin.utils.Enums.UserStatus;
import org.onetwo.plugins.admin.utils.WebConstant.DictKeys;
import org.onetwo.plugins.admin.utils.WebConstant.ValidGroup.ValidWhenNew;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;
import lombok.EqualsAndHashCode;

/***
 * 保留机构id，用于业务扩展
 * @author way
 *
 */
@Entity
@Table(name="admin_user")
@Data
@EqualsAndHashCode(callSuper=true)
@SuppressWarnings("serial")
public class AdminUser extends AdminTenantable implements UserRoot {
	
//	@Id
//	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@DbmTableIdGenerator("seq_admin_user")
    private Long id;
	
	@NotBlank(groups=ValidWhenNew.class)
    private String userName;

    private String nickName;

	@NotBlank(groups={ValidWhenNew.class})
    private String password;

    private String email;

    @Mobile
    private String mobile;

    private String gender;

    private String status;

    private Date birthday;

//    private String appCode;

	@JsonSerialize(using = ImageUrlJsonSerializer.class)
    private String avatar;
	
	/****
	 * 保留机构id，用于业务扩展
	 */
	private Long organId;
    
	
    public String getGenderName(){
    	if(StringUtils.isBlank(gender))
    		return "";
    	Optional<DataDictionary> data = DataUtils.getDictionaryCachingService().findByValue(DictKeys.SEX, gender);
    	return data.isPresent()?data.get().getName():"";
    }
    
    public String getStatusName(){
    	if(StringUtils.isBlank(status))
    		return "";
    	return UserStatus.of(status).getLabel();
    }
    
    @DateTimeFormat(iso=ISO.DATE)
    @JsonFormat(pattern="yyyy-MM-dd", timezone=JsonMapper.TIME_ZONE_CHINESE)
    public Date getBirthday() {
    	return this.birthday;
    }

	@Override
	public Boolean isSystemRootUser() {
		return getId()!=null && getId().equals(ROOT_USER_ID);
	}

}