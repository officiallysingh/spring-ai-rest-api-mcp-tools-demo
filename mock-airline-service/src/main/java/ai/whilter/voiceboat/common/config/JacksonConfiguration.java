package ai.whilter.voiceboat.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.jackson.datatype.money.MoneyModule;

@Configuration
public class JacksonConfiguration {

  @Bean
  public MoneyModule moneyModule() {
    return new MoneyModule();
  }
}
