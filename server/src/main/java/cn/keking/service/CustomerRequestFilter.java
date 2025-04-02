package cn.keking.service;

import cn.keking.utils.WebUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

/**
 * @author joman.jiang
 */
public class CustomerRequestFilter {
    private final static String EXT_PARAMETER_KEY = "extra";
    private static final ObjectMapper mapper = new ObjectMapper();
    private final static String FILE_NAME_KEY = "fullfilename";
    private final static String WATERMARK_TXT_KEY = "watermarkTxt";

    public static void doCustomerFilter(String fileUrl, HttpServletRequest req) {

        { // 兼容历史
            String[] fileNames = req.getParameterValues(FILE_NAME_KEY);
            if (fileNames != null && fileNames.length > 0) {
                req.setAttribute(FILE_NAME_KEY, fileNames[0]);
                return;
            }
        }

        // 请求增加一个ext参数，内容为json
        String[] extras = req.getParameterValues(EXT_PARAMETER_KEY);
        if (extras == null || extras.length == 0) {
            return; // 兼容历史
//            throw new RuntimeException("error params, no extras");
        }
        String extra = extras[0];
        if (!StringUtils.hasText(extra)) {
            throw new RuntimeException("error params, no extras");
        }
        try {
            String extraJson = decode(extra);
            Map<String, String> map = mapper.readValue(extraJson, Map.class);
            // 增加fullfilename是为了解决。
            // 解决存储url中的路径文件名不包含后缀，将文件名提取到req的参数中。不放按照既有逻辑放在url中，是因为oss存储url增加参数导致签名错误无法下载
            // https://stres.quectel.com:8139/fileview/onlinePreview?url=base64codexxx=&extra=1xxxxx
            String fullFileName = map.get(FILE_NAME_KEY);
            if (!StringUtils.hasText(fullFileName)) {
                fullFileName = WebUtils.getUrlParameterReg(fileUrl, "fullfilename");
            }
            if (StringUtils.hasText(fullFileName)) {
                req.setAttribute(FILE_NAME_KEY, fullFileName);
            }
            // 可自定义水印
            String watermarkTxt = map.get(WATERMARK_TXT_KEY);
            if (StringUtils.hasText(watermarkTxt)) {
                req.setAttribute(WATERMARK_TXT_KEY, watermarkTxt);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Parser parameters error", ex);
        }
    }

    public static String getFileNameFromRequest(HttpServletRequest request) {
        Object fileName = request.getAttribute(FILE_NAME_KEY);
        if (fileName != null) {
            return (String) fileName;
        }
        return null;
    }

    private static String decode(String content) throws Exception {
        String password = "2025YEAR";
        String algorithm = "DES";
        Charset charset = StandardCharsets.UTF_8;
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            SecretKeySpec key = new SecretKeySpec(password.getBytes(charset), algorithm);
            // 解密初始化
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] bytes = cipher.doFinal(Base64.getUrlDecoder().decode(content.getBytes()));
            return new String(bytes);
        } catch (Exception e) {
            throw e;
        }
    }

}
