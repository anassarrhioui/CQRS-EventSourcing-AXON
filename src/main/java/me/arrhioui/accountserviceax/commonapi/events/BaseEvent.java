package me.arrhioui.accountserviceax.commonapi.events;

import lombok.Getter;

@Getter
public class BaseEvent <T>{
    final private T id;

    public BaseEvent(T id) {
        this.id = id;
    }
}
