package com.googlecode.rd.types;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

public class HeaderTest {
	
	@Test
	public void newlyCreatedHeaderHasCorrectName() {
		
		final Header header = new Header("name", "value");
		
		assertThat(header.getName(), is("name"));
		
	}
	
	@Test
	public void newlyCreatedHeaderHasCorrectValue() {
		
		final Header header = new Header("name", "value");
		
		assertThat(header.getValue(), is("value"));
		
	}
	
	@Test
	public void headerHasSensibleToString() {
		
		final Header header = new Header("name", "value");
		
		assertThat(header.toString(), is("name:value"));
		
	}
	
	@Test
	public void headerHashCodeIsTheSameForEqualHeaders() {
		
		final Header header1 = new Header("name", "value");
		final Header header2 = new Header("name", "value");
		
		assertThat(header1.hashCode(), equalTo(header2.hashCode()));
		
	}
	
	@Test
	public void headerHashCodeIsDifferentForDifferentName() {
		
		final Header header1 = new Header("name", "value");
		final Header header2 = new Header("different", "value");
		
		assertThat(header1.hashCode(), not(equalTo(header2.hashCode())));
		
	}
	
	@Test
	public void headerHashCodeIsDifferentForDifferentValue() {
		
		final Header header1 = new Header("name", "value");
		final Header header2 = new Header("name", "different");
		
		assertThat(header1.hashCode(), not(equalTo(header2.hashCode())));
		
	}
	
	@Test
	public void headerIsEqualToItself() {
		
		final Header header = new Header("name", "value");
		
		assertThat(header, equalTo(header));
		
	}
	
	@Test
	public void headerIsNotEqualToObjectOfAnotherType() {
		
		final Header header = new Header("name", "value");
		
		assertThat(header.equals(""), is(false));
		
	}

	@Test
	public void headersAreEqualWithSameNameAndValue() {

		final Header header1 = new Header("name", "value");
		final Header header2 = new Header("name", "value");

		assertThat(header1, equalTo(header2));

	}

	@Test
	public void headersAreNotEqualWithDifferentName() {

		final Header header1 = new Header("name", "value");
		final Header header2 = new Header("different", "value");

		assertThat(header1, not(equalTo(header2)));

	}
	
	@Test
	public void headersAreNotEqualWithDifferentValue() {

		final Header header1 = new Header("name", "value");
		final Header header2 = new Header("name", "different");

		assertThat(header1, not(equalTo(header2)));

	}
	
	@Test
	public void headerWithNullNameAndValueHasHashCode() {

		final Header header = new Header(null, null);

		assertThat(header.hashCode() > 0, is(true));

	}

}
