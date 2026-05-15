package com.jaimemoro.cornermanbox.core.domain.repository;

public interface RepositoryCallback<T> {
    void onSuccess(T result);
    void onError(Exception e);
}
