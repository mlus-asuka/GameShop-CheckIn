package vip.fubuki.util;

import com.alibaba.fastjson2.JSON;
import lombok.SneakyThrows;
import vip.fubuki.GameShopPlugin;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class GameItemInitialization {
    @SneakyThrows
    public void Initialization(){
        InputStream resourceAsStream = GameShopPlugin.class.getClassLoader().getResourceAsStream("ItemName.json");
        assert resourceAsStream != null;
        final int bufferlength = resourceAsStream.available();
        byte[] bytes = new byte[bufferlength];
        int length = 0;
        int tempLength;
        do {
            tempLength = resourceAsStream.read(bytes, length, bufferlength - length);
            if (tempLength > 0)
                length += tempLength;
        }
        while (tempLength > 0);
        String data = new String(bytes, 0, length, StandardCharsets.UTF_8);
        /*
          读取Resource数据
         */
        Map hashMap =  JSON.parseObject(data, Map.class);
        GameShopPlugin.getInstance().getItemList().setItemName(hashMap);
    }
}
