package com.alliejc.wcstinder.callback;

/**
 * Created by acaldwell on 3/28/18.
 */

public interface ICallback<T,E> {
        void onError(E obj);
        void onCompleted(T obj);
    }
