package com.caiocaminha.expensesmanager.core.application.gateway.controller.studies;


import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Encoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.util.MimeType;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

public class TestEconder implements Encoder<String> {

    @Override
    public boolean canEncode(ResolvableType elementType, MimeType mimeType) {
        return true;
    }

    @Override
    public Flux<DataBuffer> encode(Publisher<? extends String> inputStream, DataBufferFactory bufferFactory, ResolvableType elementType, MimeType mimeType, Map<String, Object> hints) {
        DataBuffer buffer =  bufferFactory.allocateBuffer();

        inputStream.subscribe(new BaseSubscriber<String>() {
            @Override
            protected void hookOnNext(String value) {
                super.hookOnNext(value);
                bufferFactory.allocateBuffer(value.length());
            }

            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                super.hookOnSubscribe(subscription);
                super.request(Integer.MAX_VALUE);
            }
        });
        bufferFactory.allocateBuffer();
        return null;
    }

    @Override
    public List<MimeType> getEncodableMimeTypes() {
        return List.of();
    }
}
