package com.sr.dataexport.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Merchant {

    private long merchantId;
    private String merchantName;

    private String merchantCity;

    private String merchantState;

    private String zip;

    private int mcc;


}
