package me.athlaeos.lapi.utils;

public class Pair <T, E> {
    private final T obj1;
    private final E obj2;

    public Pair(T obj1, E obj2){
        this.obj1 = obj1;
        this.obj2 = obj2;
    }

    public T getObj1() {
        return obj1;
    }

    public E getObj2() {
        return obj2;
    }
}
