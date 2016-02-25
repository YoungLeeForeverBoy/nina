package crazysheep.io.nina.holder.timeline;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import crazysheep.io.nina.R;
import crazysheep.io.nina.bean.PostTweetBean;
import crazysheep.io.nina.prefs.UserPrefs;
import crazysheep.io.nina.utils.DebugHelper;
import crazysheep.io.nina.utils.DialogUtils;
import crazysheep.io.nina.utils.Utils;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * draft holder
 *
 * Created by crazysheep on 16/2/19.
 */
public class DraftHolder extends BaseHolder<PostTweetBean> implements View.OnClickListener {

    /////////////////// event bus //////////////////////////

    public static class EventRemoveDraft {
        private PostTweetBean postTweetBean;

        public PostTweetBean getPostTweetBean() {
            return this.postTweetBean;
        }

        public EventRemoveDraft(@NonNull PostTweetBean postTweetBean) {
            this.postTweetBean = postTweetBean;
        }
    }

    ////////////////////////////////////////////////////////

    private Context mContext;
    private UserPrefs mUserPrefs;

    @Bind(R.id.author_avatar_iv) CircleImageView avatarIv;
    @Bind(R.id.author_name_tv) TextView authorTv;
    @Bind(R.id.author_screen_name_tv) TextView authorScreenNameTv;
    @Bind(R.id.tweet_content_tv) TextView contentTv;
    @Bind(R.id.draft_remove_iv) ImageView removeIv;
    @Bind(R.id.draft_posting_pb) ProgressBar postingPb;

    private PostTweetBean mPostTweetBean;

    public DraftHolder(@NonNull ViewGroup view) {
        super(view);
        mContext = view.getContext();
        mUserPrefs = new UserPrefs(mContext);
        ButterKnife.bind(this, view);
    }

    @Override
    public void bindData(int position, PostTweetBean postTweetBean) {
        mPostTweetBean = postTweetBean;

        Glide.clear(avatarIv);
        Glide.with(mContext)
                .load(mUserPrefs.getUserAvatar())
                .into(avatarIv);

        authorTv.setText(mUserPrefs.getUsername());
        authorScreenNameTv.setText(
                mContext.getString(R.string.screen_name, mUserPrefs.getUserScreenName()));

        contentTv.setText(postTweetBean.getStatus());

        removeIv.setVisibility(postTweetBean.isFailed() ? View.VISIBLE : View.GONE);
        removeIv.setOnClickListener(this);
        postingPb.setVisibility(postTweetBean.isFailed() ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.draft_remove_iv: {
                showRemoveDraftDialog();
            }break;
        }
    }

    private void showRemoveDraftDialog() {
        DialogUtils.showConfirmDialog(
                (Activity) mContext, null, "remove this draft?",
                new DialogUtils.SimpleButtonAction() {
                    @Override
                    public String getTitle() {
                        return mContext.getString(R.string.ok_btn);
                    }

                    @Override
                    public void onClick(DialogInterface dialog) {
                        super.onClick(dialog);

                        // delete draft from database and update UI
                        // TODO check why post tweet bean id is null int database
                        DebugHelper.log("remove draft, id: " + mPostTweetBean.getId());
                        if(!Utils.isNull(mPostTweetBean.getId())) {
                            mPostTweetBean.delete();
                        }
                        EventBus.getDefault().post(new EventRemoveDraft(mPostTweetBean));
                    }
                },
                new DialogUtils.SimpleButtonAction() {
                    @Override
                    public String getTitle() {
                        return mContext.getString(R.string.cancel_btn);
                    }
                });
    }

}
