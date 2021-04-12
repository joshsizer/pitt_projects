package cs445.a1;

public class Groceries implements GroceriesInterface {

    private Set<GroceryItem> groceryList;

    public Groceries() {
        groceryList = new Set<>();
    }

    @Override
    public void addItem(GroceryItem item) {
        if (item == null) return;

        GroceryItem updated = item;

        if (groceryList.contains(item)) {
            updated = groceryList.remove(item);
            updated.setQuantity(updated.getQuantity() + item.getQuantity());
        }

        // our Set will never throw a SetFullException, so we don't really
        // have to worry about this
        try {
            groceryList.add(updated);
        } catch (SetFullException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeItem(GroceryItem item) {
        if (!groceryList.contains(item)) return;

        GroceryItem updated = groceryList.remove(item);
        int newQty = updated.getQuantity() - item.getQuantity();
        if (newQty > 0) {
            updated.setQuantity(newQty);
            try {
                groceryList.add(updated);
            } catch (SetFullException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int modifyQuantity(GroceryItem item) {
        if (item == null) throw new NullPointerException();

        int oldQty = -1;

        if (groceryList.contains(item)) {
            oldQty = groceryList.remove(item).getQuantity();
            try {
                groceryList.add(item);
            } catch (SetFullException e) {
                e.printStackTrace();
            }
        }

        return oldQty;
    }

    @Override
    public void printAll() {
        String output = "Groceries:\n";

        Object[] groceries = groceryList.toArray();

        for (int i = 0; i< groceries.length; i++) {
            output += groceries[i].toString() + "\n";
        }

        System.out.print(output);
    }
}
