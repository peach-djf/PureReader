package io.weicools.purereader.ui.meizi;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.weicools.purereader.AppConfig;
import io.weicools.purereader.R;
import io.weicools.purereader.data.GankData;
import io.weicools.purereader.ui.LoadMoreRecyclerOnScrollListener;
import io.weicools.purereader.ui.gank.GankContract;
import io.weicools.purereader.ui.gank.GankPresenter;

/**
 * A simple {@link Fragment} subclass.
 *
 * show girls
 */
public class GirlsFragment extends Fragment implements GankContract.View {
    @BindView(R.id.rv_girls)
    RecyclerView mRecyclerView;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;
    Unbinder unbinder;

    private GirlAdapter mAdapter;
    private GankContract.Presenter mPresenter;

    private int currPage = 1;
    private boolean mIsFirstLoad = true;

    public static GirlsFragment newInstance() {
        return new GirlsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_girls, container, false);
        unbinder = ButterKnife.bind(this, view);

        new GankPresenter(this);
        mAdapter = new GirlAdapter(getContext());
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRefreshLayout.setColorSchemeColors(ContextCompat.getColor(view.getContext(), R.color.colorAccent));
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadGankData(true, AppConfig.TYPE_GIRLS, 1);
            }
        });

        mRecyclerView.addOnScrollListener(new LoadMoreRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                currPage = current_page;
                mPresenter.loadGankData(false, AppConfig.TYPE_GIRLS, currPage);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setLoadingIndicator(mIsFirstLoad);
        if (mIsFirstLoad) {
            mPresenter.loadGankData(false, AppConfig.TYPE_GIRLS, 1);
            mIsFirstLoad = false;
        } else {
            //mPresenter.loadGankData(category, 10, currPage);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void setPresenter(GankContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void setLoadingIndicator(boolean active) {
        mRefreshLayout.setRefreshing(active);
    }

    @Override
    public void showResult(List<GankData> dataList) {
        mAdapter.setDataList(dataList);
    }

    @Override
    public void updateResult(List<GankData> dataList) {
        mAdapter.updateData(dataList);
    }

    @Override
    public void showLoadingDataError() {
        //mEmptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showNoData() {
        //mEmptyView.setVisibility(View.VISIBLE);
    }
}
