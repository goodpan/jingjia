package com.xmfcdz.jingjia.fragment;

import com.xmfcdz.jingjia.R;

import android.view.View;

/**
 * 侧边栏
 * 
 * @author Kevin
 * 
 */
public class LeftMenuFragment extends BaseFragment {

	@Override
	public View initViews() {
		View view = View.inflate(mActivity, R.layout.fragment_left_menu, null);
		return view;
	}

}
