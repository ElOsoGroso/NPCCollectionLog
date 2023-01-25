package com.collectionlogdisplay;


import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class WikiItem {

	private String imageUrl;
	private String name;
	private int id;
	private int quantity;
	private String quantityStr;
	private String rarityStr;
	private double rarity;
	private int exchangePrice;
	private int alchemyPrice;

	NumberFormat nf = NumberFormat.getNumberInstance();

	public WikiItem(String name, int id) {
		this.name = name;
		this.id = id;
	}
	public int getId(){
		return id;
	}
	public String getName() {
		return name;
	}

	public int getQuantity() {
		return quantity;
	}

	public String getQuantityStr() {
		return quantityStr;
	}

	public double getRarity() {
		return rarity;
	}

	public String getRarityStr() {
		return rarityStr;
	}
	public int getExchangePrice() {
		return exchangePrice;
	}
	public int getAlchemyPrice() {
		return alchemyPrice;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public String getQuantityLabelText() {
		if (quantityStr.contains("-") || quantityStr.endsWith(" (noted)")) {
			return "x" + quantityStr;
		}
		return quantity > 0 ? "x" + nf.format(quantity) : quantityStr;
	}


	public String getExchangePriceLabelText() {
		String priceLabelStr = exchangePrice > 0 ? nf.format(exchangePrice) + "gp" : "Not sold";
		if (name.equals("Nothing")) {
			priceLabelStr = "";
		}
		return priceLabelStr;
	}



	public String getAlchemyPriceLabelText() {
		String priceLabelStr = nf.format(alchemyPrice) + "gp";
		if (name.equals("Nothing")) {
			priceLabelStr = "";
		}
		return priceLabelStr;
	}

	public String getAlchemyPriceLabelTextShort() {
		String priceLabelStr = alchemyPrice > 0 ? nf.format(alchemyPrice) + "gp" : "";
		if (name.equals("Nothing")) {
			priceLabelStr = "";
		}
		return priceLabelStr;
	}
}