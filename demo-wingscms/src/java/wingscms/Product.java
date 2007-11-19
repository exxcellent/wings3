package wingscms;

/**
 * Class represents a single Product
 * 
 * @author Christian Fetzer
 */
public class Product {

    final private Integer itemnumber;
    final private String description;
    private double price; 
    
    public Integer getItemnumber() {
        return itemnumber;
    }
    
    public String getDescription() {
        return description;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public Product(Integer itemnumber, String description, double initialprice) {
        this.itemnumber = itemnumber;
        this.description = description;
        setPrice(initialprice);
    }
}
