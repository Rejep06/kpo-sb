package moscowzoo.domain.entities;

/**
 * Abstract class PredatorAnimal.
 */
public abstract class PredatorAnimal extends Animal {
    public PredatorAnimal(int number, String name, double food) {
        super(number, name, food);
    }

    @Override
    public boolean canBeInContactZoo() {
        return false;
    }
}
