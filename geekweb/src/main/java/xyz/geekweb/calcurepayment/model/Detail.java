package xyz.geekweb.calcurepayment.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class Detail implements Serializable {

    private int index;

    private String date;

    private double totalAmount;

    private double interest;

    private double balance;
}
