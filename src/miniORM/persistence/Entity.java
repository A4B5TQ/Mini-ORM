package miniORM.persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies that the class is an entity.
 * This annotation is applied to the entity class.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Entity {

    /**
     * The name of an entity. Defaults to the unqualified name of the entity class.
     * This name is used to refer to the entity in queries.
     */
    String name() default "";
}
