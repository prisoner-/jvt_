
package com.jvt.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Player.Source.TDevNodeInfor;
import com.jvt.MyApplication;
import com.jvt.R;
import com.jvt.bean.PlayNode;

import java.util.ArrayList;
import java.util.List;

public class DevListAdapter extends BaseAdapter {
    private final int GET_OK = 2;

    private final int GET_FAIL = 3;

    public static final int MODIFY_DIR_SUCCESS = 4;

    public static final int MODIFY_DIR_FIALED = 5;

    private List<PlayNode> nodeList;

    private Context con;

    private LayoutInflater inflater;

    public TextView txtParameters, txtName, txtDelete;

    int currentPosition;

    MyApplication application;

    public boolean parentIsDvr = false;

    private SharedPreferences.Editor editor;

    private boolean showSelecter = false;

    private ViewHolder vh = new ViewHolder();

    private Drawable defaultImg;

    private Drawable offLineDvrImg, onLineDvrImg;

    public static TDevNodeInfor info;

//	OnClickItemToAddListener onClickItemToAddListener;
//
////	FgMkListManager fgListManager;
//
//	public void setOnClickItemToAddListener(OnClickItemToAddListener onClickItemToAddListener)
//	{
//		this.onClickItemToAddListener = onClickItemToAddListener;
//	}

    public DevListAdapter(Context con) {
        this.con = con;
        inflater = LayoutInflater.from(con);
        nodeList = new ArrayList<PlayNode>();
        defaultImg = con.getResources().getDrawable(R.drawable.list_channel);
        offLineDvrImg = con.getResources().getDrawable(R.drawable.list_equipment_dis);
        onLineDvrImg = con.getResources().getDrawable(R.drawable.list_equipment);
        application = (MyApplication) con.getApplicationContext();
        editor = con.getSharedPreferences("favorite", Context.MODE_PRIVATE).edit();
    }

    public void setNodeList(List<PlayNode> nodeList) {
        this.nodeList = nodeList;
        notifyDataSetChanged();
    }

    public List<PlayNode> getNodeList() {
        return nodeList;
    }

    public boolean isShowSelecter() {
        return showSelecter;
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

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        PlayNode node = nodeList.get(position);
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_dev_list, null);
            vh.tvName = (TextView) convertView.findViewById(R.id.show_name);
            vh.btnSetting = (RelativeLayout) convertView.findViewById(R.id.item_stting);
            vh.imgThumb = (ImageView) convertView.findViewById(R.id.item_img);
            vh.imgState = (ImageView) convertView.findViewById(R.id.item_img1);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.btnSetting.setOnClickListener(new OnClickListstener(node));
        vh.imgState.setVisibility(View.GONE);
        vh.tvName.setText(node.getName() + "");
        vh.imgThumb.setImageDrawable(defaultImg);
        return convertView;
    }

    class ViewHolder {
        TextView tvName;

        RelativeLayout btnSetting;

        ImageView imgThumb;

        ImageView imgState;
    }

    public class OnClickListstener implements OnClickListener {
        PlayNode node;

        public OnClickListstener(PlayNode node) {
            this.node = node;
        }

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
//			if (v.getId() == R.id.item_stting)
//			{
//                Intent it = new Intent(con, AcModifyDevice.class);
//                Log.w("modify", "modify:" + node.getName() + ";---->id:" + node.getDeviceId());
//                it.putExtra("iConnMode", node.node.iConnMode);
//                it.putExtra("iChNo", 0);
//                it.putExtra("position", node.node.dwNodeId);
////                con.startActivityForResult(it,0);
//				((DevManagerActivity)con).startActivityForResult(it,0);
//			}
        }
    }
}
