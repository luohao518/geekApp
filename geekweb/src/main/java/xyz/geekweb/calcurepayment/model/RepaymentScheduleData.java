package xyz.geekweb.calcurepayment.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class RepaymentScheduleData<T> implements Serializable {

    private String name;

    private T data;

}