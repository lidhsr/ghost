package org.kymjs.kjframe.http.impl;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;
import org.kymjs.kjframe.app.HttpResultData;
import org.kymjs.kjframe.app.MyApplication;
import org.kymjs.kjframe.data.Entity;
import org.kymjs.kjframe.http.httpclient.HttpClient;
import org.kymjs.kjframe.http.httpclient.HttpResponse;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Augustus on 15/10/11.
 */
public class HttpManager {

    public static final String POST_METHOD = "post";
    public static final String GET_METHOD = "get";

    private Context context;

    public HttpManager() {
        this.context = MyApplication.gainContext();
    }

    private HttpClient createClient(Context context) {
        final HttpClient hc = new HttpClient(context);
        hc.setConnectTimeout(4000);
        hc.setReadTimeout(8000);
        return hc;
    }

    /**
     * 解析返回空值的请求.
     *
     * @param url      服务器地址
     * @param params   请求参数
     * @param listener 请求回调
     */
    public void resolveVoid(final String url, final String method, final HashMap<String, String> params, final HttpPostListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpResult<Void> result = new HttpResult<>();
                try {
                    HttpClient hc = createClient(context);

                    HttpResponse resp = null;
                    if(method.equals(POST_METHOD)) {
                        resp = hc.post(url).params(params).execute();
                    } else {
                        resp = hc.get(url).params(params).execute();
                    }

                    JSONObject jsonObject = new JSONObject(resp.asString());
                    result.setRet(jsonObject.optInt(HttpResultData.RET_CODE));
                    if (null != listener) {
                        listener.onResult(resp.getStatusCode(), result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (null != listener) {
                        listener.onResult(-2, result);
                    }
                }
            }
        }).start();
    }

    /**
     * 解析返回实体的请求.
     *
     * @param url      请求地址
     * @param params   请求参数
     * @param builder  实体构造器
     * @param listener 请求回调
     */
    public <T extends Entity> void resolveEntity(final String url, final String method, final HashMap<String, String> params, final Entity.Builder<T> builder, final HttpPostListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpResult<T> result = new HttpResult<>();
                try {
                    HttpClient hc = createClient(context);
                    HttpResponse resp = null;
                    if(method.equals(POST_METHOD)) {
                        resp = hc.post(url).params(params).execute();
                    } else {
                        resp = hc.get(url).params(params).execute();
                    }                    JSONObject jsonObject = new JSONObject(resp.asString());
                    result.setRet(jsonObject.optInt(HttpResultData.RET_CODE));
                    if (jsonObject.optInt(HttpResultData.RET_CODE) == HttpResult.OK) {
                        result.setData(builder.create(jsonObject.optJSONObject(HttpResultData.RET_DATA)));
                    } else {
                        result.setCode(jsonObject.optInt(HttpResultData.RET_CODE));
                    }
                    if (null != listener) {
                        listener.onResult(resp.getStatusCode(), result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (null != listener) {
                        listener.onResult(-2, result);
                    }
                }
            }
        }).start();
    }

    /**
     * 解析返回实体列表的请求.
     *
     * @param url      请求地址
     * @param params   请求参数
     * @param builder  实体构造器W
     * @param listener 请求回调
     */
    public <T extends Entity> void resolveListEntity(final String url, final String method, final HashMap<String, String> params, final Entity.Builder<T> builder, final HttpPostListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpResult<ArrayList<T>> result = new HttpResult<>();
                try {
                    HttpClient hc = createClient(context);
                    HttpResponse resp = null;
                    if(method.equals(POST_METHOD)) {
                        resp = hc.post(url).params(params).execute();
                    } else {
                        resp = hc.get(url).params(params).execute();
                    }
                    JSONObject jsonObject = new JSONObject(resp.asString());
                    result.setRet(jsonObject.optInt(HttpResultData.RET_CODE));
                    if (jsonObject.optInt(HttpResultData.RET_CODE) == HttpResult.OK) {
                        JSONObject retData = jsonObject.optJSONObject(HttpResultData.RET_DATA);
                        result.setLastPage(retData.optBoolean(HttpResultData.RET_LAST_PAGE));
                        result.setFirstPage(retData.optBoolean(HttpResultData.RET_FIRST_PAGE));
                        JSONArray jsonArray = jsonObject.optJSONObject(HttpResultData.RET_DATA).optJSONArray(HttpResultData.RET_LIST);
                        ArrayList<T> data = new ArrayList<T>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject item = jsonArray.optJSONObject(i);
                            if (item != null) {
                                data.add(builder.create(item));
                            }
                        }
                        result.setData(data);
                    } else {
                        result.setCode(jsonObject.optInt(HttpResultData.RET_CODE));
                    }
                    if (null != listener) {
                        listener.onResult(resp.getStatusCode(), result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (null != listener) {
                        listener.onResult(-2, result);
                    }
                }
            }
        }).start();
    }

    /**
     * 解析返回空值的文件上传.
     *
     * @param url      服务器地址
     * @param params   请求参数
     * @param listener 请求回调
     * @param files    上传的文件
     */
    public void resolveVoid(final String url, final String method, final HashMap<String, String> params,
                            final HashMap<String, File> files, final HttpPostListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpResult<Void> result = new HttpResult<>();
                try {
                    HttpClient hc = createClient(context);
                    HttpResponse resp = null;
                    if(method.equals(POST_METHOD)) {
                        resp = hc.post(url).params(params).execute(files);
                    } else {
                        resp = hc.get(url).params(params).execute(files);
                    }
                    JSONObject jsonObject = new JSONObject(new String(resp.asString()));
                    result.setRet(jsonObject.optInt(HttpResultData.RET_CODE));
                    if (null != listener) {
                        listener.onResult(resp.getStatusCode(), result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (null != listener) {
                        listener.onResult(-2, result);
                    }
                }
            }
        }).start();
    }
}
