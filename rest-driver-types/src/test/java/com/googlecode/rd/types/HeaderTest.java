package com.googlecode.rd.types;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

public class HeaderTest {

	@Test
	public void headersAreEqualWithSameNameAndValue() {

		final Header header1 = new Header("name", "value");
		final Header header2 = new Header("name", "value");

		assertThat(header1, equalTo(header2));

	}

}
