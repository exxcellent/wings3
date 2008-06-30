package wingscms;

import java.math.BigDecimal;

public class CartModel
    extends ObjectTableModel<CartItem>
{
    public CartModel() {
        getProperties().add("product");
        getProperties().add("amount");
    }

    public void addProduct(Product product, int amount) {
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setAmount(amount);

        int index = getRows().size();
        getRows().add(cartItem);
        fireTableRowsInserted(index, index);
    }

    public BigDecimal getTotal() {
        BigDecimal total = new BigDecimal("0.00");
        for (CartItem cartItem : rows) {
            total = total.add(cartItem.getProduct().getPrice().multiply(new BigDecimal(cartItem.getAmount())));
        }
        return total;
    }
}
