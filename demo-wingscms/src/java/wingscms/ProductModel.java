package wingscms;

public class ProductModel
    extends ObjectTableModel<Product>
{
    public ProductModel() {
        getProperties().add("name");
        getProperties().add("price");
    }
}
