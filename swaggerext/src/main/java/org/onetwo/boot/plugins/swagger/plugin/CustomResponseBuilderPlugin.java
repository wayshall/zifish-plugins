package org.onetwo.boot.plugins.swagger.plugin;

import java.util.Optional;

import org.onetwo.boot.core.web.view.XResponseView;
import org.onetwo.common.data.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import com.fasterxml.classmate.TypeResolver;

import springfox.documentation.builders.OperationBuilder;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.contexts.ModelContext;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

/**
 * TODO: 统一把bff controller的返回类型换位DataResult的model
 * 
 * @author weishao zeng
 * <br/>
 */
//@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER + 100)
public class CustomResponseBuilderPlugin implements OperationBuilderPlugin {

	@Autowired
	private TypeNameExtractor nameExtractor;
	@Autowired
    private TypeResolver typeResolver;
	  
	@Override
	public boolean supports(DocumentationType delimiter) {
		return true;
	}

	@Override
	public void apply(OperationContext context) {
		
		Optional<XResponseView> xview = context.findControllerAnnotation(XResponseView.class);
		if (xview.isPresent()) {
//		    ModelContext modelContext = ModelContext.returnValue(
//		    	"",
//		        context.getGroupName(),
//		        typeResolver.resolve(Result.class),
//		        Optional.empty(),
//		        context.getDocumentationType(),
//		        context.getAlternateTypeProvider(),
//		        context.getGenericsNamingStrategy(),
//		        context.getIgnorableParameterTypes());
			ModelContext modelContext = context.operationModelsBuilder().addReturn(typeResolver.resolve(Result.class));

		    OperationBuilder ob = context.operationBuilder();
		    
//		    Response response = new ResponseBuilder().code("200")
//		    										.description("success")
//		    										.build();
//		    ob.responses(null);
		    
		    
//			ConfigurablePropertyAccessor cpa = SpringUtils.newPropertyAccessor(ob, true);
//			ModelReference returnResponseModel = (ModelReference)cpa.getPropertyValue("responseModel");
//			
//		    ModelReference responseModel = ResolvedTypes.modelRefFactory(modelContext, nameExtractor).apply(typeResolver.resolve(DataResult.class));
//		    
//		    // 替换responseModel
//			ConfigurablePropertyAccessor responseModelWrapper = SpringUtils.newPropertyAccessor(responseModel, true);
//			responseModelWrapper.setPropertyValue("itemModel", Optional.ofNullable(returnResponseModel));
//			context.operationBuilder().responseModel(responseModel);
//			
//			Set<ResponseMessage> responses = (Set<ResponseMessage>)cpa.getPropertyValue("responseMessages");
//			responses.stream().filter(rm -> rm.getCode()==200).findFirst().ifPresent(rm -> {
//				ConfigurablePropertyAccessor c = SpringUtils.newPropertyAccessor(rm, true);
//				c.setPropertyValue("responseModel", responseModel);
//			});
		}
	}
	
}

