package $Package.core.gson;

//import io.realm.RealmList;
//
///**
// * json解析RealmList的bean
// * Created by Vincent on 2019-05-10 11:33:28.
// */
//public class RealmStringListConverter implements JsonSerializer<RealmList<RealmString>>,
//        JsonDeserializer<RealmList<RealmString>> {
//    @Override
//    public RealmList<RealmString> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//        RealmList<RealmString> realmStrings = new RealmList<>();
//        JsonArray jsonArray = json.getAsJsonArray();
//        for (JsonElement jsonElement : jsonArray) {
//            realmStrings.add((RealmString) context.deserialize(jsonElement, RealmString.class));
//        }
//        return realmStrings;
//    }
//
//    @Override
//    public JsonElement serialize(RealmList<RealmString> src, Type typeOfSrc, JsonSerializationContext context) {
//        JsonArray jsonArray = new JsonArray();
//        for (RealmString realmString : src) {
//            jsonArray.add(context.serialize(realmString.getValue()));
//        }
//        return jsonArray;
//    }
//}
