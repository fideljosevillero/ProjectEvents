package co.com.fideljose.consumer.config;

import co.com.fideljose.model.cards.Card;
import co.com.fideljose.model.cards.gateways.CardsGateway;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import java.time.Duration;

import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Configuration
public class RestConsumerConfig implements CardsGateway {

    @Value("${adapter.restconsumer.url}")
    private String url;
    @Value("${adapter.restconsumer.timeout}")
    private int timeout;

    private WebClient getWebClient() {
        return WebClient.builder()
                .baseUrl(url)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
            .clientConnector(getClientHttpConnector())
            .build();
    }

    @Bean
    @Primary
    public WebClient getWebClientApiV10() throws SSLException {
        return WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE,MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(getClientHttpConnector())
                .build();
    }

    private ClientHttpConnector getClientHttpConnector() {
        /*
        IF YO REQUIRE APPEND SSL CERTIFICATE SELF SIGNED: this should be in the default cacerts trustore
        */
        return new ReactorClientHttpConnector(HttpClient.create()
                .compress(true)
                .keepAlive(true)
                .option(CONNECT_TIMEOUT_MILLIS, timeout)
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(timeout, MILLISECONDS));
                    connection.addHandlerLast(new WriteTimeoutHandler(timeout, MILLISECONDS));
                }));
    }

    @Override
    public Mono<Card> adapterTest() {
        //return "RESPUESTA DESDE EL ADAPTER!!!";
        return getWebClient()
                .get()
                .uri("/players/1")
                .header("X-RapidAPI-Key", "248d151c9emsh763578d3380a923p11624djsnc47f0017bc50")
                .header("X-RapidAPI-Host", "free-nba.p.rapidapi.com")
                .retrieve()
                .bodyToMono(Card.class)
                //.exchangeToMono(clientResponse -> buildResponse(clientResponse))
                .log()
                .filter(p -> p != null)
                .timeout(Duration.ofMillis(5000));

    }

    private Mono<Card> buildResponse(ClientResponse clientResponse){
        return clientResponse.bodyToMono(Card.class);
    }
}
