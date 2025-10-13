package moscowzoo.domain.entities;

import lombok.Setter;
import moscowzoo.domain.interfaces.Inventory;

/**
 * Abstract class Thing.
 */
public abstract class Thing implements Inventory {
    @Setter
    private String name;

    private int number;

    public Thing(int number, String name) {
        this.name = name;
        this.number = number;
    }

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public String getName() {
        return name;
    }
}
