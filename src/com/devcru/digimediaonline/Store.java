package com.devcru.digimediaonline;

import java.text.NumberFormat;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * NOTE:
 * This is a proof-of-concept to understand the logic and workflow.
 * Polishing and code-separation will be done post completion.
 * 
 * CDs have a 50% discount
 * DVDs have a 25% discount -- if 5 or more, then 30%
 * Blurays have a discount of 15%
 * 
 * TODO:
 * Add default media library to choose from // DONE
 * Set up ArrayList(s) for userCart // DONE
 * Create discount calculation methods // DONE
 * Set up type tracker  // DONE
 * Set up quantity tracker for DVDs // DONE
 * Set up control-flow for user input // DONE
 * 
 * Bells and whistles // DONE
 * Perfect/refine (error-check) control-flow // DONE
 * 
 * POST COMPLETION:
 * Refactor, refactor, refactor // Ongoing...
 * Set all String variables from a master source (key-value pair?) // Discarded
 * Separate non-class elements into their own class and encapsulate (getters/setters): Store, Cart // DONE
 * Re-evaluate data structures to determine if there are better options	(List<Map<K, V>>?)
 * Allow user to delete items from cart.. not part of the requirements, but would be nice. // DONE
 * Implement unique-item/cart identifiers since there is a design bug for multiple same-items being deleted
 * Users have no way of knowing what type is what, yet -- convert itemType ints to human-friendly String values // DONE
 * 
 * RETROSPECTIVE SO FAR:
 * Using doubles to represent money was a mistake.  Use BigDecimal instead?
 * Finagling with data types has made this cumbersome.
 * Everything points to either using consistent (all Strings or Objects) types, or using <K, V> pairs.
 */

public class Store {
	// So money looks like money
	static NumberFormat formatter = NumberFormat.getCurrencyInstance();
	
	static Scanner sc = new Scanner(System.in);
	
	public static void main(String[] args) {
	
		seeMediaLib();
		
		chooseAction();
		
		applyDiscounts();
		
		System.out.println(printTotals());

		sc.close(); // Gotta remember to close our resources
	}

	public static void seeMediaLib() {
		MediaLibrary mL = new MediaLibrary();
		String welcome = "", yesNo = "";
		
		System.out.println("Welcome to DigiMedia Online!\n"
				+ "CDs are 50% off, DVDs are 25% off (buy 5 or more and get 30% off!), and Blurays are 15% off!\n\n"
				+ "Would you like to see our selection of media? y/n");
		
		do {
			yesNo = sc.nextLine();
			if (yesNo.equals("y")) {
				System.out.println("SKU | Title | Price | Type");
				mL.prettifyMediaLib();
				yesNo = "made";
				
			} else if (yesNo.equals("n")) {
				welcome = "If you have any questions, please contact us at support@digimedia.com\n";
				yesNo = "made";
			} else {
				System.out.println("Please type either 'y' or 'n' as a response.\n");
			}
		} while (yesNo != "made");

		System.out.println(welcome);
	}

	public static void chooseAction() {
		// This method has kind of become a mess.  Analyze for refactoring later...
		// Validate numeric-only input for SKU using Regex's Pattern and Match.
		Pattern p = Pattern.compile("[^0-9]");
		
		// Running total of items in userCart
		int itemCount = 0;
		
		String decision = "";
		
		System.out.println("\nYou may shop for and add an item, or delete an item from your cart, or checkout.\n"
				+ "Type 'shop', 'remove', or 'checkout' for either action:");
		
		do {
			decision = sc.nextLine();
			if (decision.equals("shop")) {
				System.out.println("Enter the 5-digit SKU of the item you wish to add to your cart.");
				
				// Here, we validate the input to ensure only numbers are entered
				String inputSKUString = sc.next(); // Accept input as string
				Matcher m = p.matcher(inputSKUString); // See if regex pattern is found
				
				if (m.find()) { // if so, reject and start over
					System.out.println("Non-numbers are not allowed!");
				} else { // otherwise, continue
					
					int inputSKU = Integer.parseInt(inputSKUString);
					
					if (Cart.matchAndAddToCart(inputSKU)) {
						System.out.println("Item added!");
						System.out.println("Your cart so far.. ");
						Cart.prettifyUserCart();
						System.out.println();
						itemCount++;
						System.out.println("____________________________");
						System.out.println("Items in cart: " + itemCount);
						System.out.println("SUBTOTAL so far: " + formatter.format(Cart.discountTotal(false)));
					} else {
						System.out.println("SKU not found.  SKUs must be 5-digits in length and consist of numbers only!");
					}
				}

			} else if (decision.equals("remove")) {
				System.out.println("Enter the 5-digit SKU of the item you wish to remove from your cart.");
				String inputSKUString = sc.next();
				Matcher m = p.matcher(inputSKUString);
				if (m.find()) {
					System.out.println("Non-numbers are not allowed!");
				} else {
					
					int inputSKU = Integer.parseInt(inputSKUString);
					
					if (Cart.matchAndDeleteFromCart(inputSKU)) {
						System.out.println("Item removed!");
						System.out.println("Your cart so far.. ");
						Cart.prettifyUserCart();
						System.out.println();
						itemCount--;
						System.out.println("____________________________");
						System.out.println("Items in cart: " + itemCount);
						System.out.println("SUBTOTAL so far: " + formatter.format(Cart.discountTotal(false)));
					} else {
						System.out.println("SKU not found in your cart!  SKUs must be 5-digits in length and consist of numbers only!");
					}
				}
			} else if (decision.equals("checkout")) {
				System.out.println("Checking out and applying discounts..");
				decision = "checkout";
			} else {
				System.out.println("\nWhat would you like to do next?  Type 'shop', 'remove', or 'checkout'!");
			}
		}
		while (decision != "checkout");
	}
	
