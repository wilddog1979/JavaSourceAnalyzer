package org.eaSTars.sca.aspect.impl;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class PerformanceMonitor {

	@Around("execution(* org.eaSTars.adashboard.converter..*.*(..))")
	public Object measureRuntime1(ProceedingJoinPoint pjp) throws Throwable {
		return measure(pjp);
	}
	
	@Around("execution(* org.eaSTars..service..*.*(..))")
	public Object measureRuntime2(ProceedingJoinPoint pjp) throws Throwable {
		return measure(pjp);
	}
	
	@Around("execution(* org.eaSTars..dao..*.*(..))")
	public Object measureRuntime3(ProceedingJoinPoint pjp) throws Throwable {
		return measure(pjp);
	}
	
	public Object measure(ProceedingJoinPoint pjp) throws Throwable {
		long starttime = System.currentTimeMillis();
		try {
			return pjp.proceed();
		} finally {
			long endtime = System.currentTimeMillis();
			System.out.printf("%d\t%s - %s\n", endtime - starttime, pjp.getTarget().getClass().getName(), pjp.getSignature().getName());
		}
	}
	
	public static void measure(ExecuteMethod methodExecutor , Class<? extends Object> clazz, String methodname) {
		long starttime = System.currentTimeMillis();
		try {
			methodExecutor.execute();
		} finally {
			long endtime = System.currentTimeMillis();
			System.out.printf("%d\t%s - %s\n", endtime - starttime, clazz.getName(), methodname);
		}
	}
}
