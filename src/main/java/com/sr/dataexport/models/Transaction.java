package com.sr.dataexport.models;

import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    private long userId;

    private long cardId;

    private int year;

    private int month;

    private int day;

    private String time;


    private double amount;


    private String type;


    private long merchantId;


    private String merchantCity;


    private String merchantState;

    private String zip;


    private int mcc;


    private String errors;


    private boolean isFraud;

}
