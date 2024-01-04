
package org.onetwo.boot.plugins.swagger.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.onetwo.dbm.annotation.DbmIdGenerator;
import org.onetwo.dbm.annotation.DbmJsonField;
import org.onetwo.dbm.id.SnowflakeGenerator;
import org.onetwo.dbm.jpa.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/***
 * swagger model表
 */
@SuppressWarnings("serial")
@Entity
@Table(name="api_swagger_model")
@Data
@EqualsAndHashCode(callSuper=true)
public class SwaggerModelEntity extends BaseEntity  {

    @Id
    //@GeneratedValue(strategy=GenerationType.IDENTITY)
    @GeneratedValue(strategy = GenerationType.AUTO, generator="snowflake") 
    @DbmIdGenerator(name="snowflake", generatorClass=SnowflakeGenerator.class)
    @NotNull
    Long id;
    /***
     * 所属导入文件
     */
    Long moduleId;
    
    /***
     * #/definitions/+name
     */
    @NotBlank
    @Length(max=200)
    String refPath;
    
    /***
     * 
     */
    @NotBlank
    @Length(max=200)
    String jsonType;
    
    /***
     * 所属swagger文档
     */
    Long swaggerId;
    
    /***
     * 
     */
    @NotBlank
    @Length(max=100)
    String name;
    
    /***
     * 描述
     */
    @NotBlank
    @Length(max=500)
    String description;
    
    /***
     * 
     */
    @NotBlank
    @Length(max=2000)
    @DbmJsonField
    String jsonData;
    
}