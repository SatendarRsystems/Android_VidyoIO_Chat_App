package com.vidyo.io.demo.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.vidyo.VidyoClient.Endpoint.Participant;
import com.vidyo.io.demo.R;
import com.vidyo.io.demo.utilities.NameBadgeUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * Summary: Adapter Component
 * Description: This is adapter for participants list
 * @author RSI
 * @date 20.08.2018
 */
public class ParticipantListAdapter extends RecyclerView.Adapter<ParticipantListAdapter.ItemHolder> {

    /**
     * Declare Activity object
     */
    private Activity mActivity;

    /**
     * Declare Participant array list
     */
    private List<Participant> participantList;

    /**
     * Declare NameBadgeUtil object
     */
    private NameBadgeUtil nameBadgeUtil;

    /**
     * Constructor
     * @param activity Activity object to execute context methods
     * @param participantList  List of participants
     */
    public ParticipantListAdapter(Activity activity, List<Participant> participantList) {
        this.participantList = new ArrayList<>(participantList);
        this.mActivity = activity;
        this.nameBadgeUtil = new NameBadgeUtil(mActivity);
    }

    /**
     * Create view holder object
     */
    @NonNull
    @Override
    public ParticipantListAdapter.ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = mActivity.getLayoutInflater().inflate(R.layout.item_participant_list, viewGroup, false);
        return new ItemHolder(view);
    }

    /**
     * Bind view holder's with participant object
     * @param itemHolder ViewHolder of item
     * @param position Position of item
     */
    @Override
    public void onBindViewHolder(@NonNull ParticipantListAdapter.ItemHolder itemHolder, int position) {
        Participant participant = participantList.get(position);

        itemHolder.textViewParticipantName.setText(participant.getName());
        itemHolder.textViewBadge.setImageBitmap(nameBadgeUtil.generateCircleBitmap(ContextCompat.getColor(mActivity, R.color.colorPrimaryDark), 42, participant.getName().substring(0, 1).toUpperCase()));
    }

    /**
     * Get item count if array list is not null
     */
    @Override
    public int getItemCount() {
        return (this.participantList != null ? this.participantList.size() : 0);
    }

    /**
     * Update participant list in fragment when any participant join or leave group
     * @param participantList  List of participants
     */
    public void updateList(List<Participant> participantList) {
        this.participantList = new ArrayList<>(participantList);
        notifyDataSetChanged();
    }

    /**
     * Participant item view holder
     */
    public class ItemHolder extends RecyclerView.ViewHolder {

        TextView textViewParticipantName;
        ImageView textViewBadge;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            textViewParticipantName = (TextView) itemView.findViewById(R.id.textViewParticipantName);
            textViewBadge = (ImageView) itemView.findViewById(R.id.textViewBadge);

        }
    }

}
