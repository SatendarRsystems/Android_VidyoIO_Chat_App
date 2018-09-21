package com.vidyo.io.demo.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.vidyo.VidyoClient.Endpoint.Participant;
import com.vidyo.io.demo.R;
import com.vidyo.io.demo.adapters.ParticipantListAdapter;
import com.vidyo.io.demo.storage.SharedStorage;
import java.util.ArrayList;

/**
 * Summary: Participant Fragment Component
 * Description: Show participants list on participants page
 * @author RSI
 * @date 17.08.2018
 */
public class ParticipantListFragment extends Fragment {
    private RecyclerView recyclerViewParticipant;
    private ParticipantListAdapter participantListAdapter;
    private ArrayList<Participant> listParticipant;
    private TextView textViewMeetingId;
    private TextView textViewUserName;
    private TextView textViewTotalMembers;

    public ParticipantListFragment() {
    }

    /**
     * Get instance of ParticipantListFragment
     * @param list contains participants list
     */
    public static ParticipantListFragment getInstance(ArrayList<Participant> list) {
        ParticipantListFragment fragment = new ParticipantListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("participants", list);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_participant_list, container, false);
        typeCastViews(view);
        initializeMethodsAndVariables();
        setDataOnViews();
        bindRecyclerView();
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    /**
     * Typecast views to their respective objects
     */
    private void typeCastViews(View v) {
        recyclerViewParticipant = v.findViewById(R.id.recyclerViewParticipant);
        textViewMeetingId = v.findViewById(R.id.textViewMeetingId);
        textViewUserName = v.findViewById(R.id.textViewUserName);
        textViewTotalMembers = v.findViewById(R.id.textViewTotalMembers);
    }

    /**
     * Initialize methods and variables
     */
    private void initializeMethodsAndVariables() {
        listParticipant = new ArrayList<>();
        listParticipant = (ArrayList<Participant>) getArguments().getSerializable("participants");

    }

    /**
     * Set data on views
     */
    private void setDataOnViews() {
        textViewUserName.setText(SharedStorage.getUsername(getContext()));
        textViewMeetingId.setText(SharedStorage.getMeetingId(getContext()));
    }

    /**
     * Setup recycler view & bind it to adapter
     */
    private void bindRecyclerView() {
        participantListAdapter = new ParticipantListAdapter(getActivity(), listParticipant);
        recyclerViewParticipant.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider));
        recyclerViewParticipant.addItemDecoration(dividerItemDecoration);
        recyclerViewParticipant.setAdapter(participantListAdapter);
        textViewTotalMembers.setText(String.valueOf(listParticipant.size() + 1));
    }


    /**
     * Update participant list
     * @param participantList List of participants in group
     */
    public void updateParticipantList(ArrayList<Participant> participantList) {
        listParticipant = participantList;
        if (participantListAdapter != null)
            participantListAdapter.updateList(listParticipant);

        textViewTotalMembers.setText(String.valueOf(listParticipant.size() + 1));
    }
}
