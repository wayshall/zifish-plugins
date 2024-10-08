
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
import org.onetwo.dbm.id.SnowflakeGenerator;
import org.onetwo.dbm.jpa.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/***
 * swagger响应表
 */
@SuppressWarnings("serial")
@Entity
@Table(name="api_swagger_response")
@Data
@EqualsAndHashCode(callSuper=true)
public class SwaggerResponseEntity extends BaseEntity  {

    @Id
    //@GeneratedValue(strategy=GenerationType.IDENTITY)
    @GeneratedValue(strategy = GenerationType.AUTO, generator="snowflake") 
    @DbmIdGenerator(name="snowflake", generatorClass=SnowflakeGenerator.class)
    @NotNull
    Long id;
    
    /***
     * 
     */
    @NotBlank
    @Length(max=200)
    String jsonType;
    
    /***
     * 名称
     */
    @NotBlank
    @Length(max=50)
    String responseCode;
    
    /***
     * 描述
     */
    @NotBlank
    @Length(max=1000)
    String description;
    
    /***
     * 所属操作id，全局参数为0
     */
    String operationId;
    
    /***
     * 
     */
    @NotBlank
    @Length(max=2000)
    String jsonData;
    /***
     * 所属swagger文档
     */
    Long swaggerId;
    
}