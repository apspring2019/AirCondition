package utils;

public interface MyObserver<T>
{
    void onStateChanged(T newState);
}
