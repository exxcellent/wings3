package wingscms;

import java.awt.Image;
import java.math.BigDecimal;

/**
 * Class represents a single Product
 * 
 * @author Christian Fetzer
 */
public class Product {

    private Integer id;
    private Image image;
    private String name;
    private String description;
    private BigDecimal price;

    public Product() {
    }

    public Product(Integer id, Image image, String name, String description, BigDecimal price) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(name).append(", ").append(description).append(" (").append(price).append(")");
		
		return sb.toString();
	}
}
