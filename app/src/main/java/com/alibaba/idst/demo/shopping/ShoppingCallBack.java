package com.alibaba.idst.demo.shopping;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nls.transport.shoppingcart.AliProduct;
import com.alibaba.nls.transport.shoppingcart.ShoppingCartCallBack;
import com.alibaba.nls.transport.shoppingcart.UdsNameEnum;

import java.util.ArrayList;

public class ShoppingCallBack extends ShoppingCartCallBack {
    @Override
    protected void onShoppingCartChange(ArrayList<AliProduct> arrayList, UdsNameEnum udsNameEnum, int status) {
        android.util.Log.i("", "carList: " + JSONObject.toJSONString(arrayList) + " status: " + status);
    }

    @Override
    protected void onActionReceived(String udsNameEnum, String info) {
        android.util.Log.i("","udsName: " + udsNameEnum + " info: " + info);
    }

    @Override
    protected void onMutliChoicesAction(UdsNameEnum udsNameEnum, ArrayList<AliProduct> src, int srcNumber, ArrayList<AliProduct> dst, int dstNumber, String extra) {
        android.util.Log.i("", "udsNameEnum: " + udsNameEnum + " src: " + JSONObject.toJSONString(src) + " srcNumber: " + srcNumber + " dst: " + JSONObject.toJSONString(dst) + " dstNumber: " + dstNumber + " extra: " + extra);
    }
}
