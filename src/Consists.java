

public class Consists extends Relation {
	
	private static final String ORDER_NO = "order_no";
	private static final String UPC = "upc";
	private static final String PRICE = "price";
	private static final String QUANTITY = "quantity";
	
//	HashMap<String, Object> attributes = new HashMap<String, Object>();
	
	public Consists(Product product, Double price) {
		this(null, product.getUpc(), price, 1);
	}
	
	public Consists(Integer orderNo, String upc, Double price, Integer quantity) {
		if(orderNo != null) {
			attributes.put(ORDER_NO, orderNo);
		}
		if(upc != null && (upc = upc.trim()).length() > 0) {
			attributes.put(UPC, upc);
		}
		if(price != null) {
			attributes.put(PRICE, price);
		}
		if(quantity != null) {
			attributes.put(QUANTITY, quantity);
		} else {
			attributes.put(QUANTITY, 0);
		}
	}
	
	public Integer getOrderNo() {
		return (Integer)attributes.get(ORDER_NO);
	}
	
	public String getUpc() {
		return (String)attributes.get(UPC);
	}
	
	public Double getPrice() {
		return (Double)attributes.get(PRICE);
	}
	
	public Integer getQuantity() {
		return (Integer)attributes.get(QUANTITY);
	}
	
	public void incrementQuantity() {
		attributes.put(QUANTITY, (Integer)attributes.get(QUANTITY) + 1);
	}
	
	public void decrementQuantity() {
		int quantity = (Integer)attributes.get(QUANTITY);
		if(quantity > 0) {
			attributes.put(QUANTITY, quantity - 1);
		}
	}
	
	@Override
	public String toString() {
		return getUpc() + " " + getPriceString() + " " + getQuantity();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Consists) {
			Consists c = (Consists)o;
			if(this.getOrderNo() != null && c.getOrderNo() != null && !this.getOrderNo().equals(c.getOrderNo())) {
				return false;
			}
			return (this.getUpc() != null && this.getUpc().equals(c.getUpc()));
		}
		return false;
	}

	private String getPriceString() {
		if(getPrice() != null) {
			return "$" + getPrice();
		}
		return null;
	}
}
