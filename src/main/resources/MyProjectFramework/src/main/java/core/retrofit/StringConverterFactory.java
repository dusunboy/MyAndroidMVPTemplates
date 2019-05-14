package $Package.core.retrofit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * 纯字符返回接口数据
 * Created by Vincent on 2019-05-10 11:33:28.
 */
public class StringConverterFactory extends Converter.Factory {

    public static StringConverterFactory create() {
        return new StringConverterFactory();
    }
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        return new ConfigurationServiceConverter();
    }
    final class ConfigurationServiceConverter implements Converter<ResponseBody, String> {

        @Override
        public String convert(ResponseBody value) throws IOException {
            BufferedReader r = new BufferedReader(new InputStreamReader(value.byteStream()));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            return total.toString();
        }
    }
}
