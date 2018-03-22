package ytc.wp.costomdep.net;

/**
 * Author: wangpeng
 * Date: 2017/6/16 13:34
 */

/**
 *  响应体
 *  code 为响应码,可以是HTTP响应码，也可以是自定义响应码
 *  responBody 响应内容， 使用泛型， 可是String，也可以是Object,甚至是一个输出流
 * @param <T>
 */
public class ResponseBody<T> {
    private int code;
    private T responBody;

    public ResponseBody(int code, T responBody) {
        this.code = code;
        this.responBody = responBody;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getResponBody() {
        return responBody;
    }

    public void setResponBody(T responBody) {
        this.responBody = responBody;
    }

    public  static <T>ResponseBody creatResponseBody(int code,T responBody){
      return new ResponseBody(code,responBody);
    }
}
