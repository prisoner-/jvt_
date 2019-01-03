package com.jvt.ui.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jvt.R;
import com.jvt.bean.SearchDeviceInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liaolei on 2018/12/28.
 */
public class    SearchAdapter extends BaseAdapter {
    public static final int MODIFY_DIR_SUCCESS = 4;
    public static final int MODIFY_DIR_FIALED = 5;
    private List<SearchDeviceInfo> nodeList;
    private Context con;
    private LayoutInflater inflater;
    // View view;
    public TextView txtParameters, txtName, txtDelete;
    int currentPosition;
    public ProgressDialog progressDialog;
    public boolean parentIsDvr = false;

    public SearchAdapter(Context con) {
        this.con = con;
        inflater = LayoutInflater.from(con);
        nodeList = new ArrayList<SearchDeviceInfo>();
        // editor = con.getSharedPreferences(FgFavorite.fileName,
        // Context.MODE_PRIVATE).edit();
    }

    public List<SearchDeviceInfo> getNodeList() {
        return nodeList;
    }

    public void setNodeList(List<SearchDeviceInfo> nodeList) {
        this.nodeList = nodeList;
        notifyDataSetChanged();
    }

    public boolean isParentIsDvr() {
        return parentIsDvr;
    }

    public void setParentIsDvr(boolean parentIsDvr) {
        this.parentIsDvr = parentIsDvr;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return nodeList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        SearchDeviceInfo node = nodeList.get(position);
        ViewHolder vh = null;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_search, null);
            vh.tv = (TextView) convertView.findViewById(R.id.tvCaption);
            vh.info = (TextView) convertView.findViewById(R.id.tvInfo);

//            vh.imgaArrow = (ImageView) convertView.findViewById(R.id.imgArrow);
//            vh.add = (Button) convertView.findViewById(R.id.btn_add);

            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.tv.setText(node.getsDevName());
        vh.info.setText(node.getsIpaddr_1() + "  " + node.getsDevId() + "  "
                + node.usChNum);
//        OnClickListstener clickListener = new OnClickListstener(node, position);
////        vh.imgaArrow.setOnClickListener(clickListener);
////        vh.add.setOnClickListener(clickListener);
//        convertView.setOnClickListener(clickListener);
        return convertView;
    }

    class ViewHolder {
        TextView tv;
        TextView info;
//        ImageView imgaArrow;
//        Button add;
    }

    public class OnClickListstener implements View.OnClickListener

    {
        SearchDeviceInfo node;
        int position;

        public OnClickListstener(SearchDeviceInfo node, int position) {
            this.node = node;
            this.position = position;
        }

        @Override
        public void onClick(View v) {

            // con.startActivity();
            String name = node.getsDevName();
            if (TextUtils.isEmpty(name)) {
                //name=node.getDwVendorId()
                name = node.sDevModel;
            }
            Intent Intent = new Intent()
                    .putExtra("deviceName", name)
                    .putExtra("umid", node.getsDevId())
                    .putExtra("channels", node.usChNum)
                    .putExtra("node", node);

            Activity activity = (Activity) con;
            activity.setResult(Activity.RESULT_OK, Intent);
            activity.finish();
        }
    }

}

