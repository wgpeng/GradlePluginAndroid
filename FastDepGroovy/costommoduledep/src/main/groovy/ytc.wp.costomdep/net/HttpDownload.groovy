package ytc.wp.costomdep.net

import ytc.wp.costomdep.Lg

/**
 * Created by wangpeng on 17-10-11.
 */
public class HttpDownload extends HttpNetClient {
    String filePath;

    HttpDownload(String url, String filePath) {
        super(url)
        this.filePath = filePath
    }

    @Override
    protected void request(OutputStream outputStream) throws IOException {

    }

    @Override
    protected ResponseBody respond(int responseCode, String responseMessage, InputStream inputStream) throws Exception {
       // BufferedInputStream bufis =  new BufferedInputStream(inputStream)
       // Lg.error bufis.text
       // new ByteArrayInputStream(inputStream).
         Lg. error " 响应吗　responseCode="+responseCode+"  responseMessage="+responseMessage+"  filePath="+filePath
        File targeFile =  new File(filePath)
        if(!targeFile.getParentFile().exists()){
            targeFile.getParentFile().mkdirs()
        }
        if(!targeFile.exists()){
            targeFile.createNewFile()
        }
        targeFile.withOutputStream {fos->
            fos << inputStream
        }
        return null;
    }
}
