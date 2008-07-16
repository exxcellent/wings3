package org.wings.sdnd;

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * DefaultTransferable - represents a generic implementation for Transferables in the wingS-framework
 */
public abstract class DefaultTransferable implements Transferable {
    private List<DataFlavor> supportedDataFlavors;

    /**
     * Initializes the DefaultTransferable Object with a array of DataFlavors to be supported
     * @param dataFlavorList
     */
    public DefaultTransferable(List<DataFlavor> dataFlavorList) {
        this.supportedDataFlavors = dataFlavorList;

        if(this.supportedDataFlavors == null)
            throw new IllegalArgumentException("supportedDataFlavors is null");
    }

    /**
     * Initializes the DefaultTransferable Object with a array of DataFlavors to be supported
     * @param dataFlavorArray
     */
    public DefaultTransferable(DataFlavor[] dataFlavorArray) {
        this.supportedDataFlavors = new ArrayList<DataFlavor>();

        this.supportedDataFlavors.addAll(Arrays.asList(dataFlavorArray));

        if(this.supportedDataFlavors.size() == 0)
            throw new IllegalArgumentException("supportedDataFlavors is empty");
    }

    /**
     * Returns the supported DataFlavors
     * @return
     */
    public DataFlavor[] getTransferDataFlavors() {
        return this.supportedDataFlavors.toArray(new DataFlavor[0]);
    }

    /**
     * Returns if the given DataFlavor is supported by this Transferable
     * @param flavor
     * @return
     */
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        for(DataFlavor df:this.supportedDataFlavors) {
            if(df.equals(flavor))
                return true;
        }
        
        return false;
    }

    /**
     * Returns the Transfer data for Flavor flavor if supported, throws Exception otherwise
     * @param flavor
     * @return
     * @throws UnsupportedFlavorException
     * @throws IOException
     */
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        Object data = getDataForClass(flavor, flavor.getRepresentationClass());
        if(data != null)
            return data;
        
        throw new UnsupportedFlavorException(flavor);
    }

    /**
     * Function that is called to get the Data of this Transferable for DataFlavor df, with it's representation class cls
     * @param df DataFlavor to
     * @param cls Representation class to get a Data-Object for
     * @return Data-Object this Transferable represents
     */
    protected abstract Object getDataForClass(DataFlavor df, Class<?> cls);
}
