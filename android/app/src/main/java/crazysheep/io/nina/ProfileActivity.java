package crazysheep.io.nina;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import crazysheep.io.nina.adapter.FragmentPagerBaseAdapter;
import crazysheep.io.nina.adapter.ProfilePagerAdapter;
import crazysheep.io.nina.bean.UserDto;
import crazysheep.io.nina.compat.APICompat;
import crazysheep.io.nina.constants.BundleConstants;
import crazysheep.io.nina.fragment.ProfileLikeFragment;
import crazysheep.io.nina.fragment.ProfileMediaFragment;
import crazysheep.io.nina.fragment.ProfileTimelineFragment;
import crazysheep.io.nina.net.NiceCallback;
import crazysheep.io.nina.prefs.UserPrefs;
import crazysheep.io.nina.utils.ActivityUtils;
import crazysheep.io.nina.utils.FabUtils;
import crazysheep.io.nina.utils.L;
import crazysheep.io.nina.utils.StringUtils;
import crazysheep.io.nina.utils.Utils;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Response;

/**
 * user profile activity
 *
 * Created by crazysheep on 16/2/2.
 */
public class ProfileActivity extends BaseSwipeBackActivity
        implements BaseActivity.ITwitterServiceActivity, AppBarLayout.OnOffsetChangedListener {

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.appbar) AppBarLayout mAppbar;
    @Bind(R.id.collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbar;
    @Bind(R.id.action_fab) FloatingActionButton mFab;
    @Bind(R.id.tabs) TabLayout mTabLayout;
    @Bind(R.id.content_vp) ViewPager mContentVp;
    // header
    @Bind(R.id.parallax_header_iv) ImageView mHeaderIv;
    @Bind(R.id.author_avatar_iv) CircleImageView mUserAvatar;
    @Bind(R.id.user_name_tv) TextView mUserNameTv;
    @Bind(R.id.user_screen_name_tv) TextView mUserScreenNameTv;
    @Bind(R.id.user_introduction_tv) TextView mUserIntroductionTv;
    @Bind(R.id.user_location_tv) TextView mUserLocationTv;
    @Bind(R.id.following_tv) TextView mFollowingTv;
    @Bind(R.id.follower_tv) TextView mFollowerTv;

    private String mUserName;
    private String mScreenName;

    private Call<UserDto> mUserCall;
    private UserDto mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // if api >= 23, I want have a light style status bar, so disable translucent status bar
        if(APICompat.api23())
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        mUserName = getIntent().getStringExtra(BundleConstants.EXTRA_USER_NAME);
        mScreenName = getIntent().getStringExtra(BundleConstants.EXTRA_USER_SCREEN_NAME);
        initUI();
        requestUser(mScreenName);
    }

    @SuppressWarnings("unchecked")
    private void initUI() {
        mAppbar.addOnOffsetChangedListener(this);
        setSupportActionBar(mToolbar);
        if(!Utils.isNull(getSupportActionBar())) {
            getSupportActionBar().setTitle(
                    !TextUtils.isEmpty(mUserName) ? mUserName : getString(R.string.profile_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        }

        List<FragmentPagerBaseAdapter.IPagerFragment> fragments = new ArrayList<>();
        Bundle bundle = new Bundle();
        bundle.putString(BundleConstants.EXTRA_USER_SCREEN_NAME, mScreenName);
        fragments.add(
                ActivityUtils.newFragment(getActivity(), ProfileTimelineFragment.class, bundle));
        fragments.add(ActivityUtils.newFragment(getActivity(), ProfileMediaFragment.class, bundle));
        fragments.add(ActivityUtils.newFragment(getActivity(), ProfileLikeFragment.class, bundle));
        mContentVp.setAdapter(
                new ProfilePagerAdapter(this, getSupportFragmentManager(), fragments));
        mContentVp.setOffscreenPageLimit(2);
        mContentVp.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setupWithViewPager(mContentVp);

        // if this profile is my own, let fab gone
        if(new UserPrefs(this).getUserScreenName().equals(mScreenName))
            FabUtils.gone(mFab);
    }

    /*
    * for handling appbar collapsing or expanding, see{@link https://github.com/saulmm/CoordinatorBehaviorExample}
    * */
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

        mCollapsingToolbar.setTitle(percentage >= 1f ? mUserName : null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void requestUser(String screenName) {
        if(!Utils.isNull(mUserCall))
            mUserCall.cancel();

        mUserCall = mTwitter.getUserInfo(screenName);
        mUserCall.enqueue(new NiceCallback<UserDto>(this) {
            @Override
            public void onRespond(Response<UserDto> response) {
                updateUserUI(response.body());
            }

            @Override
            public void onFailed(Throwable t) {
                L.d(t.toString());
            }
        });
    }

    private void updateUserUI(UserDto user) {
        if(!Utils.isNull(user)) {
            mUser = user;

            Glide.clear(mUserAvatar);
            Glide.with(this)
                    .load(user.originalProfileImageUrlHttps())
                    .into(mUserAvatar);

            Glide.clear(mHeaderIv);
            Glide.with(this)
                    .load(user.profile_background_image_url_https)
                    .asBitmap()
                    .error(R.color.colorPrimary)
                    .listener(new RequestListener<String, Bitmap>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<Bitmap> target,
                                                   boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(
                                Bitmap resource, String model, Target<Bitmap> target,
                                boolean isFromMemoryCache, boolean isFirstResource) {
                            if(APICompat.api23())
                                colorizeStatusBar(resource);

                            return false;
                        }
                    })
                    .into(mHeaderIv);

            mFab.setImageResource(user.following
                    ? R.drawable.ic_following_white_24dp : R.drawable.ic_unfollow_white_24dp);

            mUserNameTv.setText(user.name);
            mUserScreenNameTv.setText(getString(R.string.screen_name, user.screen_name));
            mUserIntroductionTv.setText(user.description);
            if(!TextUtils.isEmpty(user.location)) {
                mUserLocationTv.setText(user.location);
                TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(mUserLocationTv,
                        R.drawable.ic_location_grey_24dp, 0, 0, 0);
            } else {
                mUserLocationTv.setVisibility(View.GONE);
            }
            mFollowerTv.setText(
                    getString(user.followers_count <= 1
                                    ? R.string.profile_follower : R.string.profile_follower_s,
                            StringUtils.formatCount(user.followers_count)));
            mFollowingTv.setText(
                    getString(R.string.profile_following,
                            StringUtils.formatCount(user.friends_count)));
        }
    }

    @TargetApi(APICompat.M)
    private void colorizeStatusBar(Bitmap bitmap) {
        Palette.from(bitmap)
                .generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        Palette.Swatch maxSwatch = Collections.max(palette.getSwatches(),
                                new Comparator<Palette.Swatch>() {
                                    @Override
                                    public int compare(Palette.Swatch lhs, Palette.Swatch rhs) {
                                        return lhs.getPopulation() - rhs.getPopulation();
                                    }
                                });
                        if(ColorUtils.calculateContrast(Color.WHITE, maxSwatch.getRgb()) <= 5d)
                            getWindow().getDecorView().setSystemUiVisibility(
                                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                            | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    }
                });
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.action_fab)
    protected void clickFollowFab() {
        if(!Utils.isNull(mUser)) {
            if(mUser.following)
                mTwitter.unfollow(mUser.screen_name).enqueue(new NiceCallback<UserDto>(this) {
                    @Override
                    public void onRespond(Response<UserDto> response) {
                        Snackbar.make(mContentVp,
                                getString(R.string.unfollow_successful, mUser.screen_name),
                                Snackbar.LENGTH_LONG)
                                .show();
                    }

                    @Override
                    public void onFailed(Throwable t) {
                        Snackbar.make(mContentVp,
                                getString(R.string.unfollow_failed, mUser.screen_name),
                                Snackbar.LENGTH_LONG).show();
                    }
                });
            else
                mTwitter.follow(mUser.screen_name).enqueue(new NiceCallback<UserDto>(this) {
                    @Override
                    public void onRespond(Response<UserDto> response) {
                        Snackbar.make(mContentVp,
                                getString(R.string.follow_successful, mUser.screen_name),
                                Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailed(Throwable t) {
                        Snackbar.make(mContentVp,
                                getString(R.string.follow_failed, mUser.screen_name),
                                Snackbar.LENGTH_LONG).show();
                    }
                });

            mUser.following = !mUser.following;
            mFab.setImageResource(mUser.following
                    ? R.drawable.ic_following_white_24dp : R.drawable.ic_unfollow_white_24dp);
        }
    }

}
