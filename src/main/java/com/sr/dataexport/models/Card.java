package com.sr.dataexport.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor


public class Card {

    private long userId;


    private long cardId;


    private String cardNumber;


    private String expiryDate;


    private String cvv;


    private String cardType;
}
