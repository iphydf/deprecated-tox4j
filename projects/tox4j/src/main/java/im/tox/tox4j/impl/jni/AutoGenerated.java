package im.tox.tox4j.impl.jni;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface AutoGenerated {
  String value() default "";
}