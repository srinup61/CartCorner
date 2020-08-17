/*
 * Copyright (c) 2017. http://hiteshsahu.com- All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * If you use or distribute this project then you MUST ADD A COPY OF LICENCE
 * along with the project.
 *  Written by Hitesh Sahu <hiteshkrsahu@Gmail.com>, 2017.
 */

package com.intern.kartcorner.helper;


import com.intern.kartcorner.entities.Product;
import com.intern.kartcorner.model.OrdersCommonClass;
import com.intern.kartcorner.model.ProductCommonClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
public class CenterRepository {

    private static CenterRepository centerRepository;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    private String date,time;

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public static List<String> list = Collections.synchronizedList(new ArrayList<String>());
    private ConcurrentHashMap<String, ArrayList<Product>> mapOfProductsInCategory = new ConcurrentHashMap<String, ArrayList<Product>>();
    private List<ProductCommonClass> listOfProductsInShoppingList = Collections.synchronizedList(new ArrayList<ProductCommonClass>());

    public List<OrdersCommonClass> getListofAddress() {
        return listofAddress;
    }

    public void setListofAddress(List<OrdersCommonClass> listofAddress) {
        this.listofAddress = listofAddress;
    }

    private List<OrdersCommonClass> listofAddress = Collections.synchronizedList(new ArrayList<OrdersCommonClass>());
    private List<Set<String>> listOfItemSetsForDataMining = new ArrayList<>();

    public List<ProductCommonClass> getListInCart() {
        return listInCart;
    }

    public void setListInCart(List<ProductCommonClass> listInCart) {
        this.listInCart = listInCart;
    }

    private List<ProductCommonClass> listInCart = Collections.synchronizedList(new ArrayList<ProductCommonClass>());

    public static CenterRepository getCenterRepository() {

        if (null == centerRepository) {
            centerRepository = new CenterRepository();
        }
        return centerRepository;
    }


    public List<ProductCommonClass> getListOfProductsInShoppingList() {
        return listOfProductsInShoppingList;
    }

    public void setListOfProductsInShoppingList(ArrayList<ProductCommonClass> getShoppingList) {
        this.listOfProductsInShoppingList = getShoppingList;
    }

    public Map<String, ArrayList<Product>> getMapOfProductsInCategory() {

        return mapOfProductsInCategory;
    }

    public void setMapOfProductsInCategory(ConcurrentHashMap<String, ArrayList<Product>> mapOfProductsInCategory) {
        this.mapOfProductsInCategory = mapOfProductsInCategory;
    }


    public List<Set<String>> getItemSetList() {

        return listOfItemSetsForDataMining;
    }

    public void addToItemSetList(HashSet list) {
        listOfItemSetsForDataMining.add(list);
    }

}
