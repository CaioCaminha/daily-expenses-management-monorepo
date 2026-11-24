The following error was happening:
```text
java.lang.IllegalArgumentException: Cannot encode parameter of type io.r2dbc.spi.Parameters$InParameter (MARKET) at io.r2dbc.postgresql.codec.DefaultCodecs.encodeParameterValue(DefaultCodecs.java:294) ~[r2dbc-postgresql-1.0.7.RELEASE.jar:1.0.7.RELEASE]
```
the error was happening on 
```java
    .bind("category", transactionDetails.category())
```
the issue was that r2dbc-postgres driver was not able to encode Category.MARKET which is an Enum

When decoding is also important to read the RowSpect as String::class 
and then use Category.valueOf()
```java
    Category.valueOf(row.get("category", String.class)),
```