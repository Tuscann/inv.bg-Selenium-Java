package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Item {
	public String name;
	public String quantity_unit;
	public String catalog_number;
	public Double price_for_quantity;
	public Double price;
	public String currency;
	//public Number id;

	public Item(String name, String quantity_unit, Double price_for_quantity, Double price, String catalog_number, String currency) {
		this.name = name;
		this.price = price;
		this.catalog_number = catalog_number;
		this.quantity_unit = quantity_unit;
		this.price_for_quantity = price_for_quantity;
		this.currency = currency;
	}

	public static void main(String[] args) {
		Item coffee = new Item("Lavazza", "kg.", 20.22, 20.22, "dasdasdds", "BGN");
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		System.out.println(gson.toJson(coffee));
		String jsonCoffee = gson.toJson(coffee);
	}
}
