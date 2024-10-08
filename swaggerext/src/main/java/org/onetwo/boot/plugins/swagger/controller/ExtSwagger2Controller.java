package org.onetwo.boot.plugins.swagger.controller;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.onetwo.boot.plugins.swagger.service.impl.DatabaseSwaggerResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.json.Json;
import springfox.documentation.spring.web.json.JsonSerializer;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;
import springfox.documentation.swagger2.web.Swagger2ControllerWebMvc;
import springfox.documentation.swagger2.web.WebMvcSwaggerTransformationFilter;

/**
 * @author wayshall <br/>
 */
@Controller
@ApiIgnore
public class ExtSwagger2Controller extends Swagger2ControllerWebMvc {
	private static final String HAL_MEDIA_TYPE = "application/hal+json";
	
	@Autowired
	private DatabaseSwaggerResourceService swaggerResourceService;

	@Autowired
	public ExtSwagger2Controller(PluginRegistry<WebMvcSwaggerTransformationFilter, DocumentationType> transformations,
			DocumentationCache documentationCache,
			ServiceModelToSwagger2Mapper mapper, JsonSerializer jsonSerializer) {
		super(documentationCache, mapper, jsonSerializer, transformations);
	}


	@RequestMapping(
      method = RequestMethod.GET,
      produces = {APPLICATION_JSON_VALUE, HAL_MEDIA_TYPE})
	@Override
	public ResponseEntity<Json> getDocumentation(@RequestParam(value = "group", required = false) String swaggerGroup, HttpServletRequest servletRequest) {
		ResponseEntity<Json> res = super.getDocumentation(swaggerGroup, servletRequest);
		if(res.getStatusCode()!=HttpStatus.NOT_FOUND){
			return res;
		}
		Optional<Json> json = swaggerResourceService.findJsonByGroupName(swaggerGroup);
		if(!json.isPresent()){
			return res;
		}
		res = new ResponseEntity<Json>(json.get(), HttpStatus.OK);
		return res;
	}
    

    @RequestMapping(
      value = "/v2/api-docs/{moduleId}",
      method = RequestMethod.GET,
      produces = { APPLICATION_JSON_VALUE, HAL_MEDIA_TYPE })
    @ResponseBody
	public ResponseEntity<Json> getDocumentationByModuleId(@PathVariable("moduleId") Long moduleId, HttpServletRequest servletRequest) {
    	ResponseEntity<Json> res = null;
		Optional<Json> json = swaggerResourceService.findJsonByModuleId(moduleId);
		if(!json.isPresent()){
			res = new ResponseEntity<Json>(HttpStatus.NOT_FOUND);
			return res;
		}
		res = new ResponseEntity<Json>(json.get(), HttpStatus.OK);
		return res;
	}

}
