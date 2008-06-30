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

import org.wings.CmsFrame;
import org.wingx.table.XTableClickListener;
import org.wingx.XTable;
import org.wings.*;

import java.math.BigDecimal;

/**
 * @author hengels
 * @version $Id
 */
public class ShopExample
{
    private CmsFrame rootFrame = new CmsFrame();
    CartModel cartModel = new CartModel();
    ProductModel productModel = new ProductModel();

    private XTable productTable = new XTable();
    private XTable cartTable = new XTable();
    private SLabel productDetails = new SLabel();
    private SButton addButton = new SButton("add");

    public ShopExample() {
        productModel.getRows().add(new Product(1, "TEO-X Media", "Mediacenter", new BigDecimal("549.00")));
        productModel.getRows().add(new Product(2, "Samsung Syncmaster 223BW", "Widescreen TFT", new BigDecimal("249.00")));
        productTable.setModel(productModel);
        productTable.setSelectionMode(SListSelectionModel.NO_SELECTION);
        productTable.addClickListener(0, new XTableClickListener() {
            public void clickOccured(int row, int column) {
                productDetails.setText(productModel.getRows().get(row).getDescription());
            }
        });
        productTable.addClickListener(1, new XTableClickListener() {
            public void clickOccured(int row, int column) {
                Product product = productModel.getRows().get(row);
                cartModel.addProduct(product, 1);
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

}
