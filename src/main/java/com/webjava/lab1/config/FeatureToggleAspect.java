package com.webjava.lab1.config;

import com.webjava.lab1.service.FeatureNotAvailableException;
import com.webjava.lab1.service.FeatureToggleService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class FeatureToggleAspect {

  private final FeatureToggleService featureToggleService;

  public FeatureToggleAspect(FeatureToggleService featureToggleService) {
    this.featureToggleService = featureToggleService;
  }

  @Around("@annotation(featureToggle)")
  public Object checkFeatureToggle(ProceedingJoinPoint joinPoint, FeatureToggle featureToggle)
      throws Throwable {
    String featureName = featureToggle.value();

    if (!featureToggleService.isFeatureEnabled(featureName)) {
      throw new FeatureNotAvailableException(featureName);
    }

    return joinPoint.proceed();
  }
}
