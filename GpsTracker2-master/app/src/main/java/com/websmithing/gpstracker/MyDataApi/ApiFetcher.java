package com.websmithing.gpstracker.MyDataApi;

import com.androidnetworking.error.ANError;

;

public interface ApiFetcher {

    int KEY_API_IS_RUNNING = 1;
    int KEY_API_IS_STOPPED = 2;
    int KEY_API_IS_STOPPED_WITH_ERROR = 3;
    int KEY_API_IS_STOPPED_DELAY = 4;

    void onAPIRunningState(int a, String apiName);

    void onFetchProgress(int progress);

    void onFetchComplete(String response, String apiName);

    void onFetchFailed(ANError error, String apiName);
}