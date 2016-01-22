package com.example.maptest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.maptest.DBHelper;

public class HistoryActivity extends Activity implements AdapterView.OnItemLongClickListener, View.OnClickListener {

    private SimpleCursorAdapter adapter;
    private ListView listView;
    private DBHelper dbHelper;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_activity);
        listView = (ListView) findViewById(R.id.history_list);

        dbHelper = new DBHelper(this);
        cursor = dbHelper.select();
        String[] from = new String[]{DBHelper.FIELD_ID, DBHelper.DATE, DBHelper.DISTANCE, DBHelper.TIME, DBHelper.ENERGY};
        int[] to = new int[]{R.id.history_id, R.id.history_date, R.id.history_distance, R.id.history_time, R.id.history_energy};

        adapter = new SimpleCursorAdapter(this, R.layout.history_item, cursor, from, to){
            @Override
            public void setViewText(TextView v, String text) {

                super.setViewText(v, convText(v, text));
            }

            private String convText(TextView view, String text) {
                switch (view.getId()) {
                    case R.id.history_date:
                        Date date = null;
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                        try {

                            date = format.parse(text);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        Date currentDate = new Date();

                        if(date.getYear() >= currentDate.getYear()){
                            format = new SimpleDateFormat("MM-dd HH:mm");
                            if(date.getMonth() <= currentDate.getMonth() && date.getDay() <= currentDate.getDay())
                                format = new SimpleDateFormat("MM-dd");
                            text = format.format(date);
                        }
                        break;
                    case R.id.history_distance:
                        text = text + "KM";
                        break;
                    case R.id.history_energy:
                        text = text + "KJ";
                        break;
                    default:
                        break;
                }
                return text;
            }
        	};

        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(this);

        Button clearButton = (Button) findViewById(R.id.history_clear);
        clearButton.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        cursor.close();
        dbHelper.close();
        super.onDestroy();
    }
    @Override
    public boolean onItemLongClick(final AdapterView<?> adapterView, final View view, final int i, long l) {

        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                switch (which){
                    case AlertDialog.BUTTON_POSITIVE:
                        TextView idView = (TextView) view.findViewById(R.id.history_id);
                        int id = Integer.parseInt(idView.getText().toString());
                        dbHelper.delete(id);
                        cursor.requery();
                        adapter.notifyDataSetChanged();
                        listView.invalidate();
                        break;
                    case AlertDialog.BUTTON_NEGATIVE:
                        break;
                    default:
                        break;
                }
            }
        };

        new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.alert)).setMessage(getResources().getString(R.string.is_delete))
                .setPositiveButton(getResources().getString(R.string.ok), onClickListener)
                .setNegativeButton(getResources().getString(R.string.cancel), onClickListener)
                .show();
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.history_clear:
                dbHelper.clear();
                cursor.requery();
                adapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }
}

