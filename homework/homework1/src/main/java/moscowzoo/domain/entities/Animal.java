package moscowzoo.domain.entities;

import lombok.Setter;
import moscowzoo.domain.interfaces.Alive;
import moscowzoo.domain.interfaces.Inventory;

/**
 * Animal - abstract class.
 */
public abstract class Animal implements Alive, Inventory {
    @Setter
    private double food;

    @Setter
    private String name;

    private int number;

    /**
     * Animal constructor.
     *
     * @param number inventory number
     * @param name name of animal
     * @param food daily food
     */
    public Animal(int number, String name, double food) {
        this.number = number;
        this.name = name;
        this.food = food;
    }

    @Override
    public double getDailyFood() {
        return this.food;
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
        return this.name;
    }

    public abstract boolean canBeInContactZoo();
}
