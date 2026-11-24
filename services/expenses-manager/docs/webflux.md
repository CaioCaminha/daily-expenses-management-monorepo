# WebFlux Implementation Decisions

### Functional Endpoints

It was decided to use functional endpoints over standard Annotated Controllers (@Controller/@RestController) basically based on the 
idea that annotated controllers add an overhead layer due to component scan and reflection for RequestMappingHandlerMapping/RequestMappingHandlerAdapter
but there's no guarantee that functional endpoints will provide significant performance gain at runtime since annotation discovery happens at startup rather 
than at runtime, it doesn't happen 'per request'. That's something to be evaluated still.


## Transactions Endpoints

### multipart/form-data content type
The idea of this controller is to accept multiple formats of statements;

Multipart file uploads trigger PartEvents, containing the filename, and if the file is large enough, the first FilePartEvent will be
followed by subsequent events until PartEvent::last

The [PartEvent javadoc](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/codec/multipart/PartEvent.html)  recommends the following pattern
to consume a PartEvent, coming form WebFlux.

```text
Flux<PartEvent> allPartsEvents = ... // obtained via @RequestPayload or request.bodyToFlux(PartEvent.class)
allPartsEvents.windowUntil(PartEvent::isLast)
  .concatMap(p -> p.switchOnFirst((signal, partEvents) -> {
      if (signal.hasValue()) {
          PartEvent event = signal.get();
          if (event instanceof FormPartEvent formEvent) {
              String value = formEvent.value();
              // handle form field
          }
          else if (event instanceof FilePartEvent fileEvent) {
              String filename = fileEvent.filename();
              Flux<DataBuffer> contents = partEvents.map(PartEvent::content);
              // handle file upload
          }
          else {
              return Mono.error(new RuntimeException("Unexpected event: " + event));
          }
      }
      else {
        return partEvents; // either complete or error signal
      }
  }))
```

1. Flux.windowUntil(PartEvent::isLast): It will return a Flux<Flux<PartEvent>>, grouping PartEvents using isLast function, this is a guarantee that Flux<PartEvent> represents a single file uploaded;
2. .concatMap(...): Subscribes to each element in order, ensuring that element 1 is emitted before element 2 starts, ensuring sequential subscription, very important for this usecase (DataBuffer collection is order sensitive);
3. .switchOnFirst(...): Lets you inspect the first signal (usually the first element emitted by the source Flux) before deciding how the remaining Flux will be consumed, very useful when the way you process a stream depends on what the first element is. In our usecase we need to decide if it's an instance of FormPartEvent or FilePartEvent, this checking will decide how to consume the rest of the Flux;

Going deeper on the implementation, and covering the "// handle file upload" comment section:
```java
        return Flux.create(sink -> {
            PipedInputStream inputStream = new PipedInputStream(64 * 1024);
            PipedOutputStream outputStream;

            try {
                outputStream = new PipedOutputStream(inputStream);
            } catch (IOException e) {
                sink.error(e);
                return;
            }

            DataBufferUtils.write(contents, outputStream)
                    .doOnError(sink::error)
                    .doFinally( signalType -> {
                        try{ outputStream.close();} catch (IOException ignored) {}
                    })
                    .subscribe(DataBufferUtils.releaseConsumer());

            Schedulers.boundedElastic().schedule( () -> {
               try(InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                   CsvParserSettings settings = new CsvParserSettings();
                   settings.setNumberOfRowsToSkip(4);
                   settings.setHeaderExtractionEnabled(true);
                   settings.setDelimiterDetectionEnabled(true, ';', ',');
                   settings.setLineSeparatorDetectionEnabled(true);

                   CsvParser parser = new CsvParser(settings);

                   ResultIterator<Record, ParsingContext> iterator = parser.iterateRecords(reader).iterator();
```

The goal is to avoid loading all the file in memory at once (Using DataBufferUtils.join(dataBuffers)), creating a streaming of data:
1. Writing Flux<DataBuffer> contents into a PipedOutputStream with DataBufferUtils.write(contents, outputStream), note the .subscribe(DataBufferUtils.releaseConsumer())
   2. DataBuffer can be of type PooledDataBuffer which is an extension of DataBuffer that allows for buffers to share a memory pool. This approach is followed by Netty, [due to reference counting instead of relying on GC.](https://github.com/netty/netty/wiki/Reference-counted-objects#troubleshooting-buffer-leaks)
   3. This reference-counting approach delegates the responsibility of destroying to the consumer, that's why is important to call DataBufferUtils::release in order to avoid memory leaks;
4. The reading of PipedInputStream is done on a different thread by using Schedulers.boundedElastic().schedule() (Piped prefix means that writing and reading happens on different threads).
5. It's using [Univocity CSV Parser](https://www.baeldung.com/java-univocity-parsers) 

