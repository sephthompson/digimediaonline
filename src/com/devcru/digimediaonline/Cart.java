package com.devcru.digimediaonline;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Cart {
	
	static MediaLibrary mL = new MediaLibrary();
	
	private static List<Object> userCart = new ArrayList<Object>();
	private static List<Object> userCartSubtotal = new ArrayList<Object>();
	private static List<Object> userCartTotal = new ArrayList<Object>();
	
	public static List<Object> getUserCart() {
		return userCart;
	}
	public void setUserCart(List<Object> userCart) {
		Cart.userCart = userCart;
	}
	public List<Object> getUserCartSubtotal() {
		return userCartSubtotal;
	}
	public void setUserCartSubtotal(List<Object> userCartSubtotal) {
		Cart.userCartSubtotal = userCartSubtotal;
	}
	public static List<Object> getUserCartTotal() {
		return userCartTotal;
	}
	public void setUserCartTotal(List<Object> userCartTotal) {
		Cart.userCartTotal = userCartTotal;
	}
	
	public static boolean matchAndAddToCart(int sku) {
		// Checks to see if the SKU exists in the mediaLibrary
		// We set return type to boolean to check if true/false in main workflow control
		boolean eval = false;
		
		int digitsInSKU = String.valueOf(sku).length();
		
		for (int i = 0; i < mL.getMediaLibrary().length; i++) {
			if (mL.getMediaLibrary()[i].equals(sku) && digitsInSKU == 5) {
				
				// If match is found, add item to userCart
				userCart.add(mL.getMediaLibrary()[i]);
				userCart.add(mL.getMediaLibrary()[i + 1]);
				userCart.add(mL.getMediaLibrary()[i + 2]);
				userCart.add(mL.getMediaLibrary()[i + 3]);
				
				// Add to user cart's total cost.
				userCartSubtotal.add(mL.getMediaLibrary()[i + 2]);
				
				eval = true;
			}
		}
		return eval;
	}
	
	public static boolean matchAndDeleteFromCart(int sku) {
		boolean eval = false;
		
		int digitsInSKU = String.valueOf(sku).length();
		
		for (int i = 0; i < userCart.size(); i++) {
			if (userCart.get(i).equals(sku) && digitsInSKU == 5) {
				
				for (int j = 0; j < userCartSubtotal.size(); j++) {
					if (userCart.get(i + 2).equals(userCartSubtotal.get(j))) {
						userCartSubtotal.remove(j);
					}
				}
				
				userCart.remove(i + 3);
				userCart.remove(i + 2);
				userCart.remove(i + 1);
				userCart.remove(i);
				
				eval = true;
			}
		}
		return eval;
	}
	
	public static double discountTotal(boolean isDiscounted) {
		// Consolidated subtotal and total into one method for code simplicity
		double sum = 0.0;
		
		int cartSize = 0;
		List<Object> cartTotal = null;
		
		if (isDiscounted) {
			cartSize = userCartTotal.size();
			cartTotal = userCartTotal;
		} else {
			cartSize = userCartSubtotal.size();
			cartTotal = userCartSubtotal;
		}
		for (int i = 0; i < cartSize; i++) {
			sum += Double.parseDouble(cartTotal.get(i).toString());
		}
		return sum;
	}
	
	public static void prettifyUserCart() {
		// Prettifies the user's cart with rows and columns for human-readability
		
		for (int i = 0; i < userCart.size(); i++) {
			if (i != 0 && i % 4 == 0) {
				System.out.print("\n");
			}
			if (i % 4 != 0) {
				System.out.print(" | ");
			}
			
			// Converts itemType numerals to itemType description
			if ((i + 1) != 0 && (i + 1) % 4 == 0) {
				if (userCart.get(i).equals(1)) {
					System.out.print("CD");
				} else if (userCart.get(i).equals(2)) {
					System.out.print("DVD");
				} else if (userCart.get(i).equals(3)) {
					System.out.print("Bluray");
				}
			} else {
				System.out.print(userCart.get(i));
			}
		}
	}
	
	public static void searchUserCart(String searchMeta) {
		Scanner sc = new Scanner(System.in);
		for (int i = 0; i < mL.getMediaLibrary().length; i++) {
			if (searchMeta.equals(i)) {
				System.out.println("Found something..");
			}
		}
		sc.close();
	}
	
}