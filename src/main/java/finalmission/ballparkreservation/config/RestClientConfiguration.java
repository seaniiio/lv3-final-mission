package finalmission.ballparkreservation.config;

import finalmission.ballparkreservation.external.HolidayClient;
import finalmission.ballparkreservation.external.WeatherClient;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfiguration {

    @Bean
    public HolidayClient holidayClient(RestClient.Builder builder) {
        return new HolidayClient(builder.build());
    }

    @Bean
    public WeatherClient weatherClient(RestClient.Builder builder) {
        return new WeatherClient(builder.build());
    }

    @Bean
    public RestClientCustomizer restClientCustomizer() {
        return builder -> builder
                .requestFactory(getSimpleClientHttpRequestFactory());
    }

    private SimpleClientHttpRequestFactory getSimpleClientHttpRequestFactory() {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);
        return factory;
    }
}
