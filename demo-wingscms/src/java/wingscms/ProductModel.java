package wingscms;

public class ProductModel
    extends ObjectTableModel<Product>
{
    public ProductModel() {
    	getProperties().add("image");
        getProperties().add("name");
        getProperties().add("price");
    }
}
