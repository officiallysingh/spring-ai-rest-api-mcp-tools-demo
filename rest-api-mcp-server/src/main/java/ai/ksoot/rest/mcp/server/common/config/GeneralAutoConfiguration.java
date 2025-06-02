package ai.ksoot.rest.mcp.server.common.config;

import ai.ksoot.rest.mcp.server.common.util.MessageProvider;
import ai.ksoot.rest.mcp.server.common.util.pagination.PaginatedResourceAssembler;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;

@Configuration
@EnableConfigurationProperties
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
public class GeneralAutoConfiguration {

  @Bean
  MessageProvider messageProvider(final MessageSource messageSource) {
    return new MessageProvider(messageSource);
  }

  @Bean
  SpringProfiles springProfiles(final Environment environment) {
    return new SpringProfiles(environment);
  }

  @Bean
  PaginatedResourceAssembler paginatedResourceAssembler(
      @Nullable final HateoasPageableHandlerMethodArgumentResolver resolver) {
    return new PaginatedResourceAssembler(resolver);
  }

  @Bean
  BeanRegistry defaultBeanRegistry(final ApplicationContext applicationContext) {
    BeanRegistry beanRegistry = new DefaultBeanRegistry();
    beanRegistry.setApplicationContext(applicationContext);
    return beanRegistry;
  }

  @Bean
  Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
    return builder ->
        builder
            .featuresToEnable(SerializationFeature.INDENT_OUTPUT)
            .serializerByType(HttpHeaders.class, new HttpHeadersSerializer())
            .deserializerByType(HttpHeaders.class, new HttpHeadersDeserializer());
  }

  //    @Bean
  //    public CookieHeaderFilter bodyFilter() {
  //      return merge(
  //              defaultValue(),
  //              replaceJsonStringProperty(singleton("secret"), "XXX"));
  //    }

  //  private HeaderFilter whitelistHeaderFilter() {
  //    Set<String> allowedHeaders = Set.of(
  //            "accept",
  //            "accept-encoding",
  //            "accept-language",
  //            "connection",
  //            "content-length",
  //            "content-type",
  //            "host",
  //            "origin",
  //            "referer"
  //    );
  //
  //    return (headers, value) -> {
  //      if (allowedHeaders.contains(headers. name.toLowerCase())) {
  //        return value;
  //      }
  //      return null; // Completely remove other headers from log
  //    };
  //  }
}
