package $Package.core.model;

/**
 * 短信bean
 * Created by Vincent on 2019-05-10 11:33:28.
 */
public class SMSBean {

    private String name;
    private String phone;
    private String content;
    private String date;
    private String type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "SMSBean{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", content='" + content + '\'' +
                ", date='" + date + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
