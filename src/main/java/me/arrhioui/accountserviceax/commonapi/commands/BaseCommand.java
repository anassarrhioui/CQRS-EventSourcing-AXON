package me.arrhioui.accountserviceax.commonapi.commands;

import lombok.Getter;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
public class BaseCommand<T> {
    @TargetAggregateIdentifier
    final private T id;

    public BaseCommand(T id) {
        this.id = id;
    }
}
