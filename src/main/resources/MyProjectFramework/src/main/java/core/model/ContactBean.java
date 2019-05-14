package $Package.core.model;

import com.google.gson.annotations.Expose;

/**
 * 联系人bean
 * Created by Vincent on 2019-05-10 11:33:28.
 */
public class ContactBean {

    @Expose
    private String name;
    @Expose
    private String email;
    @Expose
    private String phone;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "ContactBean{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
