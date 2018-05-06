package com.netex.apps.intf;

public interface Result<T, V> {
    void bind(Effect<T, V> success, Effect<T, V> failure);

    static <T, V> Result<T, V> failure(T indicator, V message) {
        return new Failure<>(indicator, message);
    }

    static <T, V> Result<T, V> success(T indicator, V value) {
        return new Success<>(indicator, value);
    }

    class Success<T, V> implements Result<T, V> {

        private final T indicator;
        private final V value;

        private Success(T indicator, V value) {
            this.value = value;
            this.indicator = indicator;
        }

        public V getValue() {
            return value;
        }

        @Override
        public void bind(Effect<T, V> success, Effect<T, V> failure) {
            success.apply(indicator);
        }
    }

    class Failure<T, V> implements Result<T, V> {
        private final V message;
        private final T indicator;

        private Failure(T indicator, V message) {
            this.message = message;
            this.indicator = indicator;
        }

        @Override
        public void bind(Effect<T, V> success, Effect<T, V> failure) {
            failure.apply(indicator);
        }

        public V getMessage() {
            return message;
        }
    }
}
