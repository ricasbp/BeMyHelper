package com.example.bemyhelper;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HelpHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HelpHistoryFragment extends ListFragment {

    private UserViewModel userViewModel;
    private User currentUser;

    private ArrayList <HelpRequest> helpRequestsList = new ArrayList<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems=new ArrayList<String>();

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;


    public HelpHistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HelpHistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HelpHistoryFragment newInstance(String param1, String param2) {
        HelpHistoryFragment fragment = new HelpHistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }



    public void aux() {
        DataBaseConnection.getInstance().getHelpRequestsAndOperation(h -> {
            if (h.getHelper().getEmail().equals(currentUser.getEmail()) || h.getDisabled().getEmail().equals(currentUser.getEmail())) {

                listItems.add(h.toString());
                adapter.notifyDataSetChanged();
                helpRequestsList.add(h);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        currentUser = userViewModel.getSelectedItem().getValue();

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        currentUser.setLastHelpHistoryItemClicked(this.helpRequestsList.get(position));
        HelpReviewFragment helpReviewFragment = new HelpReviewFragment();
        helpReviewFragment.show(getParentFragmentManager(), helpReviewFragment.getTag());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View contentView = inflater.inflate(R.layout.fragment_help_history, container, false);

        ListView listView = contentView.findViewById(android.R.id.list);



        adapter=new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1,
                listItems);
        setListAdapter(adapter);
        aux();

        return  contentView;
    }

    class CustomAdapter extends BaseAdapter {
        List<String> items;

        public CustomAdapter(List<String> items) {
            super();
            this.items = items;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return items.get(i).hashCode();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            TextView textView = new TextView(getContext());
            textView.setText(items.get(i));
            return textView;
        }
    }

}