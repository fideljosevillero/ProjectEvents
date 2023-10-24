package co.com.fideljose.model.cards;
import lombok.*;
//import lombok.NoArgsConstructor;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Card {
    private int id;
    private String first_name;
    private int height_feet;
}
