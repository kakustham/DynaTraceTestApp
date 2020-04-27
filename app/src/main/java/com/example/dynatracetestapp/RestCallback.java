package com.example.dynatracetestapp;


import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class RestCallback implements Callback {

    private RestRequest restRequest;

    /**
     * Constructor.
     *
     * @param restRequest instance of {@link RestRequest} that holds request and response data.
     */
    public RestCallback(RestRequest restRequest) {
        this.restRequest = restRequest;
    }

    @Override
    public void onResponse(Call call, Response response) {
        if (restRequest != null && restRequest.hasBaseResponseEvent()) {
            restRequest.getBaseResponseEvent().setCode(response.code());
            restRequest.getBaseResponseEvent().setResponse(response.body());
            postEvent();
        }
    }

    @Override
    public void onFailure(Call call, Throwable t) {
        if (restRequest != null && !call.isCanceled()) {
            postEvent();
        }
    }

    /**
     * Post {@link BaseResponseEvent} with flag TRUE that request has been canceled.
     */
    public void cancel() {
        if (restRequest != null && restRequest.hasBaseResponseEvent()) {
            restRequest.getBaseResponseEvent().setCanceled(true);
            postEvent();
        }
    }


    private void postEvent() {
        if (restRequest.isUseStickyIntent()) {
            EventBus.getDefault().postSticky(restRequest.getBaseResponseEvent());
        } else {
            EventBus.getDefault().post(restRequest.getBaseResponseEvent());
        }
    }
}
