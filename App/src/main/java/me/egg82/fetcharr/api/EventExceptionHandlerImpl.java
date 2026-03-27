package me.egg82.fetcharr.api;

import com.sasorio.event.EventSubscription;
import com.sasorio.event.bus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventExceptionHandlerImpl implements EventBus.EventExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public EventExceptionHandlerImpl() { }

    @Override
    public <E> void eventExceptionCaught(EventBus<? super E> bus, EventSubscription<? super E> subscription, E event, Throwable throwable) {
        logger.error("Exception in event bus for event {} at subscriber {}", subscription.event(), subscription.subscriber(), throwable);
    }
}
