package techpart.webpost.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
    servers = {
        @Server(url = "/", description = "Default Server URL")
    },
    info = @Info(title = "webpost API 명세서",
        description = "TEAM-F 홍지섭",
        version = "v1"))
@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi chatOpenApi() {
        // "/**" 경로에 매칭되는 API를 그룹화하여 문서화한다.
        String[] paths = {"/**"};

        return GroupedOpenApi.builder()
            .group("webpost API v1")  // 그룹 이름을 설정한다.
            .pathsToMatch(paths)     // 그룹에 속하는 경로 패턴을 지정한다.
            .build();
    }

    // API 보안 설정
    @Bean
    public OpenAPI api() {
        SecurityScheme apiKey = new SecurityScheme()
            .type(SecurityScheme.Type.APIKEY)
            .in(SecurityScheme.In.HEADER)
            .name("${name}");

        SecurityRequirement securityRequirement = new SecurityRequirement()
            .addList("apiKey");

        return new OpenAPI()
            .components(new Components().addSecuritySchemes("apiKey", apiKey))
            .addSecurityItem(securityRequirement);
    }
}