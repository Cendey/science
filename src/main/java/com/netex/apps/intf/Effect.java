package com.netex.apps.intf;

public interface Effect<T, V> {
    V apply(T t);
}
