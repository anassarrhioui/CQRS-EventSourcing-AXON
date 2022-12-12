package me.arrhioui.accountserviceax.commonapi.dtos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreditAccountRequestDTO {
    private String id;
    private String currency;
    private double amount;
}
