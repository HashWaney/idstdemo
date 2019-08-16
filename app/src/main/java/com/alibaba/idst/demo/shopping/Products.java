package com.alibaba.idst.demo.shopping;

import com.alibaba.nls.transport.shoppingcart.AliProduct;

public class Products extends AliProduct {

    String name;

    /**
     * @param identityCode 商品唯一标识码
     * @param num          商品数量
     */
    public Products(String identityCode, int num, String name) {
        super(identityCode, num);
        this.name = name;
    }

    @Override
    public AliProduct clone() {
        Products products = new Products(this.getIdentityCode(), this.getNum(), this.name);
        return products;
    }
}
