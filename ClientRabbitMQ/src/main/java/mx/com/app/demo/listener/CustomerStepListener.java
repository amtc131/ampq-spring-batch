package mx.com.app.demo.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class CustomerStepListener implements StepExecutionListener {

	@Override
	public void beforeStep(StepExecution stepExecution) {
		System.out.println("==");

	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		System.out.println("READ COUNT = " + stepExecution);
		return ExitStatus.COMPLETED;
	}

}
