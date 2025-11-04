package moscowzoo.ui;

import java.util.List;
import java.util.Scanner;
import moscowzoo.domain.entities.Animal;
import moscowzoo.domain.enums.InventoryType;
import moscowzoo.domain.interfaces.Inventory;
import moscowzoo.service.implementations.CreationRequest;
import moscowzoo.service.implementations.InventoryFactory;
import moscowzoo.service.interfaces.ZooService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Console for users.
 */
@Component
public class Console implements CommandLineRunner {
    private final ZooService zooService;
    private final InventoryFactory inventoryFactory;
    private final Scanner scanner;

    /**
     * Console constructor.
     *
     * @param zooService       MoscowZooService.
     * @param inventoryFactory inventory factory for creating inventory.
     */
    @Autowired
    public Console(ZooService zooService, InventoryFactory inventoryFactory) {
        this.zooService = zooService;
        this.inventoryFactory = inventoryFactory;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run(String... args) {
        System.out.println("=== Welcome to the Moscow Zoo system! ===");
        showMainMenu();
    }

    private void showMainMenu() {
        boolean running = true;

        while (running) {
            printMainMenu();
            int choice = getIntInput("Select action: ");

            switch (choice) {
                case 1 -> showAddMenu();
                case 2 -> showTotalFood();
                case 3 -> showContactZooAnimals();
                case 4 -> showInventoryReport();
                case 5 -> showAllAnimals();
                case 6 -> showAllItems();
                case 0 -> {
                    running = false;
                    System.out.println("Good bye!");
                }
                default -> System.out.println("Invalid input. Please try again.");
            }
        }
        scanner.close();
    }

    private void printMainMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("   Moscow zoopark  ");
        System.out.println("=".repeat(50));
        System.out.println("1. Add an object to inventory");
        System.out.println("2. Show total food consumption");
        System.out.println("3. Show animals for a petting zoo");
        System.out.println("4. Generate an inventory report");
        System.out.println("5. Show all animals");
        System.out.println("6. Show all inventory items");
        System.out.println("0. Exit");
        System.out.println("-".repeat(50));
    }

    private void showAddMenu() {
        System.out.println("\n=== Add object ===");
        System.out.println("1. Add animal");
        System.out.println("2. Add thing");
        System.out.println("0. Cancel");

        int choice = getIntInput("Choose type of object: ");

        switch (choice) {
            case 1 -> addAnimal();
            case 2 -> addThing();
            case 0 -> {
                return;
            }
            default -> System.out.println("Invalid input.");
        }
    }

    private void addAnimal() {
        System.out.println("\n=== Add animal ===");

        InventoryType animalType = selectAnimalType();
        if (animalType == null) {
            return;
        }

        System.out.print("Enter the animal's name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("The name cannot be empty.");
            return;
        }

        Double food = getDoubleInput("Enter your daily food intake (kg): ");
        if (food == null || food <= 0) {
            System.out.println("Food consumption must be a positive number.");
            return;
        }

        Integer kindness = null;
        if (animalType == InventoryType.RABBIT || animalType == InventoryType.MONKEY) {
            kindness = getIntInput("Enter kindness level (1-10): ");
            if (kindness == null || kindness < 1 || kindness > 10) {
                System.out.println("The kindness level should be between 1 and 10.");
                return;
            }
        }

        try {
            CreationRequest request = new CreationRequest(animalType, name, food, kindness);

            boolean accepted = zooService.addAnimalFromRequest(request);
            if (accepted) {
                System.out.println("The animal has been successfully added to the zoo!");
            } else {
                System.out.println("The animal did not pass the medical examination and was not added.");
            }
        } catch (Exception e) {
            System.out.println("Error adding animal: " + e.getMessage());
        }
    }

    private void addThing() {
        System.out.println("\n=== Add item ===");

        // Выбор типа вещи
        InventoryType thingType = selectThingType();
        if (thingType == null) {
            return;
        }

        System.out.print("Enter the name of the item: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("The name cannot be empty.");
            return;
        }

        try {
            // Создание запроса (для вещей food и kindness не нужны)
            CreationRequest request = new CreationRequest(thingType, name, null, null);

            // Добавление вещи в инвентарь
            zooService.addThingFromRequest(request);
            System.out.println("The item has been successfully added to your inventory!");
        } catch (Exception e) {
            System.out.println("Error adding item: " + e.getMessage());
        }
    }

    private InventoryType selectAnimalType() {
        System.out.println("Choose type of animal: ");
        System.out.println("1. Rabbit");
        System.out.println("2. Monkey");
        System.out.println("3. Tiger");
        System.out.println("4. Wolf");
        System.out.println("0. Cancel");

        int choice = getIntInput("Input number of type: ");
        return switch (choice) {
            case 1 -> InventoryType.RABBIT;
            case 2 -> InventoryType.MONKEY;
            case 3 -> InventoryType.TIGER;
            case 4 -> InventoryType.WOLF;
            case 0 -> null;
            default -> {
                System.out.println("Incorrect choice of animal type.");
                yield null;
            }
        };
    }

