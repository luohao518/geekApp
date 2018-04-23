package xyz.geekweb.calcurepayment.model;

import javax.validation.constraints.Size;

public class FormData {

	private String name;
	
	@Size(min=10, max=10)
	private String date;
	
	@Size(min=1, max=10)
	private String totalAmount;

	@Size(min=1, max=3)
	private String interestRate;
	
	@Size(min=1, max=10)
	private String repaymentMonthly;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(String interestRate) {
		this.interestRate = interestRate;
	}

	public String getRepaymentMonthly() {
		return repaymentMonthly;
	}

	public void setRepaymentMonthly(String repaymentMonthly) {
		this.repaymentMonthly = repaymentMonthly;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(" name:").append(name).append(" totalAmount:").append(totalAmount).append(" interestRate:")
				.append(interestRate).append(" repaymentMonthly:").append(repaymentMonthly);
		return sb.toString();
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}
