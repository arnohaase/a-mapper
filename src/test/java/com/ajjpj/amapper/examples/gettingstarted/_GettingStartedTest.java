package com.ajjpj.amapper.examples.gettingstarted;

import com.ajjpj.amapper.javabean2.JavaBeanMapper;
import com.ajjpj.amapper.javabean2.builder.JavaBeanMapperBuilder;
import com.ajjpj.amapper.javabean2.builder.JavaBeanMapping;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 * @author arno
 */
public class _GettingStartedTest {
    @Test
    public void gettingStartedExample() throws Exception {
        // initialize the mapper. This is typically done once during start-up
        final JavaBeanMapper mapper = JavaBeanMapperBuilder.create() // create a builder
                .withBeanMapping(JavaBeanMapping.create(Person.class, PersonTO.class) // register a bean mapping for the Person... classes
                        .withMatchingPropsMappings() // start by wiring properties with corresponding names
                        .addMapping("lastName", String.class, "surName", String.class) // wire Person.lastName to PersonTO.surName
                        .addOneWayMapping("firstName + ' ' + middleName + ' ' + lastName", String.class, "fullName", String.class) // wire 'fullName' to a more complex expression
                )
                .withBeanMapping(JavaBeanMapping.create(Address.class, AddressTO.class) // ... and do the same for the Address... classes
                        .withMatchingPropsMappings()
                )
                .build(); // finally build the actual mapper from the builder

        // initialize a person. This could come from a database or whatever
        final Person p = new Person();
        p.setFirstName("Red");
        p.setMiddleName("X.");
        p.setLastName("Blackbeard");
        p.setNumChildren(13);
        p.getAddress().setStreet("Somewhere");
        p.getAddress().setCity("The Sea");

        // call the mapper to transform our person into a corresponding TO instance
        final PersonTO personTO = mapper.map(p, PersonTO.class);

        // verify that all properties were actually mapped
        assertEquals("Red", personTO.getFirstName());
        assertEquals("Blackbeard", personTO.getSurName());
        assertEquals("Red X. Blackbeard", personTO.getFullName());
        assertEquals((short) 13, personTO.getNumChildren());
        assertEquals("Somewhere", personTO.getAddress().getStreet());
        assertEquals("The Sea", personTO.getAddress().getCity());

        // modify the TO - this could happen through a user action
        personTO.setFirstName("Reddy");

        // map back to an instance of Person
        mapper.map(personTO, p);

        // verify that the change was merged into the Person object
        assertEquals("Reddy", p.getFirstName());

        // verify that none of the other fields was changed.
        assertEquals("Blackbeard", p.getLastName());
        assertEquals("X.", p.getMiddleName());
        assertEquals(13, p.getNumChildren());
        assertEquals("Somewhere", p.getAddress().getStreet());
        assertEquals("The Sea", p.getAddress().getCity());
    }
}
