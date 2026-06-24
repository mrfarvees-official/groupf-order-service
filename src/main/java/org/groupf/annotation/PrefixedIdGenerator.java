package org.groupf.annotation;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.AnnotationBasedGenerator;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;
import org.hibernate.generator.GeneratorCreationContext;

import java.lang.reflect.Member;
import java.util.EnumSet;

public class PrefixedIdGenerator implements BeforeExecutionGenerator, AnnotationBasedGenerator<PrefixedId> {

    private String prefix;
    private String sequenceName;
    private int numberLength;

    @Override
    public void initialize(
            PrefixedId config,
            Member member,
            GeneratorCreationContext context
    ) {
        this.prefix = config.prefix();
        this.sequenceName = config.sequenceName();
        this.numberLength = config.numberLength();
    }

    @Override
    public Object generate(
            SharedSessionContractImplementor session,
            Object owner,
            Object currentValue,
            EventType eventType
    ) {
        Number nextValue = (Number) session
                .createNativeQuery("SELECT nextval('" + sequenceName + "')")
                .getSingleResult();

        return prefix + String.format("%0" + numberLength + "d", nextValue.longValue());
    }

    @Override
    public EnumSet<EventType> getEventTypes() {
        return EnumSet.of(EventType.INSERT);
    }
}