package com.webjava.lab1.config;

import com.webjava.lab1.service.FeatureNotAvailableException;
import com.webjava.lab1.service.FeatureToggleService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class FeatureToggleAspect {

  private static final Logger log = LoggerFactory.getLogger(FeatureToggleAspect.class);

  private final FeatureToggleService featureToggleService;

  public FeatureToggleAspect(FeatureToggleService featureToggleService) {
    this.featureToggleService = featureToggleService;
  }

  @Around("@annotation(featureToggle)")
  public Object checkFeatureToggle(ProceedingJoinPoint joinPoint, FeatureToggle featureToggle)
      throws Throwable {
    String featureName = featureToggle.value();

    if (!featureToggleService.isFeatureEnabled(featureName)) {
      log.warn(
          "Feature '{}' is disabled, blocking access to method '{}'",
          featureName,
          joinPoint.getSignature().getName());
      throw new FeatureNotAvailableException(featureName);
    }

    log.info(
        "Feature '{}' is enabled, proceeding with method '{}'",
        featureName,
        joinPoint.getSignature().getName());
    return joinPoint.proceed();
  }
}
