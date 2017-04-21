package $Package.core.model;

/**
 * 通话记录bean
 * Created by Vincent on $Time.
 */
public class CallLogBean {

    private String phone;
    private String name;
    private String type;
    private String date;
    private String duration;
    private String id;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "CallLogBean{" +
                "phone='" + phone + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", date='" + date + '\'' +
                ", duration='" + duration + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
