package ytc.wp.costomdep.net;


/**
 * Author: wangpeng
 * Date: 2017/6/16 10:42
 */
public abstract class BasicHttpParam {
    protected StringBuffer params = new StringBuffer();

    public abstract BasicHttpParam setParams(String key, String value);
    public abstract String getParamString();

}
