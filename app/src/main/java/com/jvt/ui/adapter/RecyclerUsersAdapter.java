package com.jvt.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.Player.Core.UserImg.UserImgEntity.UserImgCompareInfo;
import com.Player.Core.Utils.CommenUtil;
import com.jvt.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liaolei on 2018/12/24.
 */
public class RecyclerUsersAdapter extends RecyclerView.Adapter<RecyclerUsersAdapter.VH> {

    public static final int USERS_SIZE = 60;//比对列表长度
    public static final int[] TYPE_LIST = {R.string.snap_blacklist, R.string.snap_whitelist, R.string.snap_vip};//比对列表长度
    List<UserImgCompareInfo> users ;
    Context mContext;

    public RecyclerUsersAdapter(Context ctx, List<UserImgCompareInfo> users) {

        this.mContext = ctx;
        this.users=users;
    }

//    public void updateList() {
//        if (users.size() >= USERS_SIZE) {
//            users.clear();
//        }
//        users.add(user);
//        notifyItemInserted(users.size() - 1);
//    }
//
//    public void updateList(List<UserImgCompareInfo> users) {
//        if (users.size() >= USERS_SIZE) {
//            users.clear();
//        }
//        users.addAll(users);
//        notifyItemInserted(users.size() - 1);
//    }


    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_recycler, viewGroup, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH vh, int i) {
        UserImgCompareInfo user = users.get(i);
//        vh.face.setImageBitmap(CommenUtil.getBitmapFromByte(user.i_snapImg));//当前比对截图
        if (user.i_iLibFlag == 1) {
            vh.libFace.setImageBitmap(CommenUtil.getBitmapFromByte(user.i_libImg));//注册图
        }
        vh.type.setText(mContext.getResources().getText(TYPE_LIST[user.i_iBWMode - 1]));
        vh.name.setText( user.i_sLibName);
//        String time[] = user.i_sCapTime.split(" ");
//        userTime.setText(time[0]);
//        userHour.setText(time[1]);
        vh.time.setText(user.i_sCapTime);
//        vh.sex.setText(mContext.getResources().getText(R.string.snap_sex)+"："+user.);
        DecimalFormat decimalFormat = new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        String p = decimalFormat.format(user.i_fSimilarity * 100);//format 返回的是字符串
//        vh.similarity.setText(mContext.getResources().getString(R.string.snap_similarity) + p + "%");
        vh.count.setText(mContext.getResources().getString(R.string.snap_count) + user.i_iCount + "");

        vh.remarks.setText("加了几个来回说了的规划了是否公开身份告诉大驾光临时间管理看是否规范的教室里");
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class VH extends RecyclerView.ViewHolder {

//        @BindView(R.id.img_face)
//        ImageView face;
        @BindView(R.id.img_lib_face)
        ImageView libFace;
        @BindView(R.id.tv_name)
        TextView name;
//        @BindView(R.id.tv_similarity)
//        TextView similarity;
        @BindView(R.id.tv_type)
        TextView type;
//        @BindView(R.id.tv_sex)
//        TextView sex;
        @BindView(R.id.tv_count)
        TextView count;
        @BindView(R.id.tv_time)
        TextView time;
        @BindView(R.id.tv_remarks)
        TextView remarks;

        public VH(@NonNull View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

    }
}