package crazysheep.io.nina.net;

import crazysheep.io.nina.utils.Utils;
import retrofit2.Call;
import retrofit2.Response;

/**
 * nice callback implement {@link retrofit2.Callback}
 *
 * Created by crazysheep on 16/1/27.
 */
public abstract class NiceCallback<T> implements retrofit2.Callback<T> {

    /////////////////////// empty nice callback ////////////////////////
    public static class EmptyNiceCallback<T> extends NiceCallback<T> {
        @Override
        public void onFailed(Throwable t) {
        }

        @Override
        public void onRespond(Response<T> response) {
        }
    }
    ////////////////////////////////////////////////////////////////////

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if(response.code() == HttpConstants.CODE_200)
            onRespond(response);
        else
            onFailed(new Throwable("request failed, status code: " + response.code()
                    + ", response header: " + response.headers().toString()
                    + ", response body: " + (Utils.isNull(response.body())
                    ? null : response.body().toString())));
        onDone();
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        onFailed(t);
        onDone();
    }

    public abstract void onRespond(Response<T> response);

    public abstract void onFailed(Throwable t);

    public void onDone() {}

}
