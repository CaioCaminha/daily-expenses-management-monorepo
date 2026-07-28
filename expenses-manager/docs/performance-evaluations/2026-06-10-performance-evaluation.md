# Performance Testing performed June 10 2026

## K6 Script used to perform load testing

```js
import http from 'k6/http';
import { sleep, check } from 'k6';
import { Trend, Rate, Counter } from 'k6/metrics';
import { FormData } from 'https://jslib.k6.io/formdata/0.0.2/index.js';

// Custom metrics
const responseTrend = new Trend('response_time');
const errorRate = new Rate('error_rate');
const requestCounter = new Counter('total_requests');

export const options = {
    stages: [
        { duration: '30s', target: 85 },   // ramp up to 25 VUs
        { duration: '1m',  target: 85 },   // hold 25 VUs - baseline
        { duration: '30s', target: 200 },  // ramp up to 100 VUs
        { duration: '2m',  target: 300 },  // hold 100 VUs - stress
        { duration: '30s', target: 400 },  // ramp up to 200 VUs - peak
        { duration: '1m',  target: 400 },  // hold peak
        { duration: '30s', target: 0 },    // ramp down
    ],
    thresholds: {
        // 95% of requests must complete under 2s
        http_req_duration: ['p(95)<2000'],
        // 99% under 5s
        'http_req_duration': ['p(99)<5000'],
        // Error rate must stay below 1%
        error_rate: ['rate<0.01'],
    },
};

const csvFile = open('statement.csv');

export default function () {
    const url = 'http://localhost:8089/v1/statement';
    const fd = new FormData();

    fd.append('csv', http.file(csvFile, 'statement.csv', 'text/csv'));

    const res = http.post(url,
        fd.body(),
        {
            headers: { 'Content-Type': 'multipart/form-data; boundary=' + fd.boundary },
            timeout: '30s',
        }
    );

    // Track custom metrics
    responseTrend.add(res.timings.duration);
    requestCounter.add(1);

    const success = check(res, {
        'is status 201': (r) => r.status === 201,
        'response time < 4s': (r) => r.timings.duration < 4000,
    });

    errorRate.add(!success);

    // Realistic think time between requests (0.5s to 1.5s)
    sleep(Math.random() * 1 + 0.5);
}
```

Ramping from 85 VUs to 400 VUs concurrent users.

Idea is to evaluate how many incoming requests can be handled and measure performance degradation as concurrency increases.

## Current r2dbc pool configuration
```java
    @Override
    @Bean
    public ConnectionPool connectionFactory() {
        log.info("Initializing ConnectionFactory with Connection Pool");
        Map<String, String> options = new HashMap<>();
        options.put("lock_timeout", "10s");
        options.put("transaction_timeout", "40s"); // todo review if its a good timeout 20s
        options.put("idle_in_transaction_session_timeout", "120s");
        options.put("tcp_keepalives_idle", "300s");
        options.put("tcp_keepalives_interval", "5s");
        options.put("client_connection_check_interval", "120s");

        ConnectionFactory connectionFactory =  new PostgresqlConnectionFactory(
                PostgresqlConnectionConfiguration
                        .builder()
                        .username(properties.user)
                        .password(properties.password)
                        .database(properties.name)
                        .host(properties.server.host)
                        .port(properties.server.port)
                        .options(options)
                        .build()
        );

        ConnectionPoolConfiguration poolConfiguration = ConnectionPoolConfiguration.builder()
                .connectionFactory(connectionFactory)
                .initialSize(16)
                .maxSize(32)
                .maxIdleTime(Duration.ofDays(1))
                .maxLifeTime(Duration.ofDays(3))
                .maxAcquireTime(Duration.ofSeconds(10))
                .build();

        return new ConnectionPool(poolConfiguration);
    }
```

Based on this article from HikariCP about connection pool sizing, it was configured max amount based on current PC 16 available cores, therefore 32 open TCP connections.
Even though the application only has access to 1 available CPU core it was a good result:

## Result

```text
  █ THRESHOLDS

    error_rate
    ✓ 'rate<0.01' rate=0.82%

    http_req_duration
    ✓ 'p(99)<5000' p(99)=3.97s


  █ TOTAL RESULTS

    checks_total.......: 65168  180.498904/s
    checks_succeeded...: 99.58% 64898 out of 65168
    checks_failed......: 0.41%  270 out of 65168

    ✓ is status 201
    ✗ response time < 4s
      ↳  99% — ✓ 32314 / ✗ 270

    CUSTOM
    error_rate.....................: 0.82% 270 out of 32584
    response_time..................: avg=1501.780446 min=4.902159 med=1381.301728 max=4276.958392 p(90)=3209.67997 p(95)=3495.356798
    total_requests.................: 32584 90.249452/s

    HTTP
    http_req_duration..............: avg=1.5s        min=4.9ms    med=1.38s       max=4.27s       p(90)=3.2s       p(95)=3.49s
      { expected_response:true }...: avg=1.5s        min=4.9ms    med=1.38s       max=4.27s       p(90)=3.2s       p(95)=3.49s
    http_req_failed................: 0.00% 0 out of 32584
    http_reqs......................: 32584 90.249452/s

    EXECUTION
    iteration_duration.............: avg=2.5s        min=508.89ms med=2.41s       max=5.72s       p(90)=4.31s      p(95)=4.6s
    iterations.....................: 32584 90.249452/s
    vus............................: 1     min=1            max=400
    vus_max........................: 400   min=400          max=400

    NETWORK
    data_received..................: 95 MB 264 kB/s
    data_sent......................: 41 MB 115 kB/s




running (6m01.0s), 000/400 VUs, 32584 complete and 0 interrupted iterations 
```

## Conclusion
Still has room for improvement, some of the requests had over 4 seconds of response time.

Important: Not sure if current virtual-threads setup is correct, even though 400 concurrent users are accessing the application a small set of threads are created, seems like a pool of threads
which would be default behavior for spring, not sure if virtual threads are actually being created.