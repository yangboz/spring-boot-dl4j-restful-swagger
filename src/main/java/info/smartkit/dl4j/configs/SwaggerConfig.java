package info.smartkit.dl4j.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * The Class SwaggerConfig.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("info.smartkit.dl4j.controllers"))
                .paths(PathSelectors.ant("/dl4j/*"))
                .build()
                .apiInfo(apiInfo());
        //				.useDefaultResponseMessages(false)
//				.globalResponseMessage(RequestMethod.GET,
//						new ArrayList(new ResponseMessageBuilder()
//										.code(500)
//										.message("500 message")
//										.responseModel(new ModelRef("Error"))
//										.build(),
//								new ResponseMessageBuilder()
//										.code(403)
//										.message("Forbidden!")
//										.build()));
    }

    private ApiInfo apiInfo() {
        ApiInfo apiInfo = new ApiInfo(
                "AI.SMARTKIT.INFO REST API",
                "SMARTKIT.INFO description of AI APIs.",
                "API TOS",
                "Terms of service",
                "contact@smartkit.info",
                "License of API",
                "API license URL");
        return apiInfo;
    }


}
