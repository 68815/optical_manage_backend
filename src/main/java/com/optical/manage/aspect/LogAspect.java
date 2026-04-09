package com.optical.manage.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 日志切面类
 * 用于记录 Controller 和 Service 层的调用日志
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

    /**
     * Controller 层切点
     */
    @Pointcut("execution(* com.optical.manage.controller..*.*(..))")
    public void controllerPointcut() {}

    /**
     * Service 层切点
     */
    @Pointcut("execution(* com.optical.manage.service..*.*(..))")
    public void servicePointcut() {}

    /**
     * Mapper 层切点
     */
    @Pointcut("execution(* com.optical.manage.mapper..*.*(..))")
    public void mapperPointcut() {}

    /**
     * 环绕通知 - Controller 层
     * 记录请求和响应信息
     */
    @Around("controllerPointcut()")
    public Object aroundController(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs();

        // 过滤掉 ServerWebExchange 等不需要记录的参数
        String argsStr = Arrays.stream(args)
                .filter(arg -> !(arg instanceof ServerWebExchange))
                .map(this::toString)
                .collect(Collectors.joining(", "));

        log.info("[CONTROLLER] {}.{} - 请求参数: {}", className, methodName, argsStr);

        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();

            // 处理 Mono 和 Flux 返回值
            if (result instanceof Mono) {
                return ((Mono<?>) result).doOnSuccess(res -> {
                    log.info("[CONTROLLER] {}.{} - 响应结果: {}, 耗时: {}ms",
                            className, methodName, toString(res), (System.currentTimeMillis() - startTime));
                }).doOnError(error -> {
                    log.error("[CONTROLLER] {}.{} - 执行异常: {}, 耗时: {}ms",
                            className, methodName, error.getMessage(), (System.currentTimeMillis() - startTime));
                });
            } else if (result instanceof Flux) {
                return ((Flux<?>) result).doOnComplete(() -> {
                    log.info("[CONTROLLER] {}.{} - 响应完成, 耗时: {}ms",
                            className, methodName, (System.currentTimeMillis() - startTime));
                }).doOnError(error -> {
                    log.error("[CONTROLLER] {}.{} - 执行异常: {}, 耗时: {}ms",
                            className, methodName, error.getMessage(), (System.currentTimeMillis() - startTime));
                });
            } else {
                log.info("[CONTROLLER] {}.{} - 响应结果: {}, 耗时: {}ms",
                        className, methodName, toString(result), (endTime - startTime));
                return result;
            }
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("[CONTROLLER] {}.{} - 执行异常: {}, 耗时: {}ms",
                    className, methodName, e.getMessage(), (endTime - startTime));
            throw e;
        }
    }

    /**
     * 环绕通知 - Service 层
     * 记录方法调用和耗时
     */
    @Around("servicePointcut()")
    public Object aroundService(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs();

        String argsStr = Arrays.stream(args)
                .map(this::toString)
                .collect(Collectors.joining(", "));

        log.debug("[SERVICE] {}.{} - 请求参数: {}", className, methodName, argsStr);

        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();

            if (result instanceof Mono) {
                return ((Mono<?>) result).doOnSuccess(res -> {
                    log.debug("[SERVICE] {}.{} - 执行成功, 耗时: {}ms",
                            className, methodName, (System.currentTimeMillis() - startTime));
                }).doOnError(error -> {
                    log.error("[SERVICE] {}.{} - 执行异常: {}, 耗时: {}ms",
                            className, methodName, error.getMessage(), (System.currentTimeMillis() - startTime));
                });
            } else if (result instanceof Flux) {
                return ((Flux<?>) result).doOnComplete(() -> {
                    log.debug("[SERVICE] {}.{} - 执行完成, 耗时: {}ms",
                            className, methodName, (System.currentTimeMillis() - startTime));
                }).doOnError(error -> {
                    log.error("[SERVICE] {}.{} - 执行异常: {}, 耗时: {}ms",
                            className, methodName, error.getMessage(), (System.currentTimeMillis() - startTime));
                });
            } else {
                log.debug("[SERVICE] {}.{} - 执行成功, 耗时: {}ms", className, methodName, (endTime - startTime));
                return result;
            }
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("[SERVICE] {}.{} - 执行异常: {}, 耗时: {}ms",
                    className, methodName, e.getMessage(), (endTime - startTime));
            throw e;
        }
    }

    /**
     * 环绕通知 - Mapper 层
     * 记录 SQL 执行
     */
    @Around("mapperPointcut()")
    public Object aroundMapper(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        log.trace("[MAPPER] {}.{} - SQL执行开始", className, methodName);

        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            log.trace("[MAPPER] {}.{} - SQL执行完成, 耗时: {}ms", className, methodName, (endTime - startTime));
            return result;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("[MAPPER] {}.{} - SQL执行异常: {}, 耗时: {}ms",
                    className, methodName, e.getMessage(), (endTime - startTime));
            throw e;
        }
    }

    /**
     * 异常通知
     * 记录所有层抛出的异常
     */
    @AfterThrowing(pointcut = "controllerPointcut() || servicePointcut() || mapperPointcut()", throwing = "ex")
    public void afterThrowing(JoinPoint joinPoint, Throwable ex) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        log.error("[EXCEPTION] {}.{} - 异常类型: {}, 异常信息: {}",
                className, methodName, ex.getClass().getSimpleName(), ex.getMessage(), ex);
    }

    /**
     * 对象转字符串（限制长度，防止日志过大）
     */
    private String toString(Object obj) {
        if (obj == null) {
            return "null";
        }
        try {
            String str = obj.toString();
            if (str.length() > 500) {
                return str.substring(0, 500) + "...(truncated)";
            }
            return str;
        } catch (Exception e) {
            return obj.getClass().getSimpleName();
        }
    }
}
