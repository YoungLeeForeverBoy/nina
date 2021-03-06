package crazysheep.io.nina.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import crazysheep.io.nina.R;
import crazysheep.io.nina.adapter.FragmentPagerBaseAdapter;
import crazysheep.io.nina.adapter.TimelineAdapter;
import crazysheep.io.nina.bean.TweetDto;
import crazysheep.io.nina.constants.BundleConstants;
import crazysheep.io.nina.net.HttpCache;
import crazysheep.io.nina.net.NiceCallback;
import crazysheep.io.nina.utils.L;
import crazysheep.io.nina.utils.Utils;
import crazysheep.io.nina.widget.swiperefresh.LoadMoreRecyclerView;
import retrofit2.Call;
import retrofit2.Response;

/**
 * profile timeline fragment
 *
 * Created by crazysheep on 16/2/2.
 */
public class ProfileTimelineFragment extends BaseFragment
        implements FragmentPagerBaseAdapter.IPagerFragment, BaseFragment.INetworkFragment,
                   LoadMoreRecyclerView.OnLoadMoreListener {

    private static final int PAGE_SIZE = 21; // tweet count every request
    // tweet count every request we wanted
    private static final int PAGE_SIZE_WANTED = PAGE_SIZE - 1;

    @Bind(R.id.data_rv) LoadMoreRecyclerView mTimelineRv;
    private TimelineAdapter mTimelineAdapter;

    private Call<List<TweetDto>> mTimelineCall;
    private String mScreenName;

    @Override
    public String getTitle(@NonNull Context context) {
        return context.getString(R.string.profile_tab_tweet);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScreenName = getArguments().getString(BundleConstants.EXTRA_USER_SCREEN_NAME);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_profile_timeline, container, false);
        ButterKnife.bind(this, contentView);

        mTimelineAdapter = new TimelineAdapter(getActivity(), null);
        mTimelineRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTimelineRv.setAdapter(mTimelineAdapter);
        mTimelineRv.setOnLoadMoreListener(this);

        return contentView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(Utils.isNull(mTimelineCall) && isVisibleToUser)
            requestFirstPage();
    }

    @Override
    public void onLoadMore() {
        requestNextPage();
    }

    @SuppressWarnings("unchecked")
    public void requestFirstPage() {
        if(!Utils.isNull(mTimelineCall))
            mTimelineCall.cancel();

        mTimelineCall = mTwitter.getUserTimeline(HttpCache.CACHE_IF_HIT, mScreenName,
                PAGE_SIZE, null);
        mTimelineCall.enqueue(new NiceCallback<List<TweetDto>>(getActivity()) {
            @Override
            public void onRespond(Response<List<TweetDto>> response) {
                // if server return tweet count equal PAGE_SIZE,
                // that mean timeline have more tweets
                if (response.body().size() > PAGE_SIZE_WANTED) {
                    mTimelineRv.setLoadMoreEnable(true);
                    //tweetDtos.remove(tweetDtos.size() - 1); // remove lasted tweet
                } else {
                    mTimelineRv.setLoadMoreEnable(false);
                }
                mTimelineAdapter.setData(response.body());
            }

            @Override
            public void onFailed(Throwable t) {

            }
        });
    }

    @SuppressWarnings("unchecked")
    public void requestNextPage() {
        if(!Utils.isNull(mTimelineCall))
            mTimelineCall.cancel();

        long maxId = ((TweetDto)mTimelineAdapter.getItem(mTimelineAdapter.getItemCount() - 1)).id;
        mTimelineCall = mTwitter.getUserTimeline(HttpCache.CACHE_NETWORK, mScreenName, PAGE_SIZE,
                maxId);
        mTimelineCall.enqueue(new NiceCallback<List<TweetDto>>(getActivity()) {
            @Override
            public void onRespond(Response<List<TweetDto>> response) {
                if (response.body().size() > PAGE_SIZE_WANTED)
                    mTimelineRv.setLoadMoreEnable(true);
                else
                    mTimelineRv.setLoadMoreEnable(false);
                response.body().remove(0); // remove maxId tweet
                mTimelineAdapter.addData(response.body());
            }

            @Override
            public void onFailed(Throwable t) {
                L.d(t.toString());
            }
        });
    }

}
