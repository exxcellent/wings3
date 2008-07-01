/*
 * Copyright 2000,2005 wingS development team.
 *
 * This file is part of wingS (http://wingsframework.org).
 *
 * wingS is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * Please see COPYING for the complete licence.
 */
package wingscms;

import java.awt.Image;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.wings.CmsFrame;
import org.wings.SButton;
import org.wings.SComponent;
import org.wings.SConstants;
import org.wings.SDimension;
import org.wings.SImageIcon;
import org.wings.SLabel;
import org.wings.SListSelectionModel;
import org.wings.STable;
import org.wings.session.SessionManager;
import org.wings.table.STableCellRenderer;
import org.wingx.XTable;
import org.wingx.XZoomableImage;
import org.wingx.table.XTableClickListener;

/**
 * @author hengels
 * @version $Id
 */
public class ShopExample
{
    private CmsFrame rootFrame = new CmsFrame();
    CartModel cartModel = new CartModel();
    ProductModel productModel = new ProductModel();

    private XTable productTable = new XTable() {

		/* (non-Javadoc)
		 * @see org.wings.STable#getColumnClass(int)
		 */
		@Override
		public Class getColumnClass(int col) {
			if (col == 0) {
				return Image.class;
			}
			
			return super.getColumnClass(col);
		}
    };
    
    private XTable cartTable = new XTable();
    private SLabel productDetails = new SLabel();
    private SButton addButton = new SButton("add");

    public ShopExample() {
    	Image img1 = null;
		Image img2 = null;
		try {
			img1 = ImageIO.read(buildURLContext("teox.jpg"));
			img2 = ImageIO.read(buildURLContext("samsung.jpg"));
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        productModel.getRows().add(new Product(1, img1, "TEO-X Media", "Mediacenter", new BigDecimal("549.00")));
        productModel.getRows().add(new Product(2, img2, "Samsung Syncmaster 223BW", "Widescreen TFT", new BigDecimal("249.00")));
        productTable.setModel(productModel);
        productTable.setSelectionMode(SListSelectionModel.NO_SELECTION);
        productTable.addClickListener(1, new XTableClickListener() {
            public void clickOccured(int row, int column) {
                productDetails.setText(productModel.getRows().get(row).getDescription());
            }
        });
        productTable.addClickListener(2, new XTableClickListener() {
            public void clickOccured(int row, int column) {
                Product product = productModel.getRows().get(row);
                cartModel.addProduct(product, 1);
            }
        });
        
        productTable.setDefaultRenderer(Image.class, new STableCellRenderer() {

        	private XZoomableImage zoomableImage;
        	
			/* (non-Javadoc)
			 * @see org.wings.table.STableCellRenderer#getTableCellRendererComponent(org.wings.STable, java.lang.Object, boolean, int, int)
			 */
			public SComponent getTableCellRendererComponent(STable table, Object value, boolean isSelected, int row,
					int column) {
		    	zoomableImage = new XZoomableImage();
				
				if (value instanceof Image) {
					zoomableImage.setPreviewImageMaxDimension(new SDimension(100, 80));
					zoomableImage.setDetailImage((Image) value);
				}
				return zoomableImage;
			}
        });

        cartTable.setModel(cartModel);
        cartTable.setSelectionMode(SListSelectionModel.NO_SELECTION);
        cartTable.getColumnModel().getColumn(1).setCellRenderer(new IntegerCellRenderer());

        productDetails.setWordWrap(true);

        rootFrame.add(productTable, "products");
        rootFrame.add(productDetails, "productDetails");
        rootFrame.add(cartTable, "cart");

        rootFrame.getContentPane().setPreferredSize(SDimension.FULLAREA);
        rootFrame.getContentPane().setVerticalAlignment(SConstants.TOP_ALIGN);
        rootFrame.setVisible(true);
    }

    private URL buildURLContext(String image) {
    	
    	HttpServletRequest request = SessionManager.getSession().getServletRequest();
    	
    	try {
			return new URL(request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/" + request.getContextPath() + "/images/" + image);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }
}
