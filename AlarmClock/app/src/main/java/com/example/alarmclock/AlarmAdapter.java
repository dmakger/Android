package com.example.alarmclock;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.opengl.Visibility;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {

    private Context context;
    private List<Alarm> alarmList;
    private int layout;

    public AlarmAdapter (Context context, int layout, List<Alarm> alarmList) {
        this.context = context;
        this.layout = layout;
        this.alarmList = alarmList;
    }

    public void add(Alarm alarm) {
        alarmList.add(alarm);
        notifyItemInserted(alarmList.size());
    }

    public void remove(int position) {
        System.out.println(position);
        alarmList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(0, alarmList.size());
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layout, parent, false);
        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        Alarm alarm = alarmList.get(position);
        holder.time.setText(alarm.time);
        holder.stateAlarm.setChecked(alarm.stateAlarm);
        holder.stateAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarm.stateAlarm = !alarm.stateAlarm;
                if (alarm.stateAlarm) {
                    alarm.seconds = alarm.getNeedSeconds(alarm.time);
                    System.out.println("Set: " + alarm.seconds);
                } else {
                    alarm.seconds = 0;
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Delete it?");
                builder.setMessage("Do you really want to delete it?");
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        remove(position);
                    }
                });
                builder.show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public class AlarmViewHolder extends RecyclerView.ViewHolder {

        TextView time;
        Switch stateAlarm;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            stateAlarm = itemView.findViewById(R.id.state_alarm);
        }
    }
}
