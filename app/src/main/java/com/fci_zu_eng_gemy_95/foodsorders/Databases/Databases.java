package com.fci_zu_eng_gemy_95.foodsorders.Databases;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.fci_zu_eng_gemy_95.foodsorders.Model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Databases extends SQLiteAssetHelper {
    private static final String DB_NAME="URFoodDB.db";
    private static final int DB_VERSION = 1;

    public Databases(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public List<Order> getCarts(){
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String[] sqlSelect = {"ProductId","ProductName","Quantity","Price","Discount"};
        String sqlTable = "OrderDetails";

        queryBuilder.setTables(sqlTable);
        Cursor c = queryBuilder.query(db,sqlSelect,null,null,null,null,null);

        final List<Order> results = new ArrayList<>();

        if(c.moveToFirst()){
            do {
                results.add(new Order(
                        c.getString(c.getColumnIndex("ProductId")),
                        c.getString(c.getColumnIndex("ProductName")),
                        c.getString(c.getColumnIndex("Quantity")),
                        c.getString(c.getColumnIndex("Price")),
                        c.getString(c.getColumnIndex("Discount"))));
            }while (c.moveToNext());
        }
        return results;
    }

    public void addToCart(Order order){
        SQLiteDatabase db = getWritableDatabase();
        String query = String.format("insert into OrderDetails(ProductId,ProductName,Quantity,Price,Discount)" +
                        "values('%s','%s','%s','%s','%s') ",
                order.getProductId(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice(),
                order.getDiscount());
        db.execSQL(query);
    }

    public void cleanCart(){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM ORDERDETAILS");
        db.execSQL(query);
    }

    //Favourites

    public void addToFavourites(String FoodId){
        SQLiteDatabase db = getReadableDatabase();
        String Query = String.format("INSERT INTO Favourites(FoodId) VALUES('%s');",FoodId);
        db.execSQL(Query);
    }

    public void removeFromFavourites(String FoodId){
        SQLiteDatabase db = getReadableDatabase();
        String Query = String.format("DELETE FROM Favourites WHERE FoodId = '%s';",FoodId);
        db.execSQL(Query);
    }

    public boolean isFavourite(String FoodId){
        SQLiteDatabase db = getReadableDatabase();
        String Query = String.format("SELECT * FROM Favourites WHERE FoodId = '%s';",FoodId);
        Cursor cursor = db.rawQuery(Query,null);
        if (cursor.getCount() <=0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

}
