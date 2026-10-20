package com.portfolio.financecontrol.application.port.out;

import com.portfolio.financecontrol.domain.event.DomainEvent;

public interface EventPublisherPort {

    void publish(DomainEvent event);
}
