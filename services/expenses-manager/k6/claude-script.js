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
        { duration: '30s', target: 25 },   // ramp up to 25 VUs
        { duration: '1m',  target: 25 },   // hold 25 VUs - baseline
        { duration: '30s', target: 30 },  // ramp up to 100 VUs
        { duration: '2m',  target: 30 },  // hold 100 VUs - stress
        { duration: '30s', target: 50 },  // ramp up to 200 VUs - peak
        { duration: '1m',  target: 50 },  // hold peak
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