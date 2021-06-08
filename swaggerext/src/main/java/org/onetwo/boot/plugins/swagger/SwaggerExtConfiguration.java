package org.onetwo.boot.plugins.swagger;

import org.onetwo.boot.plugin.core.JFishWebPlugin;
import org.onetwo.boot.plugins.swagger.controller.ExtApiResourceController;
import org.onetwo.boot.plugins.swagger.controller.ExtSwagger2Controller;
import org.onetwo.boot.plugins.swagger.mapper.SwaggerModelMapper;
import org.onetwo.boot.plugins.swagger.service.impl.DatabaseSwaggerResourceService;
import org.onetwo.boot.plugins.swagger.util.DatabaseDocumentationCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import springfox.documentation.service.Documentation;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.json.JsonSerializer;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;
import springfox.documentation.swagger2.web.Swagger2ControllerWebMvc;
import springfox.documentation.swagger2.web.WebMvcSwaggerTransformationFilter;

/**
 * @author wayshall <br/>
 */
@Configuration
@ConditionalOnProperty(name=org.onetwo.boot.core.config.BootJFishConfig.ZIFISH_CONFIG_PREFIX+ ".swagger.dbstore", havingValue="true")
@ConditionalOnClass(Documentation.class)
@ComponentScan(basePackageClasses=DatabaseSwaggerResourceService.class)
@JFishWebPlugin(SwaggerExtPlugin.class)
public class SwaggerExtConfiguration {
	/*@Autowired
	DatabaseSwaggerResourceService databaseSwaggerResourceService;*/
	@Autowired(required=false)
	private WebMvcConfigurationSupport webMvcConfigurationSupport;
	
//	@SuppressWarnings("unchecked")
//	@Bean
//	public HandlerMapping swagger2ControllerMapping(Environment environment, Swagger2ControllerWebMvc controller) {
//		PropertySourcedRequestMappingHandlerMapping mapping = new PropertySourcedRequestMappingHandlerMapping(environment, controller);
//		
//		if (webMvcConfigurationSupport!=null) {
//			Map<String, CorsConfiguration> corsConfigurations = (Map<String, CorsConfiguration>)ReflectUtils.invokeMethod("getCorsConfigurations", webMvcConfigurationSupport);
//			mapping.setCorsConfigurations(corsConfigurations);
//		}
//		return mapping;
//	}
	
	@Bean
	public Swagger2ControllerWebMvc swagger2Controller(@Qualifier("webMvcSwaggerTransformationFilterRegistry")
    PluginRegistry<WebMvcSwaggerTransformationFilter, DocumentationType> transformations,
			DocumentationCache documentationCache,
			ServiceModelToSwagger2Mapper mapper, JsonSerializer jsonSerializer) {
		return new ExtSwagger2Controller(transformations, documentationCache, mapper,
				jsonSerializer);
	}
	
	@Bean
    public DocumentationCache resourceGroupCache(){
    	return new DatabaseDocumentationCache();
    }
	
	@Bean
	public SwaggerModelMapper swaggerModelMapper(){
		return new SwaggerModelMapper();
	}
	
	@Bean
	public ExtApiResourceController ExtApiResourceController() {
		return new ExtApiResourceController();
	}
}
