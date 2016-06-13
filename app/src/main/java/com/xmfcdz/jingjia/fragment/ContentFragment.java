package com.xmfcdz.jingjia.fragment;

import com.xmfcdz.jingjia.R;

import android.view.View;

/**
 * 主页内容
 * 
 * @author Kevin
 * 
 */
public class ContentFragment extends BaseFragment {

	@Override
	public View initViews() {
		View view = View.inflate(mActivity, R.layout.fragment_content, null);
		return view;
	}

}
