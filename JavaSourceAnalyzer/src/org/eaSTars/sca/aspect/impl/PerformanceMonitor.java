package org.eaSTars.sca.aspect.impl;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class PerformanceMonitor {

	@Around("execution(* org.eaSTars.adashboard.populator..*.*(..))")
	public Object measureRuntime1(ProceedingJoinPoint pjp) throws Throwable {
		return measure(pjp);
	}
	
	@Around("execution(* org.eaSTars.adashboard.service..*.*(..))")
	public Object measureRuntime2(ProceedingJoinPoint pjp) throws Throwable {
		return measure(pjp);
	}
	
	public Object measure(ProceedingJoinPoint pjp) throws Throwable {
		long starttime = System.currentTimeMillis();
		Object retval = pjp.proceed();
		long endtime = System.currentTimeMillis();
		System.out.println((endtime - starttime)+" "+pjp.getTarget().getClass().getName()+" - "+pjp.getSignature().getName());
		
		return retval;
	}
}
