package com.xmfcdz.jingjia;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.BarChartView;
import com.db.chart.view.LineChartView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AWeekTrendFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AWeekTrendFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AWeekTrendFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private LineChartView mChart;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private BarChartView mChartOne;
    private final String[] mLabels= {"周一", "周二", "周三", "周四", "周五","周六","周日"};
    private final float [][] mValues = {{950f, 750f, 550f, 450f, 100f,880f,501f}};
    private OnFragmentInteractionListener mListener;
    private boolean hidden;
    private LineChartView mLineChart = null;
    private LineSet dataset = null;
    private Paint gridPaint = null;
    public AWeekTrendFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AWeekTrendFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AWeekTrendFragment newInstance(String param1, String param2) {
        AWeekTrendFragment fragment = new AWeekTrendFragment();
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

        return  inflater.inflate(R.layout.fragment_aweek_trend, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
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

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden) {
            refresh();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (!hidden) {
            refresh();
        }

    }

    // 刷新ui
    public void refresh() {
        try {
            // 可能会在子线程中调到这方法
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    mLineChart = (LineChartView)getView().findViewById(R.id.linechart_week);
                    produceLineChar(mLineChart);
                    mLineChart.invalidate();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void produceLineChar(LineChartView chart){
        dataset = new LineSet(mLabels, mValues[0]);
        dataset.setColor(Color.parseColor("#47c0fc"))//设置直线颜色
                .setDotsStrokeThickness(Tools.fromDpToPx(2))
                .setDotsStrokeColor(Color.parseColor("#47c0fc"))//设置 圈圈颜色
                .setDotsColor(Color.parseColor("#eef1f6"));
//                .setFill(Color.parseColor("#47c0fc"));//设置填充颜色
        chart.addData(dataset);

        gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#308E9196"));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setAntiAlias(true);
        gridPaint.setStrokeWidth(Tools.fromDpToPx(1f));
        chart.setBorderSpacing(1)
                .setAxisBorderValues(0, 1000, 200);
        chart.setLabelsColor(Color.parseColor("#ffffff"));
        chart.setFontSize(32);
        chart.setAxisColor(Color.parseColor("#47c0fc"));
        chart.show();
        chart.invalidate();
    }
}
