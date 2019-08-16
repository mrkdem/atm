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
@ApiModel(description = "All details about the Customer.")
public class Customer {

    @ApiModelProperty(notes = "The customer username")
    private String username;

    @ApiModelProperty(notes = "The customer account")
    private Account account;
}
