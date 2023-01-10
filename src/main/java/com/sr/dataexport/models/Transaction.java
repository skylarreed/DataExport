package com.sr.dataexport.models;

import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "user_id")
    private long userId;
    @Column(name = "card_id")
    private long cardId;
    @Column(name = "year")
    private int year;
    @Column(name = "month")
    private int month;
    @Column(name = "day")
    private int day;
    @Column(name = "time")
    private String time;

    @Column(name = "amount")

    private double amount;

    @Column(name = "type")
    private String type;

    @Column(name = "merchant_id")
    private long merchantId;

    @Column(name = "merchant_city")
    private String merchantCity;

    @Column(name = "merchant_state")
    private String merchantState;

    @Column(name = "zip")
    private String zip;

    @Column(name = "mcc")
    private int mcc;

    @Column(name = "errors")
    private String errors;

    @Column(name = "fraud")
    private boolean isFraud;

}
