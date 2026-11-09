package util;

import config.AppConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AppContext {

    private static AnnotationConfigApplicationContext ctx;

    public static void start() {
        ctx = new AnnotationConfigApplicationContext(AppConfig.class);
    }

    public static <T> T getBean(Class<T> kelas) {
        return ctx.getBean(kelas);
    }
}