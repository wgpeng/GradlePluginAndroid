package ytc.wp.costomdep.net;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.atomic.AtomicReference;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import ytc.wp.costomdep.Lg;

/**
 * Author: wangpeng
 * Date: 2017/8/11 16:18
 */
public class SSLContextUtil {
    private final static String protocol = "TLS";
    private static AtomicReference<SSLContext> atomicSSLContext = new AtomicReference<>(null);
    private static HostnameVerifier sHostnameVerifier = null;

    public static SSLContext getSSLContext(){
        if(atomicSSLContext.get() !=null) return atomicSSLContext.get();
        SSLContext sslContext = getSSLContext(protocol);
        initSSLContext(sslContext);
        atomicSSLContext.compareAndSet(null,sslContext);
        return atomicSSLContext.get();
    }

    private static void initSSLContext(SSLContext sslContext){
        if(sslContext == null) return;
        try {
            sslContext.init(null,new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }},new SecureRandom());
        } catch (KeyManagementException e) {
            Lg.error(e);
        }
    }

    private static SSLContext getSSLContext(String protocol){
        try {
            return SSLContext.getInstance(protocol);
        } catch (NoSuchAlgorithmException e) {
            Lg.error(e);
        }
        return null;
    }

    public static HostnameVerifier getHostnameVerifie(){
        if(sHostnameVerifier != null) return sHostnameVerifier;
        sHostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                //固定接口，直接通过
                return true;
            }
        };

        return sHostnameVerifier;
    }

}
