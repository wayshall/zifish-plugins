
package org.onetwo.plugins.admin.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.SafeHtml;
import org.onetwo.dbm.annotation.DbmJsonField;
import org.onetwo.dbm.annotation.SnowflakeId;
import org.onetwo.dbm.ui.annotation.DUIEntity;
import org.onetwo.dbm.ui.annotation.DUIField;
import org.onetwo.dbm.ui.annotation.DUIInput;

import lombok.Data;

/***
 * 后台登陆日志
 */
@SuppressWarnings("serial")
@Entity
@Table(name="admin_user_log")
@Data
//@EqualsAndHashCode(callSuper=true)
@DUIEntity(
        name = "AdminLoginLog", 
        label = "后台用户操作日志"
)
public class AdminUserLogEntity implements Serializable {
	
	public static final String ANONYMOUS_USER = "ANONYMOUS";

    @SnowflakeId
    @NotNull
    Long id;
    
    /***
     * 用户ip
     */
    @Length(max=50)
    @SafeHtml
    @DUIField(label = "用户ip", order = 1)
    String userIp;
    
    /***
     * 操作时间
     */
    @DUIInput(type=DUIInput.InputTypes.DATE_TIME)
    @DUIField(label = "操作时间", order = 2)
    Date operationAt;
    
    /***
     * 错误信息
     */
    @Length(max=500)
    @SafeHtml
    @DUIInput(type=DUIInput.InputTypes.TEXTAREA)
    @DUIField(label = "错误信息", order = 3)
    String errorMsg;
    
    /***
     * 用户名称
     */
    @Length(max=50)
    @SafeHtml
    @DUIField(label = "用户名称", order = 4)
    String userName;
    
    /***
     * 操作名称
     */
    @Length(max=50)
    @SafeHtml
    @DUIField(label = "操作名称", order = 5)
    String operationName;
    
    /***
     * 操作代码
     */
    @Length(max=10)
    @SafeHtml
    @DUIField(label = "操作代码", order = 5)
    String operationCode;
    
    /***
     * 操作方法
     */
    @Length(max=10)
    @SafeHtml
    @DUIField(label = "操作方法", order = 6)
    String requestMethod;
    
    /***
     * 是否成功
     */
    @DUIInput(type=DUIInput.InputTypes.SWITCH)
    @DUIField(label = "是否成功", order = 7)
    Boolean isSuccess;
    
    /***
     * 操作的url
     */
    @Length(max=200)
    @SafeHtml
    @DUIField(label = "操作的url", order = 8)
    String requestUrl;
    
    /***
     * 用户ID
     */
    @NotNull
    @DUIInput(type=DUIInput.InputTypes.NUMBER)
    @DUIField(label = "用户ID", order = 9)
    Long userId;
    
    /***
     * 用户昵称
     */
    @Length(max=50)
    @SafeHtml
    @DUIField(label = "用户昵称", order = 10)
    String nickName;
    
    /***
     * 用户浏览器
     */
    @Length(max=20)
    @SafeHtml
    @DUIField(label = "用户浏览器", order = 11)
    String browser;
    
    /***
     * 用户agent
     */
    @Length(max=2000)
    String userAgent;
    
    @DbmJsonField
    Object requestParameters;
    
}