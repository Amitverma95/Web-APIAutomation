package pojo;

import java.math.BigDecimal;
import java.util.Objects;

public class Product {

    private final String name;
    private final BigDecimal price;
    private final int quantity;

    public Product(String name, BigDecimal price) {
        this(name, price, 1);
    }

    public Product(String name, BigDecimal price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product other = (Product) o;
        return quantity == other.quantity && Objects.equals(name, other.name)
                && price != null && other.price != null && price.compareTo(other.price) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, quantity);
    }

    @Override
    public String toString() {
        return name + " ($" + price + " x " + quantity + ")";
    }
}
