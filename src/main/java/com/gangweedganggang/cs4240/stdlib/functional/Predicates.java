package com.gangweedganggang.cs4240.stdlib.functional;

import java.util.function.Predicate;

public class Predicates {
    public static <T> Predicate<T> not(Predicate<T> p) {
        return (t) -> !p.test(t);
    }
}
