package com.projeto.grupo10.vacineja.observer;

public interface Subscriber {
    default void atualizaQtdDoses(){
        System.out.println("eae kk");
    }
}
