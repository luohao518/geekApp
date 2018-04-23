package xyz.geekweb.calcurepayment;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import xyz.geekweb.calcurepayment.model.Detail;
import xyz.geekweb.calcurepayment.model.RepaymentScheduleData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class CalcuRepaymentService {

	private Formatter f = new Formatter(System.out);


	/**
	 * calcuRepayment
	 * @param name
	 * @param date
	 * @param totalAmount
	 * @param interestRate
	 * @param repaymentMonthly
	 * @return
	 */
	public RepaymentScheduleData<List<Detail>> of(String name, String date, double totalAmount, double interestRate,
												  double repaymentMonthly) {
		double balance = totalAmount;
		int i = 0;
		System.out.println(date);
		LocalDate baseDate = LocalDate.parse(date);

		RepaymentScheduleData<List<Detail>> repaymentScheduleData = new RepaymentScheduleData<List<Detail>>();
		repaymentScheduleData.name = name;
		ArrayList<Detail> detailList = new ArrayList<Detail>();
		Detail detail = null;

		final DecimalFormat df = new DecimalFormat("#.##");

		while (balance > 0) {
			double interest = balance * (interestRate / 12);
			if (interest > repaymentMonthly) {
				throw new IllegalArgumentException("The interest is larger then repaymentMonthly!");
			}
			double totalAmountForPrint = balance + interest;
			balance += interest - repaymentMonthly;
			balance = balance < 0 ? 0 : balance;
			++i;

			if (i > 500) {
				throw new IllegalArgumentException("Too much number!");
			}
			detail = new Detail();
			detail.index = i;
			detail.date = baseDate.plusMonths(i).toString();
			detail.totalAmount = Double.valueOf(df.format(totalAmountForPrint));
			detail.interest = Double.valueOf(df.format(interest));
			detail.balance = Double.valueOf(df.format(balance));
			detailList.add(detail);

		}

		repaymentScheduleData.data = detailList;
		return repaymentScheduleData;
	}

	public void printList(String json) {
		Gson gson = new Gson();
		RepaymentScheduleData<List<Detail>> repaymentScheduleData = gson.fromJson(json,
				new TypeToken<RepaymentScheduleData<List<Detail>>>() {
				}.getType());
		printHead();
		for (Detail item : repaymentScheduleData.data) {
			printData(item);
		}
	}

	private void printHead() {
		f.format("%-5s %10s %12s %12s %12s\n", "index", "date", "Total Amount", "interest", "balance");
		f.format("%-5s %10s %12s %12s %12s\n", "-----", "----------", "------------", "------------", "------------");
	}

	private void printData(Detail detail) {
		f.format("%-5d %10s %,10.2f  %,10.2f   %,10.2f \n", detail.index, detail.date, detail.totalAmount,
				detail.interest, detail.balance);
	}

}
