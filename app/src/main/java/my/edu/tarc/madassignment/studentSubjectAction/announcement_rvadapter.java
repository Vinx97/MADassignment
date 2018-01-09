package my.edu.tarc.madassignment.studentSubjectAction;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import javax.xml.transform.Result;

import my.edu.tarc.madassignment.R;
import my.edu.tarc.madassignment.entities.Announcement;

/**
 * Created by ASUS on 25/12/2017.
 */

public class announcement_rvadapter extends RecyclerView.Adapter<announcement_rvadapter.ViewHolder>{

    Context context;
    List<Announcement> announcementList;

    public announcement_rvadapter(Context context, List<Announcement> announcementList) {
        this.context = context;
        this.announcementList = announcementList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_card, parent, false);
         return new ViewHolder(itemView);
    }

    @Override  //Set data
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.messageTextView.setText(announcementList.get(position).getMessage());
        holder.dateTextView.setText(announcementList.get(position).getDate());
    }

    @Override //Count list size
    public int getItemCount() {
            return announcementList.size();
    }

    public Announcement getItem(int position) {
        return announcementList.get(position);
    }

    //reference components
    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView messageTextView;
        public TextView dateTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            dateTextView = (TextView)itemView.findViewById(R.id.dateTextView);
        }
    }
}
