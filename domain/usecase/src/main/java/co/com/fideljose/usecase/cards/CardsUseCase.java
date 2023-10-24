package co.com.fideljose.usecase.cards;

import co.com.fideljose.model.cards.Card;
import co.com.fideljose.model.cards.gateways.CardsGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
public class CardsUseCase {

    final CardsGateway cardsGateway;

    public Mono<Card> testUseCase(){
        return cardsGateway.adapterTest();
    }
}
