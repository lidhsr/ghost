package org.kymjs.kjframe.http.impl;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;
import org.kymjs.kjframe.app.HttpResultData;
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

    private Context context;

    public HttpManager(Context context) {
        this.context = context;
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
    public void resolveVoid(final String url, final HashMap<String, String> params, final HttpPostListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpResult<Void> result = new HttpResult<>();
                try {
                    HttpClient hc = createClient(context);
                    HttpResponse resp = hc.post(url).params(params).execute();
                    JSONObject jsonObject = new JSONObject(new String(resp.asString()));
                    result.setRet(jsonObject.optInt(HttpResultData.RET_CODE));
                    if (null != listener) {
                        listener.onResult(resp.getStatusCode(), result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
    public <T extends Entity> void resolveEntity(final String url, final HashMap<String, String> params, final Entity.Builder<T> builder, final HttpPostListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpResult<T> result = new HttpResult<>();
                try {
                    HttpClient hc = createClient(context);
                    HttpResponse resp = hc.post(url).params(params).execute();
                    JSONObject jsonObject = new JSONObject(new String(resp.asString()));
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
    public <T extends Entity> void resolveListEntity(final String url, final HashMap<String, String> params, final Entity.Builder<T> builder, final HttpPostListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpResult<ArrayList<T>> result = new HttpResult<>();
                try {
                    HttpClient hc = createClient(context);
                    HttpResponse resp = hc.post(url).params(params).execute();
                    JSONObject jsonObject = new JSONObject(new String(resp.asString()));
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
    public void resolveVoid(final String url, final HashMap<String, String> params,
                            final HashMap<String, File> files, final HttpPostListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpResult<Void> result = new HttpResult<>();
                try {
                    HttpClient hc = createClient(context);
                    HttpResponse resp = hc.post(url).params(params).execute(files);
                    JSONObject jsonObject = new JSONObject(new String(resp.asString()));
                    result.setRet(jsonObject.optInt(HttpResultData.RET_CODE));
                    if (null != listener) {
                        listener.onResult(resp.getStatusCode(), result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //    /**
//     * 解析返回实体列表的请求.
//     * @param url 请求地址
//     * @param params 请求参数
//     * @param builder 实体构造器W
//     * @param listener 请求回调
//     */
//    protected <T extends Entity> void resolveListEntity(String url, HttpParams params, final Entity.Builder<T> builder, final HttpPostListener listener) {
//        final HttpResult<ArrayList<T>> result = new HttpResult<>();
//        if(null == params) {
//            params = new HttpParams();
//        }
//        KJHttp kjh = new KJHttp();
//        kjh.post(url, params,
//                new HttpCallBack() {
//                    @Override
//                    public void onSuccess(Map<String, String> headers, byte[] t) {
//                        super.onSuccess(headers, t);
//                        try {
//                            JSONObject jsonObject = new JSONObject(new String(t));
//                            Log.e("msg", "json:" + jsonObject.toString());
//                            result.setRet(jsonObject.optInt(HttpResultData.RET_CODE));
//                            if (jsonObject.optInt(HttpResultData.RET_CODE) == HttpResult.OK) {
//                                JSONObject retData = jsonObject.optJSONObject(HttpResultData.RET_DATA);
//                                result.setLastPage(retData.optBoolean(HttpResultData.RET_LAST_PAGE));
//                                result.setFirstPage(retData.optBoolean(HttpResultData.RET_FIRST_PAGE));
//                                JSONArray jsonArray = jsonObject.optJSONObject(HttpResultData.RET_DATA).optJSONArray(HttpResultData.RET_LIST);
//                                ArrayList<T> data = new ArrayList<T>();
//                                for (int i = 0; i < jsonArray.length(); i++) {
//                                    JSONObject item = jsonArray.optJSONObject(i);
//                                    if (item != null) {
//                                        data.add(builder.create(item));
//                                    }
//                                }
//                                result.setData(data);
//                            } else {
//                                result.setCode(jsonObject.optInt(HttpResultData.RET_CODE));
//                            }
//                            if(null != listener) {
//                                listener.onResult(result);
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            result.setException(e);
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(int errorNo, String strMsg) {
//                        super.onFailure(errorNo, strMsg);
//                        result.setCode(errorNo);
//                        result.setMsg(strMsg);
//                        if(null != listener) {
//                            listener.onResult(result);
//                        }
//                    }
//                });
//    }

//    /**
//     * 解析返回空值的请求.
//     * @param url 服务器地址
//     * @param params 请求参数
//     * @param listener 请求回调
//     */
//    public void resolveVoid(String url, HttpParams params, final HttpPostListener listener) {
//        final HttpResult<Void> result = new HttpResult<Void>();
//        if(null == params) {
//            params = new HttpParams();
//        }
//        KJHttp kjh = new KJHttp();
//        kjh.post(url, params,
//                new HttpCallBack() {
//                    @Override
//                    public void onSuccess(Map<String, String> headers, byte[] t) {
//                        super.onSuccess(headers, t);
//                        try {
//                            JSONObject jsonObject = new JSONObject(new String(t));
//                            Log.e("msg", "json:" + jsonObject.toString());
//                            result.setRet(jsonObject.optInt(HttpResultData.RET_CODE));
//                            if (jsonObject.optInt(HttpResultData.RET_CODE) == HttpResult.OK) {
//                                result.setRet(HttpResult.OK);
//                            } else {
//                                result.setCode(jsonObject.optInt(HttpResultData.RET_CODE));
//                            }
//                            result.setMsg(jsonObject.optString(HttpResultData.RET_DATA));
//                            if(null != listener) {
//                                listener.onResult(result);
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            result.setException(e);
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(int errorNo, String strMsg) {
//                        super.onFailure(errorNo, strMsg);
//                        result.setCode(errorNo);
//                        result.setMsg(strMsg);
//                        Log.e("msg", strMsg + ", onFailure..." + errorNo);
//                        if(null != listener) {
//                            listener.onResult(result);
//                        }
//                    }
//                });
//    }
//
//    /**
//     * 解析返回实体的请求.
//     * @param url 请求地址
//     * @param params 请求参数
//     * @param builder 实体构造器
//     * @param listener 请求回调
//     */
//    protected <T extends Entity> void resolveEntity(String url, HttpParams params, final Entity.Builder<T> builder, final HttpPostListener listener) {
//        final HttpResult<T> result = new HttpResult<T>();
//        if(null == params) {
//            params = new HttpParams();
//        }
//        KJHttp kjh = new KJHttp();
//        kjh.post(url, params,
//                new HttpCallBack() {
//                    @Override
//                    public void onSuccess(Map<String, String> headers, byte[] t) {
//                        super.onSuccess(headers, t);
//                        try {
//                            JSONObject jsonObject = new JSONObject(new String(t));
//                            Log.e("msg", "json:" + jsonObject.toString());
//                            result.setRet(jsonObject.optInt(HttpResultData.RET_CODE));
//                            if (jsonObject.optInt(HttpResultData.RET_CODE) == HttpResult.OK) {
//                                result.setData(builder.create(jsonObject.optJSONObject(HttpResultData.RET_DATA)));
//                            } else {
//                                result.setCode(jsonObject.optInt(HttpResultData.RET_CODE));
//                            }
//                            if(null != listener) {
//                                listener.onResult(result);
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            result.setException(e);
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(int errorNo, String strMsg) {
//                        super.onFailure(errorNo, strMsg);
//                        result.setCode(errorNo);
//                        result.setMsg(strMsg);
//                        if(null != listener) {
//                            listener.onResult(result);
//                        }
//                    }
//                });
//    }
//
//    /**
//     * 解析返回实体列表的请求.
//     * @param url 请求地址
//     * @param params 请求参数
//     * @param builder 实体构造器W
//     * @param listener 请求回调
//     */
//    protected <T extends Entity> void resolveListEntity(String url, HttpParams params, final Entity.Builder<T> builder, final HttpPostListener listener) {
//        final HttpResult<ArrayList<T>> result = new HttpResult<>();
//        if(null == params) {
//            params = new HttpParams();
//        }
//        KJHttp kjh = new KJHttp();
//        kjh.post(url, params,
//                new HttpCallBack() {
//                    @Override
//                    public void onSuccess(Map<String, String> headers, byte[] t) {
//                        super.onSuccess(headers, t);
//                        try {
//                            JSONObject jsonObject = new JSONObject(new String(t));
//                            Log.e("msg", "json:" + jsonObject.toString());
//                            result.setRet(jsonObject.optInt(HttpResultData.RET_CODE));
//                            if (jsonObject.optInt(HttpResultData.RET_CODE) == HttpResult.OK) {
//                                JSONObject retData = jsonObject.optJSONObject(HttpResultData.RET_DATA);
//                                result.setLastPage(retData.optBoolean(HttpResultData.RET_LAST_PAGE));
//                                result.setFirstPage(retData.optBoolean(HttpResultData.RET_FIRST_PAGE));
//                                JSONArray jsonArray = jsonObject.optJSONObject(HttpResultData.RET_DATA).optJSONArray(HttpResultData.RET_LIST);
//                                ArrayList<T> data = new ArrayList<T>();
//                                for (int i = 0; i < jsonArray.length(); i++) {
//                                    JSONObject item = jsonArray.optJSONObject(i);
//                                    if (item != null) {
//                                        data.add(builder.create(item));
//                                    }
//                                }
//                                result.setData(data);
//                            } else {
//                                result.setCode(jsonObject.optInt(HttpResultData.RET_CODE));
//                            }
//                            if(null != listener) {
//                                listener.onResult(result);
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            result.setException(e);
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(int errorNo, String strMsg) {
//                        super.onFailure(errorNo, strMsg);
//                        result.setCode(errorNo);
//                        result.setMsg(strMsg);
//                        if(null != listener) {
//                            listener.onResult(result);
//                        }
//                    }
//                });
//    }

}
