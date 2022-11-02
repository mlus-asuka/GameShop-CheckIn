package vip.fubuki.util;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ClassPathResource;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocalJsonUtil {
    /**
     * 从指定路径获取JSON并转换为List
     * @param path json文件路径
     * XiaoMing本体没有这个依赖
     */
    @Deprecated
    public static String getListFromJson(String path) {
        ClassPathResource resource = new ClassPathResource(path);
        return IoUtil.read(resource.getStream(), StandardCharsets.UTF_8);
    }

    /**
     * 将字符串中的”//“过滤
     * @param data 需要处理的字符串
     * @return 返回处理后的字符串
     */
    public static String regex(String data){
        String regEx = "\\\\";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(data);
        return m.replaceAll("").trim();
    }

    public static String regex2(String data){
        String regEx = "\"";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(data);
        return m.replaceAll("").trim();
    }

    public static Map<String, Object> StringToMap(String param) {
        Map<String, Object> map = new HashMap<>();


        //去除{}
        String s1 = param.replace("{", "");
        String s2 = s1.replace("}", "");
        String s3 = s2.trim();


        //1.根据逗号分隔
        String[] split = s3.split(",");

        for (int i = split.length - 1; i >= 0; i--) {

            String trim = split[i].trim();
            String[] split1 = trim.split("=");

            map.put(split1[0],split1[1]);

        }

        return map;
    }
}


