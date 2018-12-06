package com.gin.wms.manager;

import android.content.Context;

import com.bosnet.ngemart.libgen.DbClient;
import com.bosnet.ngemart.libgen.Manager;
import com.gin.wms.manager.config.ManagerSetup;
import com.gin.wms.manager.config.NgRouteUrl;
import com.gin.wms.manager.db.contract.CompUomItemContract;
import com.gin.wms.manager.db.contract.ProductContract;
import com.gin.wms.manager.db.contract.UomConversionContract;
import com.gin.wms.manager.db.data.CompUomData;
import com.gin.wms.manager.db.data.CompUomItemData;
import com.gin.wms.manager.db.data.ProductData;
import com.gin.wms.manager.db.data.UomConversionData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by manbaul on 3/26/2018.
 */

public class ProductManager extends Manager {
    public ProductManager(Context context) throws Exception {
        super(context, new NgRouteUrl().getUrlDomainMs() + "Product", new UserContext(context), new ManagerSetup());
    }

    public List<ProductData> getLocalAllProducts() throws Exception {
        return DbExecuteRead(dbClient -> dbClient.Query(ProductData.class, ProductContract.Query.getSelectAll()));
    }

    public ProductData getProductFromServer(String clientId, String productId)throws Exception{
        String functionName = "GetProduct?clientId=" + clientId + "&productId=" + productId;
        ProductData result = restClient.get(ProductData.class, functionName);

        List<ProductData> resultList = new ArrayList<>();
        resultList.add(result);
        saveProduct(resultList);

        return result;
    }

    public ProductData getProduct(String productId, String clientLocationId) throws Exception {
        ProductData result = DbExecuteRead(dbClient -> dbClient.Find(ProductData.class, new String[]{productId, clientLocationId} ));
        result.setCompUom(getLocalCompUom(result.compUomId));
        return result;
    }

    public ProductData getProduct(String productId) throws Exception {
        List<ProductData> productDataList = DbExecuteRead(dbClient -> dbClient.Query(ProductData.class, ProductContract.Query.getSelectByProduct(productId)));
        if (productDataList.size() == 0)
            return null;

        ProductData result = productDataList.get(0);
        result.setCompUom(getLocalCompUom(result.compUomId));
        return result;
    }

    public CompUomData getLocalCompUom(String compUomId) throws Exception {
        if (compUomId.isEmpty())
            return null;

        return DbExecuteRead(dbClient -> {
            CompUomData result = dbClient.Find(CompUomData.class, compUomId);
            result.compUomItems = getLocalCompUomItems(compUomId);
            result.uomConversions = getLocalUomConversions(result.compUomItems);
            return result;
        });
    }

    public List<CompUomItemData> getLocalCompUomItems(String compUomId) throws Exception {
        return DbExecuteRead(dbClient -> {
            List<CompUomItemData> results = dbClient.Query(CompUomItemData.class, CompUomItemContract.Query.getSelectList(compUomId));
            return results;
        });
    }

    public List<UomConversionData> getLocalUomConversions(List<CompUomItemData> compUomItems) throws Exception {
        return DbExecuteRead(dbClient -> {
            List<UomConversionData> results = new ArrayList<>();
            String uom1 = "", uom2 = "", uom3 = "";
            if (compUomItems.size() == 0)
                return results;

            if (compUomItems.size() > 2)
                uom3 = compUomItems.get(2).uomId;

            if (compUomItems.size() > 1)
                uom2 = compUomItems.get(1).uomId;

            if (compUomItems.size() > 0)
                uom1 = compUomItems.get(0).uomId;

            results = dbClient.Query(UomConversionData.class, UomConversionContract.Query.getSelectList(uom1, uom2, uom3));
            return results;
        });
    }

    public List<ProductData> getAllProducts() throws Exception {
        List<ProductData> results = restClient.getList(ProductData[].class, "getAllProducts");
        saveProduct(results);

        return results;
    }

    private void saveProduct(List<ProductData> productDataList)throws Exception{
        DbExecuteWrite(dbClient -> {
            for (ProductData productData : productDataList){
                productData.compUomId = productData.getCompUom().compUomId;
                dbClient.Save(productData.getCompUom());
                for (CompUomItemData compUomItemData :
                        productData.getCompUom().compUomItems) {
                    compUomItemData.compUomId = productData.getCompUom().compUomId;
                    dbClient.Save(compUomItemData);
                }

                for (UomConversionData uomConversionData :
                        productData.getCompUom().uomConversions) {
                    dbClient.Save(uomConversionData);
                }
                dbClient.Save(productData);
            }
        });
    }
}
