package $Package.project.retrofit_config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import $Package.core.gson.NullStringToEmptyAdapterFactory;
import $Package.core.type_builder.TypeBuilder;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * 解析json基类
 * Created by Vincent on $Time.
 */
public class BaseJsonBeanDeserializer<T> implements JsonDeserializer<BaseJsonBean> {


    private final Class<T> aClass;

    public BaseJsonBeanDeserializer(Class<T> aClass) {
        this.aClass = aClass;
    }

    @Override
    public BaseJsonBean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory()).create();
        BaseJsonBean baseJsonBean = new BaseJsonBean();
        JsonObject jsonObject = json.getAsJsonObject();
        JsonElement jsonResultElement = jsonObject.get("result");
        baseJsonBean.setUnAuthorizedRequest(jsonObject.has("unAuthorizedRequest")
                && jsonObject.get("unAuthorizedRequest").getAsBoolean());
        baseJsonBean.setSuccess(jsonObject.has("success")
                && jsonObject.get("success").getAsBoolean());
        baseJsonBean.setError(jsonObject.get("error").isJsonNull()
                ? "" : jsonObject.get("error").getAsJsonObject().get("message").getAsString());
        if (jsonObject.has("result")) {
            if (jsonResultElement.isJsonNull()) {
                baseJsonBean.setResult(null);
                baseJsonBean.setResults(null);
            } else {
                if (jsonResultElement.isJsonArray()) {
                    baseJsonBean.setResult(null);
                    baseJsonBean.setResults(fromJson2Array(gson, jsonResultElement, aClass));
                } else {
                    if (jsonResultElement.getAsJsonObject().has("message")) {
                        JsonElement jsonMessageElement = jsonResultElement.getAsJsonObject().get("message");
                        baseJsonBean.setMessage(jsonMessageElement.getAsString());
                    } else {
                        baseJsonBean.setResult(fromJson2Object(gson, jsonResultElement, aClass));
                        baseJsonBean.setResults(null);
                    }
                }
            }
        }
        return baseJsonBean;
    }
    /**
     * json转为数组实体
     * @param gson
     * @param jsonResultElement
     * @param aClass
     * @param <T>
     * @return
     */
    private <T> ArrayList<T> fromJson2Array(Gson gson, JsonElement jsonResultElement, Class<T> aClass) {
        Type type = TypeBuilder
                .newInstance(ArrayList.class)
                .addTypeParam(aClass)
                .build();
        return gson.fromJson(jsonResultElement, type);
    }

    /**
     * json转为实体
     * @param gson
     * @param jsonResultElement
     * @param aClass
     * @param <T>
     * @return
     */
    private <T> T fromJson2Object (Gson gson, JsonElement jsonResultElement, Class<T> aClass) {
        return gson.fromJson(jsonResultElement, aClass);
    }

}
