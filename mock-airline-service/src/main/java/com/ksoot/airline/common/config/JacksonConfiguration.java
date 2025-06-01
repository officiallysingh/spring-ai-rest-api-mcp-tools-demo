package com.ksoot.airline.common.config;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.Module;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Iterator;
import java.util.Map;
import javax.money.MonetaryAmount;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.jackson.datatype.money.MoneyModule;

@Configuration
public class JacksonConfiguration {

  @Bean
  Module moneyModule() {
    return new MoneyModule().withMoney().withDecimalNumbers();
  }

  // To fix Swagger issue
  @Bean
  MonetaryAmountConverter monetaryAmountConverter() {
    return new MonetaryAmountConverter();
  }

  class MonetaryAmountConverter implements ModelConverter {

    @Override
    public Schema<?> resolve(
        final AnnotatedType type,
        final ModelConverterContext context,
        final Iterator<ModelConverter> chain) {
      if (type.isSchemaProperty()) {
        final JavaType _type = Json.mapper().constructType(type.getType());
        if (_type != null) {
          final Class<?> cls = _type.getRawClass();
          if (MonetaryAmount.class.isAssignableFrom(cls)) {
            final Schema<?> moneySchema = new ObjectSchema();
            moneySchema.setProperties(
                Map.of("amount", new NumberSchema(), "currency", new StringSchema()));
            return moneySchema;
          }
        }
      }
      return (chain.hasNext()) ? chain.next().resolve(type, context, chain) : null;
    }
  }
}
