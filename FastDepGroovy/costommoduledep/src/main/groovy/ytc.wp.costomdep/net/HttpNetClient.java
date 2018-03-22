package ytc.wp.costomdep.net;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import ytc.wp.costomdep.Lg;
import ytc.wp.costomdep.TextUtils;

/**
 * Author: wangpeng
 * Date: 2017/6/15 20:00
 */
public abstract class HttpNetClient {
    private static int TIMEOUT_TIME = 10000;
    public Map<String, String> httpHeadsProperty = new HashMap<>();
    protected BasicHttpParam mPostParam;
    protected String url;

    public HttpNetClient(String url) {
        this.url = url;
    }

    public ResponseBody post(BasicHttpParam httpParam) {
        return post(httpParam, -1);
    }

    public ResponseBody post(final BasicHttpParam httpParams, final int netOutTIme) {
        this.mPostParam = httpParams;
        final String postUrl = this.url;
       /* int paramLength = 0;
        if (httpParams != null && !httpParams.params) {
            paramLength = httpParams.params.toString().getBytes().length;
        }*/
//        setHttpHead("Content-Length", String.valueOf(paramLength));
        // 2.创建连接
        ResponseBody responseBody = creatConnection(postUrl);
        // 创建失败
        if (responseBody.getCode() != CONNECTION_SUCCESS) return responseBody;
        HttpURLConnection httpUrlConnection = (HttpURLConnection) responseBody.getResponBody();
        if (httpUrlConnection != null) try {
            httpUrlConnection.setRequestMethod("POST");
        } catch (ProtocolException e) {
            Lg.error(e);
            return ResponseBody.creatResponseBody(NetError.NET_CONNECTION_ERROR, " POST set Exception");
        }
        // 3、执行
        return execRequest(httpUrlConnection, httpParams, netOutTIme);
    }

    public ResponseBody get(BasicHttpParam httpParam) {
        return get(httpParam, -1);
    }

    public ResponseBody get(final BasicHttpParam httpParam, final int netOutTime) {
        ResponseBody responseBody = null;
        // 1.拼接参数
        final String getUrl;
        if (httpParam != null && !TextUtils.isEmpty(httpParam.params.toString())) {
            getUrl = this.url + "?" + httpParam.params;
        } else {
            getUrl = this.url;
        }
        // 2.创建连接
        responseBody = creatConnection(getUrl);
        if (responseBody.getCode() != CONNECTION_SUCCESS) return responseBody;
        HttpURLConnection httpUrlConnection = (HttpURLConnection) responseBody.getResponBody();
        if (httpUrlConnection == null) return responseBody;
        // 3 . 执行
        responseBody = execRequest(httpUrlConnection, httpParam, netOutTime);
        return responseBody;
    }


    protected ResponseBody execRequest(HttpURLConnection httpUrlConnection, BasicHttpParam httpParams, int netOutTIme) {
        ResponseBody responseBody = null;

        // 1.设置超时
        if (netOutTIme <= 0) {
            httpUrlConnection.setConnectTimeout(TIMEOUT_TIME);//连接超时 单位毫秒
            // httpURLConnection.setReadTimeout(2000);//读取超时 单位毫秒
        }

        //2. 设置请求头
        initHttpHead(httpUrlConnection);

        // 设置https
        if (httpUrlConnection instanceof HttpsURLConnection) { // 是Https请求
            SSLContext sslContext = SSLContextUtil.getSSLContext();
            if (sslContext != null) {
                SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
                ((HttpsURLConnection) httpUrlConnection).setSSLSocketFactory(sslSocketFactory);
                ((HttpsURLConnection) httpUrlConnection).setHostnameVerifier(SSLContextUtil.getHostnameVerifie());
            }
        }

        // 3.如果是post请求发送参数
        if (httpUrlConnection.getRequestMethod() == "POST") {
            httpUrlConnection.setRequestProperty("connection", "Keep-Alive");
            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.setDoInput(true);
            httpUrlConnection.setUseCaches(false);
            // 4. 发起请求
            Lg.info(httpParams.getParamString());
            responseBody = sendRequest(httpUrlConnection, httpParams);
        }
        // 判断请求是否成功,如responseBody请求体已经有值,则说明sendRequest有异常
        if (responseBody != null) return responseBody;

        // 5. 处理请求响应
        responseBody = respondHandler(httpUrlConnection);
        httpUrlConnection.disconnect();
        return responseBody;
    }


    private void initHttpHead(HttpURLConnection httpURLConnection) {
        // 设置通用的请求属性
        httpURLConnection.setRequestProperty("accept", "*/*");
        httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
        httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        if (httpHeadsProperty != null && httpHeadsProperty.size() > 0) {
            for (String name : httpHeadsProperty.keySet()) {
                String property = httpHeadsProperty.get(name);
                httpURLConnection.setRequestProperty(name, property);
            }
        }
    }


    public HttpNetClient setHttpHead(String key, String value) {
        httpHeadsProperty.put(key, value);
        return this;
    }

    private final static int CONNECTION_SUCCESS = 100000000;

