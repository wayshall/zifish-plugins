package org.onetwo.boot.plugins.swagger.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.onetwo.boot.plugins.swagger.entity.SwaggerEntity;
import org.onetwo.boot.plugins.swagger.entity.SwaggerModuleEntity;
import org.onetwo.boot.plugins.swagger.entity.SwaggerModuleEntity.Status;
import org.onetwo.boot.plugins.swagger.entity.SwaggerModuleEntity.StoreTypes;
import org.onetwo.boot.plugins.swagger.vo.ModuleListResponse;
import org.onetwo.common.db.builder.Querys;
import org.onetwo.common.db.spi.BaseEntityManager;
import org.onetwo.common.spring.SpringUtils;
import org.onetwo.common.spring.copier.CopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.models.Swagger;
import io.swagger.parser.SwaggerParser;
import springfox.documentation.spring.web.json.Json;
import springfox.documentation.spring.web.json.JsonSerializer;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

/**
 * @author wayshall
 * <br/>
 */
@Transactional
@Service
public class DatabaseSwaggerResourceService {
	
	@Autowired
	private BaseEntityManager baseEntityManager;
	@Autowired
	private SwaggerServiceImpl swaggerService;
	@Autowired
	private JsonSerializer jsonSerializer;
	@Autowired
	private SwaggerResourcesProvider swaggerResources;
	
	/***
	 * 通过moduleName查找swagger，并转为Json对象
	 * @author weishao zeng
	 * @param groupName
	 * @return
	 */
	public Optional<Json> findJsonByGroupName(String groupName){
		Optional<Json> json = findSwaggerByGroupName(groupName).map(s->jsonSerializer.toJson(s));
		return json;
	}

	/****
	 * 通过moduleId查找swagger，并转为Json对象
	 * @author weishao zeng
	 * @param moduleId
	 * @return
	 */
	public Optional<Json> findJsonByModuleId(Long moduleId){
		Optional<Json> json = findSwaggerByModuleId(moduleId).map(s->jsonSerializer.toJson(s));
		return json;
	}

	public Optional<Swagger> findSwaggerByGroupName(String applicationName){
		SwaggerModuleEntity file = findByModuleName(applicationName);
		if(file==null){
//			throw new BaseException("swagger module not found for: " + groupName);
			return Optional.empty();
		}
		Optional<Swagger> swagger = findSwaggerByModuleId(file.getId());
		return swagger;
	}
	

	public Optional<Swagger> findSwaggerByModuleId(Long moduleId){
		SwaggerEntity swaggerEntity = swaggerService.findByModuleId(moduleId);
		if(swaggerEntity==null){
//			throw new BaseException("swagger not found for swaggerFileId: " + file.getId());
			return Optional.empty();
		}
		Swagger swagger = swaggerService.convertBySwagger(swaggerEntity);
		return Optional.ofNullable(swagger);
	}
	
	
	/***
	 * 通过分组名称查找
	 * @author wayshall
	 * @param groupName
	 * @return
	 */
	public SwaggerModuleEntity findByModuleName(String groupName){
		SwaggerModuleEntity file = baseEntityManager.findOne(SwaggerModuleEntity.class, "moduleName", groupName);
		return file;
	}

	public List<SwaggerModuleEntity> findAllEnabled(){
		return findListByStatus(SwaggerModuleEntity.Status.ENABLED);
	}

	/****
	 * 查找所有module的swagger resouce
	 * @author weishao zeng
	 * @return
	 */
	public List<ModuleListResponse> findModuleSwaggerResource(boolean includeDefault) {
		List<ModuleListResponse> resources = findAllEnabled().stream().map(se->{
			ModuleListResponse swaggerResource = resource(se);
			return swaggerResource;
		})
		.collect(Collectors.toList());
		Collections.sort(resources);
		
		if (includeDefault) {
			swaggerResources.get().stream().filter(sr -> sr.getName().equals("default")).findFirst().ifPresent(sr -> {
				ModuleListResponse mr = CopyUtils.copy(ModuleListResponse.class, sr);
				resources.add(0, mr);
			});
		}
		return resources;
	}

	private ModuleListResponse resource(SwaggerModuleEntity sfe) {
		ModuleListResponse swaggerResource = new ModuleListResponse();
		swaggerResource.setName(sfe.getModuleName());
		swaggerResource.setSwaggerVersion("2.0");
		swaggerResource.setModuleId(sfe.getId().toString());
		swaggerResource.setLocation("/v2/api-docs?group="+sfe.getModuleName());
		return swaggerResource;
	}
	
	public List<SwaggerModuleEntity> findListByStatus(Status status){
		List<SwaggerModuleEntity> files = Querys.from(baseEntityManager, SwaggerModuleEntity.class)
											  .where()
											  	.field("status").equalTo(status)
											  	.ignoreIfNull()
											  .end()
											  .toQuery()
											  .list();
		return files;
	}
	
	public SwaggerModuleEntity saveSwaggerModule(StoreTypes storeType, Swagger swagger, String content){
//		String moduleName = swagger.getInfo().getTitle() + "_" + swagger.getInfo().getVersion();
		String moduleName = swagger.getInfo().getTitle();
		SwaggerModuleEntity swaggerFileEntity = baseEntityManager.findOne(SwaggerModuleEntity.class, "moduleName", moduleName);
		if(swaggerFileEntity==null){
			swaggerFileEntity = new SwaggerModuleEntity();
		}
//		swaggerFileEntity.setGroupName(groupName);
		swaggerFileEntity.setModuleName(moduleName);
		swaggerFileEntity.setStatus(Status.ENABLED);
		swaggerFileEntity.setStoreType(storeType);
		swaggerFileEntity.setContent(content);
		baseEntityManager.save(swaggerFileEntity);
		return swaggerFileEntity;
	}
	
	public SwaggerModuleEntity importSwagger(MultipartFile swaggerFile){
		String content = SpringUtils.readMultipartFile(swaggerFile);
		return importSwagger(content);
	}
	
	public SwaggerModuleEntity importSwagger(String content){
		StoreTypes storeType;
		Swagger swagger;
		SwaggerParser parser = new SwaggerParser();
		if(content.startsWith("http")){
			storeType = StoreTypes.URL;
			swagger = parser.read(content);
		}else if(content.startsWith("file:")){
			storeType = StoreTypes.DATA;
			swagger = parser.read(content);
		}else{
			storeType = StoreTypes.DATA;
			swagger = parser.parse(content);
		}
		SwaggerModuleEntity module = saveSwaggerModule(storeType, swagger, content);
		
		this.swaggerService.save(module, swagger);
		return module;
	}

	public SwaggerModuleEntity removeWithCascadeData(String groupName){
		SwaggerModuleEntity swaggerFileEntity = findByModuleName(groupName);
		return removeWithCascadeData(swaggerFileEntity);
	}
	public SwaggerModuleEntity removeWithCascadeData(Long swaggerFileId){
		SwaggerModuleEntity swaggerFileEntity = baseEntityManager.load(SwaggerModuleEntity.class, swaggerFileId);
		return removeWithCascadeData(swaggerFileEntity);
	}
    public SwaggerModuleEntity removeWithCascadeData(SwaggerModuleEntity swaggerFileEntity){
    	this.swaggerService.removeWithCascadeData(swaggerFileEntity.getId());
    	baseEntityManager.remove(swaggerFileEntity);
    	return swaggerFileEntity;
    }
}
