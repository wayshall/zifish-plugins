
package org.onetwo.boot.plugins.swagger.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.onetwo.dbm.annotation.DbmIdGenerator;
import org.onetwo.dbm.annotation.DbmJsonField;
import org.onetwo.dbm.id.SnowflakeGenerator;
import org.onetwo.dbm.jpa.BaseEntity;

import io.swagger.models.Info;
import lombok.Data;
import lombok.EqualsAndHashCode;

/***
 * swagger文档基础配置和信息
 */
@SuppressWarnings("serial")
@Entity
@Table(name="api_swagger")
@Data
@EqualsAndHashCode(callSuper=true)
public class SwaggerEntity extends BaseEntity  {

    @Id
    //@GeneratedValue(strategy=GenerationType.IDENTITY)
    @GeneratedValue(strategy = GenerationType.AUTO, generator="snowflake") 
    @DbmIdGenerator(name="snowflake", generatorClass=SnowflakeGenerator.class)
    @NotNull
    Long id;
    
    /***
     * 所属导入模块
     */
    Long moduleId;
    
    /***
     * 冗余的分组名称
     */
    @NotBlank
    @Length(max=100)
    String applicationName;
    
    /***
     * 主机
     */
    @NotBlank
    @Length(max=100)
    String host;
    
    /***
     * 基础路径
     */
    @NotBlank
    @Length(max=100)
    String basePath;
    
    /***
     * 
     */
    @NotBlank
    @Length(max=20)
    String swagger;
    
    /***
     * 信息
     */
    @NotBlank
    @Length(max=2000)
    @DbmJsonField
    Info info;
    
    /***
     * 文档更新次数
     */
    Integer updateCount = 1;
    
}