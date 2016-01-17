package com.example.administrator.mylistviewslide;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private ListView lv_mian;
    private ArrayList list;
    private MyAdaPter adapter;
    private MyFrameLayOut layOut;//打开的item
    private MyFrameLayOut.OnStateChangeListener onStateChangeListener=new MyFrameLayOut.OnStateChangeListener() {
        @Override
        public void onOpen(MyFrameLayOut myframgelayOut) {
            layOut=myframgelayOut;
        }

        @Override
        public void onClose(MyFrameLayOut myframgelayOut) {
                layOut=null;
        }

        @Override
        public void onDown(MyFrameLayOut myframgelayOut) {
          if(layOut!=null) {
              layOut.close();
          }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv_mian = (ListView) findViewById(R.id.lv_mian);

        list = new ArrayList<String>();
        for (int i = 0; i < 40; i++) {
            list.add("content" + i);
        }
        adapter = new MyAdaPter();
        lv_mian.setAdapter(adapter);
    }
    class MyAdaPter extends BaseAdapter{
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder=null;
            if(convertView==null) {
                convertView=View.inflate(MainActivity.this,R.layout.item,null);
                viewHolder=new ViewHolder();
                viewHolder.content= (TextView) convertView.findViewById(R.id.item_content);
                final ViewHolder finalViewHolder = viewHolder;
                viewHolder.content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, finalViewHolder.content.getText().toString(),Toast.LENGTH_SHORT).show();
                    }
                });
                viewHolder.delete= (TextView) convertView.findViewById(R.id.delete);
                viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        list.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.content.setText((String)list.get(position));
            MyFrameLayOut view= (MyFrameLayOut) convertView;
            view.setOnStateChangeListener(onStateChangeListener);
            return convertView;
        }
        private class ViewHolder {
            private TextView content;
            private TextView delete;
        }
    }
}
