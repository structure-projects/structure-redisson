package cn.structure.starter.redisson.utils;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

/**
 *
 * @Title:  StringUtil
 * @Description: StringUtil
 * @date:   2019/9/19 16:31
 * @Version V1.0.0
 *
 */
public class StringUtil {

    /**
     * <p>
     *     通过spel表达式获取理想的key值
     * </p>
     * @param key key 参数
     * @param parameterNames 参数列表名
     * @param values 参数列表值
     * @return java.lang.String
     */
    public static String getValueBySpelKey(String key, String[] parameterNames, Object[] values){
        //不存在表达式返回
        if (!key.contains("#")) {
            return key;
        }
        //使用下划线拆分表达式
        String[] spelKeys = key.split("_");
        //要返回的key
        StringBuilder sb = new StringBuilder();
        //遍历拆分结果用解析器解析
        for (int i = 0 ; i <= spelKeys.length - 1 ; i++) {
            if (!spelKeys[i].startsWith("#")) {
                sb.append(spelKeys[i]);
                continue;
            }
            String tempKey = spelKeys[i];
            //spel解析器
            ExpressionParser parser = new SpelExpressionParser();
            //spel上下文
            EvaluationContext context = new StandardEvaluationContext();
            for (int j = 0; j < parameterNames.length; j++) {
                context.setVariable(parameterNames[j], values[j]);
            }
            Expression expression = parser.parseExpression(tempKey);
            Object value = expression.getValue(context);
            if (value != null) {
                sb.append(value.toString());
            }
        }
        //返回
        return sb.toString();
    }

    /**
     * <p>判断字符串是否为空</p>
     * <ul>
     * <li>null-->true
     * <li>""-->true
     * <li>"  "-->true
     * <li>"\t"-->true
     * <li>"\n"-->true
     * <li>"\f"-->true
     * <li>"\r"-->true
     * <li>"123"-->false
     * <li>" 123 "-->false
     * <li>" 1 23 "-->false
     * </ul>
     * @param string
     * @return boolean
     **/
    public static boolean isBlank(String string) {
        if (string == null || string.length() == 0) {
            return true;
        }
        int l = string.length();
        for (int i = 0; i < l; i++) {
            if (!StringUtil.isWhitespace(string.codePointAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>
     *     判断是否为空白字符
     * </p>
     * @param c
     * @return boolean
     **/
    private static boolean isWhitespace(int c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\f' || c == '\r';
    }

    /**
     * <p>
     *     unicode 转字符串
     * </p>
     * @param unicode
     * @return java.lang.String
     **/
    public static String unicode2String(String unicode) {

        StringBuffer string = new StringBuffer();

        String[] hex = unicode.split("\\\\u");

        for (int i = 1; i < hex.length; i++) {

            // 转换出每一个代码点
            int data = Integer.parseInt(hex[i], 16);

            // 追加成string
            string.append((char) data);
        }

        return string.toString();
    }

    /**
     * <p>
     *     去掉英文单引号以及首尾空格
     * </p>
     * @param string
     * @return java.lang.String
     **/
    public static String trimAndRemoveQuot(String string) {
        return string.replaceAll("['*]*", "").trim();
    }

    /**
     * <p>
     *     去掉英文单引号以及所有空格
     * </p>
     * @param string
     * @return java.lang.String
     **/
    public static String removeAllBlankAndQuot(String string) {
        return string.replaceAll("['*| *|　*|\\s*]*", "").trim();
    }

    /**
     * <p>
     *      拼接redis带前缀的地址
     * </p>
     * @param address
     * @return
     */
    public static  String prefixAddress(String address) {
        if (!StringUtils.isEmpty(address) && !address.startsWith("redis")) {
            return "redis://" + address;
        }
        return address;
    }

}
