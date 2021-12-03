package com.challengers.trackmyorder;

import android.app.Application;
import android.content.ContextWrapper;

import com.challengers.trackmyorder.model.DelBoy;
import com.challengers.trackmyorder.model.Order;
import com.challengers.trackmyorder.model.User;
import com.challengers.trackmyorder.util.Constants;
import com.challengers.trackmyorder.util.Prefs;
import com.firebase.client.Firebase;
//import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

//import io.realm.Realm;
//import io.realm.RealmConfiguration;


public class AppClass extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //Firebase Init
        Firebase.setAndroidContext(this);

        /*
        * This is to initialize a wrapper class for saving the preferences.
        * */
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        // initialize Realm
        Realm.init(getApplicationContext());




        RealmConfiguration config = new RealmConfiguration.Builder(
        )
                .name("trackmyorder.realm")
                .build();
        Realm.setDefaultConfiguration(config);

        /*if(Prefs.contains(Constants.FIRST_RUN_KEY)) {
            if(Prefs.getBoolean(Constants.FIRST_RUN_KEY, true)) {
                populateDummyData();
            }
        } else {
            populateDummyData();
        }*/
    }

    private void populateDummyData() {
        Realm realm = Realm.getDefaultInstance();

        DelBoy delBoy = new DelBoy();
        delBoy.setId("email");
        delBoy.setName("Users");

        Order order = new Order();
        order.setOrderId("parcel 1001");
        order.setItem("Above 30kg");
        order.setStatus(Constants.STATUS_UNKNOWN);

        Order order1 = new Order();
        order1.setOrderId("parcel 1002");
        order1.setItem("Below 30kg");
        order1.setStatus(Constants.STATUS_UNKNOWN);

        Order order2 = new Order();
        order2.setOrderId("parcel 1003");
        order2.setItem("letters");
        order2.setStatus(Constants.STATUS_UNKNOWN);

        Order order3 = new Order();
        order3.setOrderId("parcel 1004");
        order3.setItem("delicate and fragile");
        order3.setStatus(Constants.STATUS_UNKNOWN);

        List<Order> orderList = new ArrayList<>(4);
        orderList.add(order);
        orderList.add(order1);
        orderList.add(order2);
        orderList.add(order3);

        String orderIdList = "";
        for (Order orders : orderList) {
            if (orderIdList.equals("")) {
                orderIdList = orders.getOrderId();
            } else {
                orderIdList = orderIdList + Constants.LOCATION_DELIMITER + orders.getOrderId();
            }
        }
        delBoy.setCurrentOrderIds(orderIdList);

        User user = new User();
        user.setUserId("email");
        user.setUsername("email");
        user.setCurrentOrderId(order1.getOrderId());

        User user1 = new User();
        user1.setUserId("email");
        user1.setUsername("email");
        user1.setCurrentOrderId(order.getOrderId());

        User user2 = new User();
        user2.setUserId("email");
        user2.setUsername("email");
        user2.setCurrentOrderId(order2.getOrderId());

        User user3 = new User();
        user3.setUserId("email");
        user3.setUsername("email");
        user3.setCurrentOrderId(order3.getOrderId());

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(delBoy);
        realm.copyToRealmOrUpdate(orderList);
        realm.copyToRealmOrUpdate(user);
        realm.copyToRealmOrUpdate(user1);
        realm.copyToRealmOrUpdate(user2);
        realm.copyToRealmOrUpdate(user3);
        realm.commitTransaction();

        Prefs.putBoolean(Constants.FIRST_RUN_KEY, false);
    }
}
