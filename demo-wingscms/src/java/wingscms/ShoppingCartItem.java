package wingscms;

/**
 * ShoppingCartItem
 * 
 * @author Christian Fetzer
 */
public class ShoppingCartItem {

    final private Product product;
    private int amount = 1;
    
    public Product getProduct() { return this.product; }
    public int getAmount() { return this.amount; }
    public void setAmount(int amount) { this.amount = amount; }
    
    public ShoppingCartItem(Product product) { 
        this.product = product;
    }
    
    public double getAllRoundPrice() {
        return amount * getProduct().getPrice();
    }
}