    protected ResponseBody creatConnection(String httpUrl) {
        if(!TextUtils.isEmpty(httpUrl)){
            Lg.info("url: " + httpUrl);
        }
        ResponseBody responseBody = null;

        URL url;
        HttpURLConnection httpURLConnection = null;
        try {
            url = new URL(httpUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            responseBody = ResponseBody.creatResponseBody(NetError.NET_ILLEGAL_ERROR, httpUrl + " 路径不合法");
            Lg.error(e);
        } catch (IOException e) {
            responseBody = ResponseBody.creatResponseBody(NetError.NET_CONNECTION_ERROR, e.getMessage());
            Lg.error(e);
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        responseBody = ResponseBody.creatResponseBody(CONNECTION_SUCCESS, httpURLConnection);

        return responseBody;
    }

    protected ResponseBody sendRequest(HttpURLConnection httpURLConnection, BasicHttpParam httpPostParam) {
        // 获取URLConnection对象对应的输出流
        ResponseBody responseBody = null;
        OutputStream outputStream = null;
        try {
            outputStream = httpURLConnection.getOutputStream();
           // if(httpPostParam!=null&&!TextUtils.isEmpty(httpPostParam.getParamString())) outputStream.
            request(outputStream);
        } catch (SocketTimeoutException e) {
            responseBody = ResponseBody.creatResponseBody(NetError.NETWORK_TIMEOUT, "发送请求超时" + e.getMessage());
            return responseBody;
        } catch (Exception e) {
            responseBody = ResponseBody.creatResponseBody(NetError.NET_REUEST_ERROR, "发送请求异常:  "+e.getMessage());
            Lg.error(e.toString());
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    Lg.error("请求输出流关闭 :  "+e);
                }
            }
        }
        return responseBody;
    }

    protected abstract void request(OutputStream outputStream) throws IOException;

    protected ResponseBody respondHandler(HttpURLConnection httpURLConnection) {
        // 根据ResponseCode判断连接是否成功
        ResponseBody responseBody = null;
        String responseMessage = "";
        InputStream inputStream = null;
        try {
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                responseMessage = httpURLConnection.getResponseMessage();
                inputStream = httpURLConnection.getInputStream();
                responseBody = respond(responseCode, responseMessage, inputStream);
                if(responseBody==null) responseBody =ResponseBody.creatResponseBody(HttpURLConnection.HTTP_OK, null);
                responseBody.setCode(HttpURLConnection.HTTP_OK);
                Lg.error("请求成功:" + httpURLConnection.getURL().getPath() + "=====>" + responseBody.getResponBody());
            } else {
                responseBody = ResponseBody.creatResponseBody(responseCode, responseMessage);
                Lg.error("请求失败:" + httpURLConnection.getURL().getPath() + "\n" + responseCode + "  " + responseMessage);
            }

        } catch (SocketTimeoutException e) {
            responseBody = ResponseBody.creatResponseBody(NetError.NETWORK_TIMEOUT, "响应超时" + e.getMessage());
            Lg.error(e.getMessage());
        } catch (Exception e) {
            responseBody = ResponseBody.creatResponseBody(NetError.NET_RESPOND_ERROR, "响应异常" + e.getMessage());
            Lg.error(e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Lg.error("响应input流关闭异常:  "+e.toString());
                }
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        return responseBody;
    }

    protected abstract ResponseBody respond(int responseCode, String responseMessage, InputStream inputStream) throws Exception;

   /* protected static void disableConnectionReuseIfNecessary() {
        // 这是一个2.2版本之前的bug
        if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO) {

            System.setProperty("http.keepAlive", "false");

        }
    }*/

    /**
     * 使用post 的方式进行数据的请求
     *
     * @param strUrlPath 请求数据路径
     * @param params     请求体参数
     * @param encode     编码格式
     * @return 返回String
     */
    public static String submitPostData(String strUrlPath, Map<String, String> params, String encode) {

        byte[] data = getRequestData(params, encode).toString().getBytes();//获得请求体
        try {
            URL url = new URL(strUrlPath);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(3000);     //设置连接超时时间
            httpURLConnection.setDoInput(true);                  //打开输入流，以便从服务器获取数据
            httpURLConnection.setDoOutput(true);                 //打开输出流，以便向服务器提交数据
            httpURLConnection.setRequestMethod("POST");     //设置以Post方式提交数据
            httpURLConnection.setUseCaches(false);               //使用Post方式不能使用缓存
            //设置请求体的类型是文本类型
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //设置请求体的长度
            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
            //获得输出流，向服务器写入数据
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(data);

            int response = httpURLConnection.getResponseCode();            //获得服务器的响应码
            if (response == HttpURLConnection.HTTP_OK) {
                InputStream inptStream = httpURLConnection.getInputStream();
                return dealResponseResult(inptStream);                     //处理服务器的响应结果
            }
        } catch (IOException e) {
            //e.printStackTrace();
            return "err: " + e.getMessage().toString();
        }
        return "-1";
    }

    /**
     * 封装请求体信息
     *
     * @param params 请求体 参数
     * @param encode 编码格式
     * @return 返回封装好的StringBuffer
     */
    public static StringBuffer getRequestData(Map<String, String> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer();        //存储封装好的请求体信息
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), encode))
                        .append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);    //删除最后的一个"&"
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }

    /**
     * 处理服务器返回结果
     *
     * @param inputStream 输入流
     * @return 返回处理后的String 字符串
     */
    public static String dealResponseResult(InputStream inputStream) {
        String resultData = null;      //存储处理结果
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        try {
            while ((len = inputStream.read(data)) != -1) {
                byteArrayOutputStream.write(data, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultData = new String(byteArrayOutputStream.toByteArray());
        return resultData;
    }

    public String getUrl() {
        return url;
    }

    public BasicHttpParam getPostParam() {
        return mPostParam;
    }

    public static void setTimeoutTime(int timeoutTime) {
        TIMEOUT_TIME = timeoutTime;

    }
}


