package com.websmithing.gpstracker.MyDataApi;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.websmithing.gpstracker.logger.Logger;
//import com.infojaalprime.hrms.interfaces.ApiFetcher;
//import com.infojaalprime.hrms.logger.Logger;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

public class ApiManager
{
    private HashMap mapParameter, mapFile;
    private ApiFetcher apiFetcher;
    private StringBuffer stringBuffer, stringBufferFile;

    public ApiManager(ApiFetcher apiFetcher) {
        this.apiFetcher = apiFetcher;
        mapParameter = new HashMap();
        mapFile = new HashMap();
    }

    public void set_interface_context_post(String[] key, String[] value, String tag, String url) {
        mapParameter.clear();
        stringBuffer = new StringBuffer();
        for (int k = 0; k < key.length; k++) {
            mapParameter.put(key[k], value[k]);
            if (k == 0) {
                stringBuffer = stringBuffer.append(url).append("?").append(key[k]).append("=").append(value[k]);
            } else {
                stringBuffer = stringBuffer.append("&").append(key[k]).append("=").append(value[k]);
            }
        }
        Logger.e(tag + "       " + stringBuffer);
        executionMethodPost(mapParameter, tag, url);
    }

    private void executionMethodPost(HashMap mapParameter, final String tag, String url) {

        apiFetcher.onAPIRunningState(ApiFetcher.KEY_API_IS_RUNNING, tag);
        AndroidNetworking.post(url)
                .addBodyParameter(mapParameter)
                .setTag(this)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Logger.e(tag + "       " + response);
                        apiFetcher.onFetchComplete("" + response, tag);
                        apiFetcher.onAPIRunningState(ApiFetcher.KEY_API_IS_STOPPED, tag);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Logger.e("getErrorBody      " + anError.getErrorBody() +
                                "\ngetErrorDetail        " + anError.getErrorDetail() +
                                "\ngetErrorCode      " + anError.getErrorCode() +
                                "\ngetMessage        " + anError.getMessage() +
                                "\ngetResponse       " + anError.getResponse() +
                                "\ngetLocalizedMessage       " + anError.getLocalizedMessage());

                        apiFetcher.onAPIRunningState(ApiFetcher.KEY_API_IS_STOPPED_WITH_ERROR, tag);
                        apiFetcher.onFetchFailed(anError, tag);
                    }
                });
    }




    public void set_interface_context_get(String tag, String url) {

        Logger.e(tag + "        " + url);
        executionMethodGet(tag, url);
    }

    private void executionMethodGet(final String tag, String url) {
        apiFetcher.onAPIRunningState(ApiFetcher.KEY_API_IS_RUNNING, tag);
        AndroidNetworking.get(url)
                .setTag(this)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Logger.e(tag + "        " + response);
                        apiFetcher.onFetchComplete("" + response, tag);
                        apiFetcher.onAPIRunningState(ApiFetcher.KEY_API_IS_STOPPED, tag);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Logger.e("getErrorBody      " + anError.getErrorBody() +
                                "\ngetErrorDetail        " + anError.getErrorDetail() +
                                "\ngetErrorCode      " + anError.getErrorCode() +
                                "\ngetMessage        " + anError.getMessage() +
                                "\ngetResponse       " + anError.getResponse() +
                                "\ngetLocalizedMessage       " + anError.getLocalizedMessage());

                        apiFetcher.onAPIRunningState(ApiFetcher.KEY_API_IS_STOPPED_WITH_ERROR, tag);
                        apiFetcher.onFetchFailed(anError, tag);
                    }
                });
    }

    public void set_interface_context_upload(String[] key, String[] value, String[] keyFile,
                                             String[] valueFile, String tag, String url) {
        mapParameter.clear();
        mapFile.clear();
        stringBuffer = new StringBuffer();
        stringBufferFile = new StringBuffer();
        for (int k = 0; k < key.length; k++) {
            mapParameter.put(key[k], value[k]);
            if (k == 0) {
                stringBuffer = stringBuffer.append(url).append("?").append(key[k]).append("=").append(value[k]);
            } else {
                stringBuffer = stringBuffer.append("&").append(key[k]).append("=").append(value[k]);
            }
        }
        for (int l = 0; l < keyFile.length; l++) {
            mapFile.put(keyFile[l], new File(valueFile[l]));
            stringBufferFile = stringBufferFile.append("&").append(keyFile[l]).append("=").append(valueFile[l]);
        }
        Logger.e(tag + "       " + stringBuffer + stringBufferFile);
        executionMethodUpload(mapParameter, mapFile, tag, url);
    }

    private void executionMethodUpload(HashMap mapParameter, HashMap mapFile, final String tag, String url) {
        apiFetcher.onAPIRunningState(ApiFetcher.KEY_API_IS_RUNNING, tag);
        AndroidNetworking.upload(url)
                .addMultipartParameter(mapParameter)
                .addMultipartFile(mapFile)
                .setTag(this)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Logger.e(tag + "        " + response);
                        apiFetcher.onAPIRunningState(ApiFetcher.KEY_API_IS_STOPPED, tag);
                        apiFetcher.onFetchComplete("" + response, tag);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Logger.e("getErrorBody      " + anError.getErrorBody() +
                                "\ngetErrorDetail        " + anError.getErrorDetail() +
                                "\ngetErrorCode      " + anError.getErrorCode() +
                                "\ngetMessage        " + anError.getMessage() +
                                "\ngetResponse       " + anError.getResponse() +
                                "\ngetLocalizedMessage       " + anError.getLocalizedMessage());

                        apiFetcher.onAPIRunningState(ApiFetcher.KEY_API_IS_STOPPED_WITH_ERROR, tag);
                        apiFetcher.onFetchFailed(anError, tag);
                    }
                });
    }

    public void set_interface_context_post_get(String[] key, String[] value, String tag, String url) {
        mapParameter.clear();
        stringBuffer = new StringBuffer();
        for (int k = 0; k < key.length; k++) {
            mapParameter.put(key[k], value[k]);
            if (k == 0) {
                stringBuffer = stringBuffer.append(url).append("?").append(key[k]).append("=").append(value[k]);
            } else {
                stringBuffer = stringBuffer.append("&").append(key[k]).append("=").append(value[k]);
            }
        }
        Logger.e(tag + "       " + stringBuffer);
        executionMethodPostGet(mapParameter, tag, url);
    }

    private void executionMethodPostGet(HashMap mapParameter, final String tag, String url) {

        apiFetcher.onAPIRunningState(ApiFetcher.KEY_API_IS_RUNNING, tag);
        AndroidNetworking.get(url)
                .addQueryParameter(mapParameter)
                .setTag(this)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Logger.e(tag + "       " + response);
                        apiFetcher.onFetchComplete("" + response, tag);
                        apiFetcher.onAPIRunningState(ApiFetcher.KEY_API_IS_STOPPED, tag);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Logger.e("getErrorBody      " + anError.getErrorBody() +
                                "\ngetErrorDetail        " + anError.getErrorDetail() +
                                "\ngetErrorCode      " + anError.getErrorCode() +
                                "\ngetMessage        " + anError.getMessage() +
                                "\ngetResponse       " + anError.getResponse() +
                                "\ngetLocalizedMessage       " + anError.getLocalizedMessage());

                        apiFetcher.onAPIRunningState(ApiFetcher.KEY_API_IS_STOPPED_WITH_ERROR, tag);
                        apiFetcher.onFetchFailed(anError, tag);
                    }
                });
    }

    public void set_interface_context_send_json(JSONObject jsonObject, String tag, String url) {

        stringBuffer = new StringBuffer();
        stringBuffer.append(url).append(" ").append(jsonObject);

        Logger.e(tag + "       " + stringBuffer);
        executionMethodSendJson(jsonObject, tag, url);
    }

    private void executionMethodSendJson(JSONObject jsonObject, final String tag, String url) {

        apiFetcher.onAPIRunningState(ApiFetcher.KEY_API_IS_RUNNING, tag);
        AndroidNetworking.post(url)
                .addJSONObjectBody(jsonObject)
                .setTag(this)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Logger.e(tag + "       " + response);
                        apiFetcher.onFetchComplete("" + response, tag);
                        apiFetcher.onAPIRunningState(ApiFetcher.KEY_API_IS_STOPPED, tag);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Logger.e(
                                "getErrorBody      " + anError.getErrorBody() +
                                "\ngetErrorDetail        " + anError.getErrorDetail() +
                                "\ngetErrorCode      " + anError.getErrorCode() +
                                "\ngetMessage        " + anError.getMessage() +
                                "\ngetResponse       " + anError.getResponse() +
                                "\ngetLocalizedMessage       " + anError.getLocalizedMessage());

                        apiFetcher.onAPIRunningState(ApiFetcher.KEY_API_IS_STOPPED_WITH_ERROR, tag);
                        apiFetcher.onFetchFailed(anError, tag);
                    }
                });
    }
}

