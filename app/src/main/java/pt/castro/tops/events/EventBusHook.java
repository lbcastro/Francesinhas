package pt.castro.tops.events;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Created by lourenco.castro on 02-06-2015.
 */
@Target({ElementType.METHOD})
public @interface EventBusHook {
}