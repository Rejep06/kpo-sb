package moscowzoo.domain.entities;

import moscowzoo.domain.interfaces.Herbivore;

/**
 * Abstract class HerbivoreAnimal.
 */
public abstract class HerbivoreAnimal extends Animal implements Herbivore {

    private int kindnessLevel;

    public HerbivoreAnimal(int number, String name, double food, int kindnessLevel) {
        super(number, name, food);
        this.kindnessLevel = kindnessLevel;
    }

    public int getKindnessLevel() {
        return kindnessLevel;
    }

    @Override
    public boolean canBeInContactZoo() {
        return kindnessLevel > 5;
    }
}
