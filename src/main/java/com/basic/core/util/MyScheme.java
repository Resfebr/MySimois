package com.basic.core.util;

import org.apache.storm.spout.Scheme;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * locate com.basic.core.util
 * Created by windy on 2019/1/2
 */
public class MyScheme implements Scheme {
    private static final Charset UTF8_CHARSET = StandardCharsets.UTF_8;
    public static String STRING_SCHEME_KEY = "word";

    public static String deserializeString(ByteBuffer string) {
        if (string.hasArray()) {
            int base = string.arrayOffset();
            return new String(string.array(), base + string.position(), string.remaining());
        } else {
            return new String(Utils.toByteArray(string), UTF8_CHARSET);
        }
    }

    public List<Object> deserialize(ByteBuffer bytes) {
        return new Values(deserializeString(bytes));
    }

    public Fields getOutputFields() {
        return new Fields(STRING_SCHEME_KEY);
    }
}
