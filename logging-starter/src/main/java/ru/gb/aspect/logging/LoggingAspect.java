package ru.gb.aspect.logging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j // Slf4j - Simple logging facade for java
@Aspect
@RequiredArgsConstructor
public class LoggingAspect {

  private final LoggingProperties properties;

  @Pointcut("@annotation(ru.gb.aspect.logging.Logging)") // method
  public void loggingMethodsPointcut() {}

  @Pointcut("@within(ru.gb.aspect.logging.Logging)") // class
  public void loggingTypePointcut() {}

  @Around(value = "loggingMethodsPointcut() || loggingTypePointcut()")
  public Object loggingMethod(ProceedingJoinPoint pjp) throws Throwable {
    String methodName = pjp.getSignature().getName();
    Object[] args = pjp.getArgs();
    Class<?>[] paramTypes = ((org.aspectj.lang.reflect.MethodSignature) pjp.getSignature()).getParameterTypes();

    if (properties.isPrintArgs()) {
      StringBuilder argsLog = new StringBuilder();
      for (int i = 0; i < args.length; i++) {
        argsLog.append(paramTypes[i].getSimpleName())
                .append(" = ")
                .append(args[i]);
        if (i < args.length - 1) {
          argsLog.append(", ");
        }
      }
      log.atLevel(properties.getLevel()).log("Arguments for {}#{}: [{}]",
              pjp.getTarget().getClass().getSimpleName(), methodName, argsLog);
    }

    log.atLevel(properties.getLevel()).log("Before -> {}#{}",
            pjp.getTarget().getClass().getSimpleName(), methodName);

    try {
      return pjp.proceed();
    } finally {
      log.atLevel(properties.getLevel()).log("After -> {}#{}",
              pjp.getTarget().getClass().getSimpleName(), methodName);
    }
  }
}
