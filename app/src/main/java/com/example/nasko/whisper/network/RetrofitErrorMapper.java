package com.example.nasko.whisper.network;


import android.content.Context;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.network.RequestFailedException;

import java.io.IOException;

import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.functions.Func1;

public class RetrofitErrorMapper<T> implements Func1<Throwable, Observable<T>> {

    private Context context;

    public RetrofitErrorMapper(Context context) {
        this.context = context;
    }

    @Override
    public Observable<T> call(Throwable throwable) {
        String errorMsg = null;
        if (throwable instanceof HttpException) {
            Response<?> response = ((HttpException) throwable).response();
            if (response != null) {
                try {
                    errorMsg = response.errorBody().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (throwable instanceof IOException) {
            errorMsg = context.getString(R.string.message_timeout);
        }

        if (errorMsg == null) {
            errorMsg = context.getString(R.string.message_generic_bad_request);
        }

        return Observable.error(new RequestFailedException(errorMsg, throwable));
    }
}
