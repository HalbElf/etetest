package org.db1.etetest.aspect;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

@Aspect
public class LogAspect {
	private static final Logger LOGGER = Logger.getLogger(LogAspect.class);

	@Pointcut("within(@org.springframework.stereotype.Controller *)")
	public void controllerBean() {
	}

	@Around("controllerBean() && @annotation(Log)")
	public Object around(ProceedingJoinPoint point) throws Throwable {
		// pass current user to the method
		MethodSignature methodSignature = (MethodSignature) point.getSignature();
		Method method = methodSignature.getMethod();

		StringBuilder sb = new StringBuilder();
		sb.append(String.format("called method '%s' ", method.getName()));
		if (method.getParameters().length != 0) {
			sb.append(String.format(" with %d parameter(s): ", method.getParameters().length));
			for (Parameter parameter : method.getParameters()) {
				sb.append(String.format("'%s' of the type '%s'; ", parameter.getName(), parameter.getType()));
			}
			sb.append(" parameter value(s): ");
			if (point.getArgs().length != 0) {
				for (Object arg : point.getArgs()) {
					try {
						sb.append(String.format("'%s'; ", arg.toString()));
					} catch (Exception e) {
						sb.append("<unknown>; ");
					}
				}
			}
		}
		Object ret = null;
		try {
			ret = point.proceed(point.getArgs());
			return ret;
		} finally {
			sb.append(String.format(" expected to return object of: %s; ", method.getReturnType()));
			if (ret != null) {
				sb.append(String.format(" returned: %s; ", ret.toString()));
			}
			LOGGER.trace(sb.toString());
		}
	}
}
