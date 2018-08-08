package com.luoye.phoneinfo.adaper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.luoye.phoneinfo.R;
import com.luoye.phoneinfo.bean.InfoItem;

import java.util.List;

/**
 * Created by zyw on 18-8-8.
 */

public class InfoListAdapter extends RecyclerView.Adapter<InfoListAdapter.MyViewHolder> {

    private  Context context;
    private List<InfoItem> infoItems;
    private  int opened=-1;
    private Bitmap downIcon;
    private  Bitmap rightIcon;
    public InfoListAdapter(Context context,List<InfoItem> infoItems){
        this.context=context;
        this.infoItems=infoItems;
        downIcon=BitmapFactory.decodeResource(context.getResources(),R.mipmap.arrows_down);
        rightIcon=BitmapFactory.decodeResource(context.getResources(),R.mipmap.arrows_right);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView=LayoutInflater.from(context).inflate(R.layout.info_list_item,viewGroup,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i) {
        viewHolder.bindView(i, infoItems.get(i));
    }

    @Override
    public int getItemCount() {
        return infoItems.size();
    }

    class MyViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView titleTextView;
        private  TextView contentTextView;
        private RelativeLayout titleLayout;
        private  LinearLayout contentLayout;
        private ImageView flagImageView;

        public MyViewHolder(View itemView){
            super(itemView);

            titleTextView= itemView.findViewById(R.id.item_tv_title);
            contentTextView =itemView.findViewById(R.id.item_tv_content);
            titleLayout =itemView.findViewById(R.id.item_title_layout);
            contentLayout=itemView.findViewById(R.id.item_content_layout);
            flagImageView=itemView.findViewById(R.id.item_arrow_iv);

            titleLayout.setOnClickListener(this);
        }

        void bindView(int pos,InfoItem infoItem){
            titleTextView.setText(infoItem.getTitle());
            contentTextView.setText(infoItem.getContent());
            if(pos==opened){
                contentLayout.setVisibility(View.VISIBLE);
                flagImageView.setImageBitmap(downIcon);
            }
            else{
                contentLayout.setVisibility(View.GONE);
                flagImageView.setImageBitmap(rightIcon);
            }
        }

        @Override
        public void onClick(View view) {
           int pos=getAdapterPosition();
            if(pos==opened){
                //点击项已经打开，关闭
                opened=-1;
                notifyItemChanged(pos);
            }
            else{
                int oldOpened = opened;
                opened = getAdapterPosition();
                notifyItemChanged(oldOpened);
                notifyItemChanged(opened);
            }
        }


    }
}
