package com.leadbank.guardian.util;

import org.aspectj.lang.JoinPoint;

public final class AopUtils {

    public static JoinPoint unWrapJoinPoint(final JoinPoint point) {
        JoinPoint naked = point;
        while (naked.getArgs().length > 0 && naked.getArgs()[0] instanceof JoinPoint) {
            naked = (JoinPoint) naked.getArgs()[0];
        }
        return naked;
    }
}
