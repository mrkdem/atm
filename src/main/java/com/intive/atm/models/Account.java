package com.intive.atm.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "All details about the Account. ")
public class Account {

    @ApiModelProperty(notes = "The account number")
    private int accountNumber;

    @ApiModelProperty(notes = "The account balance")
    private double balance;

    @ApiModelProperty(notes = "The account limit")
    private double limit;
}
