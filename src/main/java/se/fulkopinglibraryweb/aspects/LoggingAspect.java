package se.fulkopinglibraryweb.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* se.fulkopinglibraryweb.service.*.*(..)) || " +
            "execution(* se.fulkopinglibraryweb.controllers.*.*(..)) || " +
            "execution(* se.fulkopinglibraryweb.servlets.*.*(..))") 
    public void applicationPointcut() {}

    @Around("applicationPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] methodArgs = joinPoint.getArgs();

        logger.info("Entering {}.{}() with arguments: {}", className, methodName, methodArgs);

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - start;

            logger.info("Exiting {}.{}() with result: {}. Execution time: {} ms",
                    className, methodName, result, executionTime);

            return result;
        } catch (Exception e) {
            logger.error("Exception in {}.{}() with cause: {}",
                    className, methodName, e.getCause() != null ? e.getCause() : "NULL");
            throw e;
        }
    }
}