	public static double calcDiscount(Object price, Object itemType, int quantity) {
		// Made this scalable to allow boosted-discount, based on quantity, for all itemTypes.
		double discountPercentage = 0.0;
		
		double cdDiscount = 0.5;
		double cdDiscountBoost = 0.0;
		
		double dvdDiscount = 0.25;
		double dvdDiscountBoost = 0.3;
		
		double blurayDiscount = 0.15;
		double blurayDiscountBoost = 0.0;
		
		// -99999 as a Not Applicable value until it's time to use real values
		int minCDQuantity = -99999;
		int minDVDQuantity = 5;
		int minBlurayQuantity = -99999;
		
		double discount = Double.parseDouble(price.toString());
		
		if (itemType.equals(1)) { // CDs
			if (minCDQuantity == -99999 || quantity < minCDQuantity) {
				discountPercentage = cdDiscount;
			} else {
				discountPercentage = cdDiscountBoost;
			}
		} else if (itemType.equals(2)) { // DVDs
			if (minDVDQuantity == -99999 || quantity < minDVDQuantity) {
				discountPercentage = dvdDiscount;
			} else {
				discountPercentage = dvdDiscountBoost;
			}
		} else if (itemType.equals(3)) { // Blurays
			if (minBlurayQuantity == -99999 || quantity < minBlurayQuantity) {
				discountPercentage = blurayDiscount;
			} else {
				discountPercentage = blurayDiscountBoost;
			}
		}
		
		discount = discount - (discount * discountPercentage);
		
		return discount;
	}
	
	public static void applyDiscounts() {
		int cdCount = 0;
		int dvdCount = 0;
		int blurayCount = 0;
		
		/* Looks for the itemType
		 * NOTE: we need a loop to keep count, and a loop to add item to userCart.
		 * If we tried to do this in one loop, it would keep sending the quantity into calcDVD() and unintentionally
		 * apply a discount to the item being added under 5 until it finally hits 5, which is not what we want.
		 * There is most likely a way to do this in one loop, but I'm not that smart, yet.
		 */	
		for (int i = 3; i < Cart.getUserCart().size(); i += 4 ) {
			
			Object type = Cart.getUserCart().get(i);
			
			if (type.equals(1)) {
				cdCount++;
			}
			if (type.equals(2)) {
				dvdCount++;
			}
			if (type.equals(3)) {
				blurayCount++;
			}
		}
		
		for (int i = 3; i < Cart.getUserCart().size(); i += 4 ) {
			
			Object name = Cart.getUserCart().get(i - 2);
			Object price = Cart.getUserCart().get(i - 1);
			Object type = Cart.getUserCart().get(i);
			
			String meta = "Item: "	+ name	+ " | Price before discount: " + formatter.format(price);
			
			if (type.equals(1)) {
				Object cdResult = calcDiscount(price, type, cdCount);
				String cdMeta = " | Price after discount: " + formatter.format(cdResult);
				System.out.println(meta + cdMeta);
				Cart.getUserCartTotal().add(cdResult);
			}
			if (type.equals(2)) {
				Object dvdResult = calcDiscount(price, type, dvdCount);
				String dvdMeta = " | Price after discount: " + formatter.format(dvdResult);
				System.out.println(meta + dvdMeta);
				Cart.getUserCartTotal().add(dvdResult);
			}
			if (type.equals(3)) {
				Object blurayResult = calcDiscount(price, type, blurayCount);
				String blurayMeta = " | Price after discount: " + formatter.format(blurayResult);
				System.out.println(meta + blurayMeta);
				Cart.getUserCartTotal().add(blurayResult);
			}
		}
	}
	
	public static StringBuilder printTotals() {
		double subtotal = Cart.discountTotal(false);
		double total = Cart.discountTotal(true);
		double savings = subtotal - total;
		
		String subtotalFormatted = formatter.format(subtotal);
		String totalFormatted = formatter.format(total);
		String savingsFormatted = formatter.format(savings);
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("____________________________\nSUBTOTAL (before discounts): " + subtotalFormatted);
		sb.append("\nTOTAL (after discounts): " + totalFormatted);
		sb.append("\nToday's savings: " + savingsFormatted + "!");
		
		return sb;
	}
	
}