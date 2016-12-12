package org.eaSTars.sca.aspect.impl;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class PerformanceMonitor {

	//@Around("execution(* org.eaSTars.sca.dao.impl.*.*(..))")
	@Around("execution(* org.eaSTars.dblayer.dao.impl.*.*(..))")
	public Object measureRuntime(ProceedingJoinPoint pjp) throws Throwable {
		long starttime = System.currentTimeMillis();
		Object retval = pjp.proceed();
		long endtime = System.currentTimeMillis();
		System.out.println((endtime - starttime)+" "+pjp.getSignature().toString());
		
		return retval;
	}
}
