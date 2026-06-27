package org.groupf.annotation;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.EventType;
import org.hibernate.generator.GeneratorCreationContext;
import org.hibernate.query.sql.spi.NativeQueryImplementor;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Member;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PrefixedIdGeneratorTest {

    @Test
    void generate_shouldReturnPrefixedFormattedId() {
        PrefixedId config = mock(PrefixedId.class);

        when(config.prefix()).thenReturn("product_");
        when(config.sequenceName()).thenReturn("product_seq");
        when(config.numberLength()).thenReturn(4);

        Member member = mock(Member.class);
        GeneratorCreationContext context = mock(GeneratorCreationContext.class);

        PrefixedIdGenerator generator = new PrefixedIdGenerator();
        generator.initialize(config, member, context);

        SharedSessionContractImplementor session =
                mock(SharedSessionContractImplementor.class);

        @SuppressWarnings({"rawtypes", "unchecked"})
        NativeQueryImplementor nativeQuery =
                mock(NativeQueryImplementor.class);

        when(session.createNativeQuery("SELECT nextval('product_seq')"))
                .thenReturn(nativeQuery);

        when(nativeQuery.getSingleResult())
                .thenReturn(7L);

        Object result = generator.generate(
                session,
                new Object(),
                null,
                EventType.INSERT
        );

        assertEquals("product_0007", result);
    }

    @Test
    void getEventTypes_shouldReturnInsertOnly() {
        PrefixedIdGenerator generator = new PrefixedIdGenerator();

        EnumSet<EventType> eventTypes = generator.getEventTypes();

        assertEquals(EnumSet.of(EventType.INSERT), eventTypes);
    }

}