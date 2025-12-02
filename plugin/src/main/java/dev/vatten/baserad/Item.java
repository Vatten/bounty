package dev.vatten.baserad;

import de.exlll.configlib.Configuration;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Configuration
@NoArgsConstructor
public class Item {
    @Getter
    private String material;
    @Getter
    private String nbt;
    @Getter
    private int amount;

    public Item(String material, String nbt, int amount) {
        this.material = material;
        this.nbt = nbt;
        this.amount = amount;
    }

    public Item(String material, String nbt) {
        this(material, nbt, 1);
    }
}
