package com.netex.apps.intf;

public interface Result<T, V> {
    void bind(Effect<T> success, Effect<T> failure);

    T indicator();

    V message();

    static <T, V> Result<T, V> failure(T indicator, V message) {
        return new Failure<>(indicator, message);
    }

    static <T, V> Result<T, V> success(T indicator, V value) {
        return new Success<>(indicator, value);
    }

    class Success<T, V> implements Result<T, V> {

        private final T indicator;
        private final V message;

        private Success(T indicator, V message) {
            this.message = message;
            this.indicator = indicator;
        }

        @Override
        public V message() {
            return message;
        }

        @Override
        public T indicator() {
            return indicator;
        }

        @Override
        public void bind(Effect<T> success, Effect<T> failure) {
            success.apply(indicator);
        }
    }

    class Failure<T, V> implements Result<T, V> {
        private final T indicator;
        private final V message;

        private Failure(T indicator, V message) {
            this.message = message;
            this.indicator = indicator;
        }

        @Override
        public T indicator() {
            return indicator;
        }

        @Override
        public void bind(Effect<T> success, Effect<T> failure) {
            failure.apply(indicator);
        }

        public V message() {
            return message;
        }
    }
}
