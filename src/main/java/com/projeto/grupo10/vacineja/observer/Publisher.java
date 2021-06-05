package com.projeto.grupo10.vacineja.observer;


import java.util.HashSet;
import java.util.Set;

public interface Publisher {
    Set<Subscriber> subscribers = new HashSet<>();

    default void addSubscriber(Subscriber subscriber){
        subscribers.add(subscriber);
    }

    default void notificaNovaQtdDoses(){
        subscribers.forEach(Subscriber::atualizaQtdDoses);
    }
}