    private InventoryType selectThingType() {
        System.out.println("Choose type of item:");
        System.out.println("1. Table");
        System.out.println("2. Computer");
        System.out.println("0. ↩Cancel");

        int choice = getIntInput("Input number of type: ");
        return switch (choice) {
            case 1 -> InventoryType.TABLE;
            case 2 -> InventoryType.COMPUTER;
            case 0 -> null;
            default -> {
                System.out.println("Incorrect choice of animal type.");
                yield null;
            }
        };
    }

    private void showTotalFood() {
        double totalFood = zooService.calculateTotalFood();
        System.out.printf("\n=== TOTAL FOOD CONSUMPTION ===\n");
        System.out.printf("All animals consume: %.2f kg per day\n", totalFood);
    }

    private void showContactZooAnimals() {
        List<Animal> contactAnimals = zooService.getAnimalsForContactZoo();
        System.out.println("\n=== ANIMALS FOR CONTACT ZOO ===");

        if (contactAnimals.isEmpty()) {
            System.out.println("Currently there are no animals suitable for the contact zoo.");
        } else {
            System.out.println("The following animals can be in the contact zoo:");
            contactAnimals.forEach(animal -> {
                String type = animal.getClass().getSimpleName();
                System.out.printf(" - %s (%s, No.%d)", animal.getName(), type, animal.getNumber());
                if (animal instanceof moscowzoo.domain.entities.HerbivoreAnimal herbivore) {
                    System.out.printf(" - kindness: %d/10", herbivore.getKindnessLevel());
                }
                System.out.println();
            });
        }
    }

    private void showInventoryReport() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("               INVENTORY REPORT");
        System.out.println("=".repeat(60));

        List<Inventory> allItems = zooService.getInventories();
        List<Animal> animals = allItems.stream()
            .filter(item -> item instanceof Animal)
            .map(item -> (Animal) item)
            .toList();

        System.out.println("\nANIMALS:");
        if (animals.isEmpty()) {
            System.out.println("   No animals in the zoo.");
        } else {
            animals.forEach(animal -> {
                String contactInfo = animal.canBeInContactZoo() ? " [Contact]" : "";
                System.out.printf("   %s (%s, No.%d) - %.2f kg/day%s\n",
                    animal.getName(),
                    animal.getClass().getSimpleName(),
                    animal.getNumber(),
                    animal.getDailyFood(),
                    contactInfo);
            });
        }

        List<Inventory> things = allItems.stream()
            .filter(item -> !(item instanceof Animal))
            .toList();

        System.out.println("\nTHINGS:");
        if (things.isEmpty()) {
            System.out.println("   No items in inventory.");
        } else {
            things.forEach(thing ->
                System.out.printf("   %s (%s, No.%d)\n",
                    thing.getName(),
                    thing.getClass().getSimpleName(),
                    thing.getNumber()));
        }

        System.out.println("\n" + "-".repeat(60));
        System.out.printf("GENERAL STATISTICS:\n");
        System.out.printf("   Total animals: %d\n", animals.size());
        System.out.printf("   Total things: %d\n", things.size());
        System.out.printf("   Total objects: %d\n", allItems.size());
        System.out.printf("   Current inventory counter: %d\n", inventoryFactory.getCurrentInventoryNumber());
        System.out.println("=".repeat(60));
    }

    private void showAllAnimals() {
        List<Inventory> allItems = zooService.getInventories();
        List<Animal> animals = allItems.stream()
            .filter(item -> item instanceof Animal)
            .map(item -> (Animal) item)
            .toList();

        System.out.println("\n=== ALL ZOO ANIMALS ===");

        if (animals.isEmpty()) {
            System.out.println("There are currently no animals in the zoo.");
        } else {
            animals.forEach(animal -> {
                String type = animal.getClass().getSimpleName();
                String contactStatus = animal.canBeInContactZoo() ? "Contact" : "Non-contact";
                System.out.printf(" - %s (%s, No.%d) - %.2f kg/day - %s\n",
                    animal.getName(), type, animal.getNumber(), animal.getDailyFood(), contactStatus);
            });
        }
    }

    private void showAllItems() {
        List<Inventory> allItems = zooService.getInventories();

        System.out.println("\n=== ALL INVENTORY ITEMS ===");

        if (allItems.isEmpty()) {
            System.out.println("Inventory is empty.");
        } else {
            allItems.forEach(item -> {
                String type = item.getClass().getSimpleName();
                String category = (item instanceof Animal) ? "Animal" : "Thing";
                System.out.printf(" - %s (%s, No.%d) - %s\n",
                    item.getName(), type, item.getNumber(), category);
            });
        }
    }

    // Helper methods for data input
    private int getIntInput(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.print("Input cannot be empty. " + prompt);
                    continue;
                }
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Please enter an integer: ");
            }
        }
    }

    private Double getDoubleInput(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.print("Input cannot be empty. " + prompt);
                    continue;
                }
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.print("Please enter a number: ");
            }
        }
    }
}