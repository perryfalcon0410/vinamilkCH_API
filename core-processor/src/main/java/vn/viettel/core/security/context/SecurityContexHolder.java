package vn.viettel.core.security.context;

import org.springframework.util.Assert;

public class SecurityContexHolder {

    private static UserContext context;

    public static synchronized UserContext getContext() {
        if (context == null) {
            context = new UserContext();
        }
        return context;
    }

    public void  setContext(UserContext context) {
        Assert.notNull(context, "Only non-null UserContext instances are permitted");
        this.context = context;
    }

}
