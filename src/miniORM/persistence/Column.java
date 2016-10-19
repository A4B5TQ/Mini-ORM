package miniORM.persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Specify a mapped column for a persistent property or field.
 *  If no Column annotation is specified, the default values are applied
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

    /**
     * The name of the column.
     * Defaults to the property or field name.
     */
    String name() default "";

    boolean unique() default false;

    /**
     * Whether the database column is nullable.
     */
    boolean nullable() default true;

    /**
     * The column length.
     * Applies only if a string-valued column is used.
     */
    int length() default 255;
}
