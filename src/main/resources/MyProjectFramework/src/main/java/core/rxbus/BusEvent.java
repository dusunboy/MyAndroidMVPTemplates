package $Package.core.rxbus;

/**
 * BusEvent类
 * Created by Vincent on $Time.
 */
public class BusEvent {
    private int type;
    private Object object;

    public BusEvent(int type, Object object) {
        this.type = type;
        this.object = object;
    }

    public int getType() {
        return type;
    }

    public Object getObject() {
        return object;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
