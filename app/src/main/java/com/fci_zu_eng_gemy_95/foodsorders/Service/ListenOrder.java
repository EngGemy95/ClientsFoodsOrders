package com.fci_zu_eng_gemy_95.foodsorders.Service;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.fci_zu_eng_gemy_95.foodsorders.Common.Common;
import com.fci_zu_eng_gemy_95.foodsorders.Model.Requests;
import com.fci_zu_eng_gemy_95.foodsorders.OrdersStatus;
import com.fci_zu_eng_gemy_95.foodsorders.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ListenOrder extends Service implements ChildEventListener {

    FirebaseDatabase database ;
    DatabaseReference requestsRef ;

    public ListenOrder() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        database = FirebaseDatabase.getInstance();
        requestsRef = database.getReference("requests");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        requestsRef.addChildEventListener(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        Requests requests = dataSnapshot.getValue(Requests.class);
        showNotification(dataSnapshot.getKey(),requests);
    }

    private void showNotification(String key, Requests requests) {

        Intent intent = new Intent(getBaseContext(), OrdersStatus.class);
        // we will use it when we open OrderStatus Activity from notification not from Home Activity
        intent.putExtra("userPhone",requests.getPhone());

        PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(),
                0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext(),"StatusService");
        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setTicker("Clients")
                .setContentInfo("Your order is updated ")
                .setContentText("Order #"+key+" was update status to "+ Common.convertCodeToStatus(requests.getStatus()))
                .setContentIntent(contentIntent)
                .setContentInfo("Info")
                .setSmallIcon(R.mipmap.ic_launcher);

        NotificationManager notificationManager =
                (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(1,builder.build());

    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
