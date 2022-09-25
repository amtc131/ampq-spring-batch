package mx.com.app.demo.customer;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import mx.com.app.demo.model.Customer;

public class CustomerFieldSetMapper implements FieldSetMapper<Customer> {

	@Override
	public Customer mapFieldSet(FieldSet fieldSet) throws BindException {
		Customer customer = new Customer();

		customer.setId(fieldSet.readLong("id"));
		customer.setFirstName(fieldSet.readRawString("firstName"));
		customer.setLastName(fieldSet.readRawString("lastName"));
		customer.setBirthdate(fieldSet.readRawString("birthdate"));

		System.out.println( customer.toString() );
		
		return customer;
	}

}
