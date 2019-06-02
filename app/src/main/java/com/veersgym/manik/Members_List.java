package com.veersgym.manik;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Members_List extends AppCompatActivity implements CustomRecycle.onLongItemCustom {

    private RecyclerView recyclerView;
    private CustomRecycle customRecycleAdapter;
    private ArrayList<GymMember> gymMemberArrayList = new ArrayList<>();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("All_Members");
    private CompoundButton show_due_sw;
    private boolean isDueSend = false;
    private String data = "";
    private EditText et_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members__list);
        Intent intent = getIntent();
        data = intent.getStringExtra("KEY");
        show_due_sw = findViewById(R.id.show_overdue);
        et_search = findViewById(R.id.et_search);
        recyclerView = findViewById(R.id.rv_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        // addMemberFromDB();
        customRecycleAdapter = new CustomRecycle(gymMemberArrayList, getApplicationContext(), this);
        refreshList();

        show_due_sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isDueSend = true;
                    refreshList();
                } else {
                    isDueSend = false;
                    refreshList();
                }
            }
        });

        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if(!et_search.getText().toString().isEmpty()){
                        databaseReference.orderByChild("gymid").equalTo(Integer.parseInt(et_search.getText().toString())).addValueEventListener(firebaseEventListener);
                    }
                }
                return true;
            }
        });
    }

    private void refreshList() {
        if (data.equals("VEERS")) {
            databaseReference.orderByChild("branch").equalTo("Veer's Gym").addValueEventListener(firebaseEventListener);
        } else if (data.equals("CROSSFIT")) {
            databaseReference.orderByChild("branch").equalTo("Crossfit Fitness").addValueEventListener(firebaseEventListener);
        } else {
            et_search.setVisibility(View.VISIBLE);
            databaseReference.addValueEventListener(firebaseEventListener);
        }
    }


    private Context context = this;
    private ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            if (direction == ItemTouchHelper.RIGHT) {
                GymMember gymMember = gymMemberArrayList.get(viewHolder.getAdapterPosition());
                showAlertDialog(context, gymMember.getName(), gymMember.getId());

                customRecycleAdapter.notifyDataSetChanged();
            }

            if (direction == ItemTouchHelper.LEFT) {
                final GymMember gymMember = gymMemberArrayList.get(viewHolder.getAdapterPosition());
                customRecycleAdapter.notifyDataSetChanged();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Late SMS");
                builder.setMessage("Do you want to remind about late fee?");
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String msg = "Hey " + gymMember.getName() + ",\n\nThis to inform you that your Membership with " + gymMember.getBranch() + " Id no. " + gymMember.getGymid() + " has expired. So, renew it as soon as possible." +
                                "\n\nTeam " + gymMember.getBranch();

                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        sendSMS(gymMember.getMobile(), msg);
                                    }
                                });
                            }
                        };
                        thread.start();
                    }
                });
                builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setIcon(android.R.drawable.ic_popup_reminder);
                dialog.show();

            }
        }


        @Override
        public int getSwipeDirs(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

            GymMember gymMember = gymMemberArrayList.get(viewHolder.getAdapterPosition());

            if (days_remain(gymMember.getFeedate()) < 0) {
                return ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;

            } else {
                return ItemTouchHelper.RIGHT;

            }
            //return super.getSwipeDirs(recyclerView, viewHolder);
        }

    };


    public static void showAlertDialog(final Context context, String title, final String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Remove " + title);
        builder.setMessage("Are you sure you want to delete this entry?");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Member removed", Toast.LENGTH_SHORT).show();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("All_Members");

                Query applesQuery = databaseReference.child(id);

                applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                            appleSnapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.setIcon(android.R.drawable.ic_delete);
        dialog.show();

    }

    private ValueEventListener firebaseEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            gymMemberArrayList.clear();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                GymMember gymMember = snapshot.getValue(GymMember.class);
                if (isDueSend) {
                    if (days_remain(gymMember.getFeedate()) < 0) {
                        gymMemberArrayList.add(gymMember);
                    }
                } else {
                    gymMemberArrayList.add(gymMember);
                }

            }

            recyclerView.setAdapter(customRecycleAdapter);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private int days_remain(long feedate) {
        long diff = feedate - System.currentTimeMillis();
        // Calculate difference in days
        int diffDays = (int) (diff / (24 * 60 * 60 * 1000));
        return diffDays;
    }


    @Override
    public void onLongItemClicked(int clickeditem) {
        GymMember gymMember = gymMemberArrayList.get(clickeditem);
        showAddDialog(gymMember);

    }

    private void showAddDialog(final GymMember gymMember) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_update_fee, null);
        dialogBuilder.setView(dialogView);

        final EditText editText = (EditText) dialogView.findViewById(R.id.et_update_months);
        editText.requestFocus();
        final EditText editText1 = dialogView.findViewById(R.id.et_update_amount);
        Button button = dialogView.findViewById(R.id.bt_update_done);
        final CheckBox checkBox = dialogView.findViewById(R.id.cb_update_isRejoin);
        TextView name_tv = dialogView.findViewById(R.id.tv_update_name);
        TextView id_tv = dialogView.findViewById(R.id.tv_update_id);
        name_tv.setText(gymMember.getName());
        id_tv.setText(String.valueOf(gymMember.getGymid()));
        final String fb_id = gymMember.getId();
        final long feedate = gymMember.getFeedate();

        final AlertDialog alertDialog = dialogBuilder.create();

        alertDialog.show();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().isEmpty() || editText1.getText().toString().isEmpty()) {
                    Toast.makeText(context, "Enter details", Toast.LENGTH_SHORT).show();
                } else {
                    if (checkBox.isChecked()) {
                        databaseReference.child(fb_id).child("feedate").setValue((Integer.parseInt(editText.getText().toString()) * 2622880000L) + System.currentTimeMillis());
                        databaseReference.child(fb_id).child("amount").setValue(Integer.parseInt(editText1.getText().toString()));
                        String msg = "Hey "+gymMember.getName()+",\n\n"+"Your Membership with "+gymMember.getBranch()+" ID "+gymMember.getGymid()+" has been successfully renewed for next "
                                +days_remain((Integer.parseInt(editText.getText().toString()) * 2622880000L) + System.currentTimeMillis())
                                +" days.\n\nTeam "+gymMember.getBranch()+"\uD83D\uDCAA";
                        sendSMS(gymMember.getMobile(),msg);
                    } else {
                        databaseReference.child(fb_id).child("feedate").setValue((Integer.parseInt(editText.getText().toString()) * 2622880000L) + feedate);
                        databaseReference.child(fb_id).child("amount").setValue(Integer.parseInt(editText1.getText().toString()));
                        String msg = "Hey "+gymMember.getName()+",\n\n"+"Welcome back, Your Membership with "+gymMember.getBranch()+" ID "+gymMember.getGymid()+" has been successfully renewed for next "
                                +days_remain((Integer.parseInt(editText.getText().toString()) * 2622880000L) + System.currentTimeMillis())
                                +" days.\n\nTeam "+gymMember.getBranch()+"\uD83D\uDCAA";
                        sendSMS(gymMember.getMobile(),msg);

                    }
                }
                alertDialog.dismiss();
            }
        });
    }


    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> parts = smsManager.divideMessage(msg);
            smsManager.sendMultipartTextMessage("+91" + phoneNo, null, parts, null, null);
//            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

}
