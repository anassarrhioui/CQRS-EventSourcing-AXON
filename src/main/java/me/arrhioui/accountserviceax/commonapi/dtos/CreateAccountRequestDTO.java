package me.arrhioui.accountserviceax.commonapi.dtos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateAccountRequestDTO {
    private String currency;
    private double initialBalance;
}
