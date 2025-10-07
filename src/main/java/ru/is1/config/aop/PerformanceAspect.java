package ru.is1.config.aop;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import java.util.concurrent.TimeUnit;

@Interceptor
@MonitorPerformance
public class PerformanceAspect {

    @AroundInvoke
    public Object measureMethodExecutionTime(InvocationContext context)
            throws Exception {
        long start = System.nanoTime();
        Object returnValue = context.proceed();
        long end = System.nanoTime();

        String methodName = context.getMethod().getName();
        String className = context.getMethod().getDeclaringClass().getSimpleName();

        System.out.println(
                "Execution of " + className + "." + methodName + " took " +
                        TimeUnit.NANOSECONDS.toMillis(end - start) + " ms"
        );

        return returnValue;
    }
}
