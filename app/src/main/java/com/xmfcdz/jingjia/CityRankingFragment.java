package com.xmfcdz.jingjia;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CityRankingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CityRankingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CityRankingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private ListView surr_lv;
    private SimpleAdapter simpleAdapter;
    private List<Map<String,Object>> arrList;
    private Map<String,Object> map;

    private String[] place = new String[]{
            "厦门","北京", "天津", "上海","南京", "武汉", "广州", "深圳", "长沙", "合肥", "福州", "青岛", "西安", "石家庄", "泉州"
    };
    private String[] tds = new String[]{
            "22" , "33" , "55" , "62" , "87" , "88" , "93" , "111" , "343" , "233" , "332" , "445" , "345" , "434" , "565"
    };





    public CityRankingFragment() {
        // Required empty public constructor
    }



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CityRankingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CityRankingFragment newInstance(String param1, String param2) {
        CityRankingFragment fragment = new CityRankingFragment();
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
        if (savedInstanceState != null
                && savedInstanceState.getBoolean("isConflict", false))
            return;
        //设置标题

        surr_lv = (ListView)getView().findViewById(R.id.city_ranking_lv);
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
                R.layout.city_ranking_item,
                new String[]{"ranking","place","tds"},
                new int[]{R.id.city_ranking,R.id.city_palce,R.id.city_tds}
        );
        surr_lv.setAdapter(simpleAdapter);

        surr_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView cityTextView = (TextView) view.findViewById(R.id.city_palce);
                String cityName = cityTextView.getText().toString();
                Intent intent = new Intent(getActivity(), BaiduMapActivity.class);
                intent.putExtra("city", cityName);
                getActivity().setResult(2, intent);
                getActivity().startActivity(intent);
//                getActivity().finish();
            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_city_ranking, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
