package crazysheep.io.nina.dagger2.module;

import android.content.Context;
import android.support.annotation.NonNull;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import javax.inject.Singleton;

import crazysheep.io.nina.application.BaseApplication;
import dagger.Module;
import dagger.Provides;

/**
 * application module
 *
 * Created by crazysheep on 16/3/9.
 */
@Module
public class ApplicationModule {

    private BaseApplication mApplication;

    public ApplicationModule(@NonNull BaseApplication application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    public Context provideApplication() {
        return mApplication;
    }

    @Provides
    @Singleton
    public RefWatcher provideRefWatcher() {
        return false ? LeakCanary.install(mApplication) : RefWatcher.DISABLED;
    }

}
