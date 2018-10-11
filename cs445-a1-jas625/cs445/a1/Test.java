package cs445.a1;

public class Test {

    public static void main(String[] args) {
        GroceriesInterface myGroceries = new Groceries();

        myGroceries.addItem(new GroceryItem("Butter", 2));
        myGroceries.addItem(new GroceryItem("Milk", 1));
        myGroceries.addItem(new GroceryItem("Eggs", 12));
        myGroceries.addItem(new GroceryItem("Brownies", 5));
        myGroceries.addItem(new GroceryItem("Doritos", 100));

        myGroceries.printAll();

        myGroceries.addItem(new GroceryItem("Butter", 8));
        myGroceries.removeItem(new GroceryItem("Doritos", 20));
        myGroceries.removeItem(new GroceryItem("Eggs", 18));
        myGroceries.modifyQuantity(new GroceryItem("Milk", 5));

        System.out.println("------------------");
        myGroceries.printAll();

        int previousQty = myGroceries.modifyQuantity(new GroceryItem("Butter",
                6));

        System.out.println("------------------");
        myGroceries.printAll();

        System.out.println("The previous quantity of Butter was: "
                + previousQty + ". The new quantity is 6");


        SetInterface<String> setTest = new Set<>();

        System.out.println("Created a new set: size:  " + setTest.getSize() + ". Is empty: " +setTest.isEmpty());

        try {
            setTest.add("Hello");
            setTest.add("World!");
            System.out.println("New size " + setTest.getSize() + ". Is empty:" +
                    " " + setTest.isEmpty());
            String one = setTest.remove();
            String two = setTest.remove("Hello");
            System.out.println("One: " + one + ". Two: " + two);
            setTest.add(two);
            setTest.add(one);
            one = setTest.remove("The WOrld!");
            System.out.println("One: " + one + ". Two: " + two);
            System.out.println("Set contains cats?: " + setTest.contains("Cat"));
            System.out.println("Set contains Hello?: " + setTest.contains(
                    "Hello"));
            System.out.println("Size before clear: " + setTest.getSize());
            setTest.clear();
            System.out.println("Size after clear: " + setTest.getSize());

            SetInterface<String> setTwo = new Set<>(new String[] {"hello",
                    "cat", "my friend"});
            System.out.println("set two size: " + setTwo.getSize());

            setTest = new Set<>(3);
            System.out.println("set new size: " + setTest.getSize());

        } catch (SetFullException e) {
            e.printStackTrace();
        }
    }
}
