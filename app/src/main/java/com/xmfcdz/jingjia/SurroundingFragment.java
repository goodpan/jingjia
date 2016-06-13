package com.xmfcdz.jingjia;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SurroundingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SurroundingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SurroundingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ListView  surr_lv;
    private SimpleAdapter simpleAdapter;
    private List<Map<String,Object>> arrList;
    private Map<String,Object> map;
    public SurroundingFragment() {
        // Required empty public constructor
    }
    private String[] place = new String[]{
            "湖里",
            "思明",
            "海沧",
            "集美",
            "杏林",
            "同安",
            "翔安"
    };
    private String[] tds = new String[]{
            "22" ,
            "33" ,
            "55" ,
            "62" ,
            "87" ,
            "88" ,
            "90"
    };
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SurroundingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SurroundingFragment newInstance(String param1, String param2) {
        SurroundingFragment fragment = new SurroundingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_surrounding, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
        if (savedInstanceState != null
                && savedInstanceState.getBoolean("isConflict", false))
            return;

        surr_lv = (ListView)getView().findViewById(R.id.surr_lv);
        arrList = new ArrayList<Map<String, Object>>();
        for (int i = 1; i<place.length; i++){
            map = new HashMap<String, Object>();
            map.put("ranking",i);
            map.put("place",place[i]);
            map.put("tds",tds[i]);
            arrList.add(map);
        }
        simpleAdapter = new SimpleAdapter(
                getContext(),
                arrList,
                R.layout.surrounding_ranking_item,
                new String[]{"ranking","place","tds"},
                new int[]{R.id.ranking,R.id.palce,R.id.tds}
                );
        surr_lv.setAdapter(simpleAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
