package ytc.wp.costomdep.net
/**
 * Created by wangpeng on 17-10-9.
 */
class HttpUpLoad extends HttpNetClient {

    def srcpath;
    String end = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";
    String contentType = "application/octet-stream"

    HttpUpLoad(String url, srcpath) {
        super(url)
        setHttpHead("Content-Type", "multipart/form-data;boundary=" + boundary)
        setHttpHead("Connection", "Keep-Alive")
        this.srcpath = srcpath
    }

    @Override
    protected void request(OutputStream outputStream) throws IOException {
        File file = null;
        if(srcpath instanceof File) file= srcpath
        if(srcpath instanceof String) file = new File(srcpath)
        String uploadPath = file.path
        String filename = uploadPath.substring(uploadPath.lastIndexOf("/") + 1);
        DataOutputStream  ds = new DataOutputStream(outputStream);
        ds.writeBytes(twoHyphens + boundary + end);
        ds.writeBytes("Content-Disposition: form-data; " + "name=\"arrfile" + "\";filename=\"" + filename
                + "\"" + end);
        ds.writeBytes("Content-Type:" + contentType +end+end);
        file.withInputStream { input ->
            ds << input
            ds.writeBytes(end+twoHyphens + boundary + twoHyphens + end);
        }
    }

    @Override
    protected ResponseBody respond(int responseCode, String responseMessage, InputStream inputStream) throws Exception {
        return null
    }


    /** 读取文件头 */
    private static String getFileHeader(File file) throws IOException {
        byte[] b = new byte[28];
        InputStream inputStream = null;

        try {
            inputStream = new FileInputStream(file);
            inputStream.read(b, 0, 28);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return bytesToHex(b);
    }

    /** 将字节数组转换成16进制字符串 */
    public static String bytesToHex(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}
