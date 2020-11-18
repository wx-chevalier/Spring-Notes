package wx.api.config;

import java.util.Collections;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import wx.api.config.security.SecurityConstants;
import wx.common.data.shared.id.EntityIdFactory;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

  private Docket createSwagger2Docket() {
    Docket docket = new Docket(DocumentationType.SWAGGER_2);
    // wx.common.data.shared.id.EntityId 被序列化成字符串
    for (Class clazz : EntityIdFactory.entityIdClasses) {
      docket.directModelSubstitute(clazz, String.class);
    }
    return docket;
  }

  @Bean
  public Docket allDocket() {
    return createSwagger2Docket()
        .apiInfo(apiInfo())
        .select()
        .apis(RequestHandlerSelectors.basePackage("wx.api.rest"))
        .paths(PathSelectors.any())
        .build()
        .groupName("0-默认")
        .securityContexts(Collections.singletonList(securityContext()))
        .securitySchemes(Collections.singletonList(apiKey()));
  }

  @Bean
  public Docket accountDocket() {
    return createSwagger2Docket()
        .apiInfo(apiInfo())
        .select()
        .apis(RequestHandlerSelectors.basePackage("wx.api.rest.account"))
        .paths(PathSelectors.any())
        .build()
        .groupName("1-账户和权限")
        .securityContexts(Collections.singletonList(securityContext()))
        .securitySchemes(Collections.singletonList(apiKey()));
  }

  @Bean
  public Docket utkDeviceDocket() {
    return createSwagger2Docket()
        .apiInfo(apiInfo())
        .select()
        .apis(RequestHandlerSelectors.basePackage("wx.api.rest.mes.utkdevice"))
        .paths(PathSelectors.any())
        .build()
        .groupName("2-设备相关")
        .securityContexts(Collections.singletonList(securityContext()))
        .securitySchemes(Collections.singletonList(apiKey()));
  }

  @Bean
  public Docket iotDocket() {
    return createSwagger2Docket()
        .apiInfo(apiInfo())
        .select()
        .apis(RequestHandlerSelectors.basePackage("wx.api.rest.mes.iot"))
        .paths(PathSelectors.any())
        .build()
        .groupName("3-IoT")
        .securityContexts(Collections.singletonList(securityContext()))
        .securitySchemes(Collections.singletonList(apiKey()));
  }

  @Bean
  public Docket workOrderDocket() {
    return createSwagger2Docket()
        .apiInfo(apiInfo())
        .select()
        .apis(RequestHandlerSelectors.basePackage("wx.api.rest.workorder"))
        .paths(PathSelectors.any())
        .build()
        .groupName("4-工单")
        .securityContexts(Collections.singletonList(securityContext()))
        .securitySchemes(Collections.singletonList(apiKey()));
  }

  @Bean
  public Docket othersDocket() {
    return createSwagger2Docket()
        .apiInfo(apiInfo())
        .select()
        .apis(RequestHandlerSelectors.basePackage("wx.api.rest.common"))
        .paths(PathSelectors.any())
        .build()
        .groupName("5-通用")
        .securityContexts(Collections.singletonList(securityContext()))
        .securitySchemes(Collections.singletonList(apiKey()));
  }

  @Bean
  public Docket analysisDocket() {
    return createSwagger2Docket()
        .apiInfo(apiInfo())
        .select()
        .apis(RequestHandlerSelectors.basePackage("wx.api.rest.analysis"))
        .paths(PathSelectors.any())
        .build()
        .groupName("6-优分析")
        .securityContexts(Collections.singletonList(securityContext()))
        .securitySchemes(Collections.singletonList(apiKey()));
  }

  @Bean
  public Docket adminDocket() {
    return createSwagger2Docket()
        .apiInfo(apiInfo())
        .select()
        .apis(RequestHandlerSelectors.basePackage("wx.api.rest.admin"))
        .paths(PathSelectors.any())
        .build()
        .groupName("7-管理端")
        .securityContexts(Collections.singletonList(securityContext()))
        .securitySchemes(Collections.singletonList(apiKey()));
  }

  private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
        .title("优联云平台")
        .description(
            "✅ 已完成, 自测通过\n ⚠️ 部分需求完成，可用 \n 🐛 发现BUG，正在处理中 \n ❌ 不可用的接口，正在处理中 \n 🚧 正在计划开发的接口")
        .version("0.1")
        .build();
  }

  private ApiKey apiKey() {
    return new ApiKey("授权信息", SecurityConstants.AUTH_HEADER, "header");
  }

  private SecurityContext securityContext() {
    return SecurityContext.builder()
        .securityReferences(defaultAuth())
        .forPaths(PathSelectors.any())
        .build();
  }

  private List<SecurityReference> defaultAuth() {
    AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
    AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
    authorizationScopes[0] = authorizationScope;
    return Collections.singletonList(new SecurityReference("授权信息", authorizationScopes));
  }
}
