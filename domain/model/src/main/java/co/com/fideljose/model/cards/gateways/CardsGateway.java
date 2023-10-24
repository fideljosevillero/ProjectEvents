package co.com.fideljose.model.cards.gateways;

import co.com.fideljose.model.cards.Card;
import reactor.core.publisher.Mono;

public interface CardsGateway {

    public Mono<Card> adapterTest();
}
