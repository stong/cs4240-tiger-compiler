package com.gangweedganggang.cs4240.dot4j.model;

@FunctionalInterface
public interface ThrowingFunction<T, R> {
    R apply(T t) throws Exception;
}
