package everthing.core.interceptor;

import everthing.core.model.Thing;

/**
 * Created by hunter on 2019/2/28
 */
public interface ThingInterceptor {
    void apply(Thing thing);
}
