package cs445.sorting;

import java.lang.Math;
import java.util.Scanner;

public class IPS6 {

  public static void welcomeUser() {
    System.out.println("Welcome to the store!");
  }

  public static String[] getProducts() {
    String[] ret = {"Butter", "Milk", "Eggs", "Bacon", "Cheese"};
    return ret;
  }

  public static boolean getOrder(String[] a) {
    Scanner in = new Scanner(System.in);

    System.out.println("Please enter a product name:");
    String product = in.nextLine();

    for (int i = 0; i < a.length; i++) {
      if (a[i].equalsIgnoreCase(product)) {
        return true;
      }
    }
    return false;
  }

  public static double getPrice() {
    return Math.random() * 100;
  }

  public static double getTax(double price) {
    return price * 0.1;
  }

  public static double getTotal(double price, double tax) {
    return price + tax;
  }

  public static void printTotal(double saleTotal) {
    System.out.println("Your sale total is: $" + String.format( "%.2f",           saleTotal ));
  } 


  public static void main(String[] args) {
    welcomeUser();
    String[] productArray = getProducts();
    boolean exists = getOrder(productArray);
    if (exists == false) {
      System.out.println("Product not found");
    } else {
      double price = getPrice();
      double tax = getTax(price);
      double total = getTotal(price, tax);
      printTotal(total);
    }
  }
}


